package com.vanym.paniclecraft.plugins.computercraft;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.core.asm.TaskCallback;

public abstract class PeripheralBase implements IDynamicPeripheral {
    
    protected final Method[] methods;
    
    public PeripheralBase() {
        Class<?> current = this.getClass();
        Stream<Method> stream = Stream.empty();
        while (current != PeripheralBase.class) {
            stream = Stream.concat(stream, Arrays.stream(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }
        Comparator<Method> comparatorOrder =
                Comparator.comparing(m->m.getAnnotation(PeripheralMethod.class).value());
        Comparator<Method> comparatorName = Comparator.comparing(m->m.getName());
        Comparator<Method> comparator = comparatorOrder.thenComparing(comparatorName);
        this.methods = stream.filter(m->m.isAnnotationPresent(PeripheralMethod.class))
                             .sorted(comparator)
                             .toArray(Method[]::new);
    }
    
    @Override
    public final String[] getMethodNames() {
        return Arrays.stream(this.methods).map(Method::getName).toArray(String[]::new);
    }
    
    @Override
    public final MethodResult callMethod(
            IComputerAccess computer,
            ILuaContext context,
            int methodIndex,
            IArguments arguments) throws LuaException {
        try {
            Method method = this.methods[methodIndex];
            PeripheralMethod annotation = method.getAnnotation(PeripheralMethod.class);
            List<Object> methodArgs = new ArrayList<>();
            if (annotation.rawArgs()) {
                Stream.of(computer, context, arguments)
                      .forEachOrdered(methodArgs::add);
            } else {
                Parameter[] params = method.getParameters();
                int paramIndex = 0;
                if (params.length > paramIndex
                    && params[paramIndex].getType().isAssignableFrom(IComputerAccess.class)) {
                    ++paramIndex;
                    methodArgs.add(computer);
                }
                if (params.length > paramIndex
                    && params[paramIndex].getType().isAssignableFrom(ILuaContext.class)) {
                    ++paramIndex;
                    methodArgs.add(context);
                }
                if ((params.length - paramIndex) > arguments.count()) {
                    throw new LuaException("too few arguments");
                }
                if ((params.length - paramIndex) < arguments.count()) {
                    throw new LuaException("too many arguments");
                }
                for (int i = 0; i < arguments.count(); ++i, ++paramIndex) {
                    Parameter param = params[paramIndex];
                    Object arg = arguments.get(i);
                    Class<?> clazz = param.getType();
                    
                    try {
                        if (clazz == Boolean.TYPE || clazz == Boolean.class) {
                            methodArgs.add(((Boolean)arg).booleanValue());
                        }
                    } catch (ClassCastException | NullPointerException e) {
                        throw new LuaException(
                                String.format("argument %d must be a boolean", i + 1));
                    }
                    try {
                        if (clazz == Byte.TYPE || clazz == Byte.class) {
                            methodArgs.add(((Double)arg).byteValue());
                        } else if (clazz == Short.TYPE || clazz == Short.class) {
                            methodArgs.add(((Double)arg).shortValue());
                        } else if (clazz == Integer.TYPE || clazz == Integer.class) {
                            methodArgs.add(((Double)arg).intValue());
                        } else if (clazz == Long.TYPE || clazz == Long.class) {
                            methodArgs.add(((Double)arg).longValue());
                        } else if (clazz == Float.TYPE || clazz == Float.class) {
                            methodArgs.add(((Double)arg).floatValue());
                        } else if (clazz == Double.TYPE || clazz == Double.class) {
                            methodArgs.add(((Double)arg).doubleValue());
                        }
                    } catch (ClassCastException | NullPointerException e) {
                        throw new LuaException(
                                String.format("argument %d must be a number", i + 1));
                    }
                    try {
                        if (clazz == String.class) {
                            methodArgs.add((String)arg);
                        }
                    } catch (ClassCastException | NullPointerException e) {
                        throw new LuaException(
                                String.format("argument %d must be a string", i + 1));
                    }
                    if (methodArgs.size() == i) {
                        methodArgs.add(arg);
                    }
                }
            }
            if (annotation.mainThread()) {
                // TODO: Do not use non-api class TaskCallback
                MethodResult rets = TaskCallback.make(context, ()-> {
                    return this.execute(method, methodArgs.toArray());
                });
                return rets;
            } else {
                return MethodResult.of(this.execute(method, methodArgs.toArray()));
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new LuaException(e.getMessage());
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface PeripheralMethod {
        int value();
        
        boolean rawArgs() default false;
        
        boolean mainThread() default false;
    }
    
    private Object[] execute(Method method, Object[] args) throws LuaException {
        try {
            Object ret = method.invoke(this, args);
            if (method.getReturnType() == Void.TYPE) {
                return null;
            }
            if (ret != null && ret.getClass().isArray()) {
                if (ret.getClass().getComponentType().isPrimitive()) {
                    int len = Array.getLength(ret);
                    Object[] arr = new Object[len];
                    for (int i = 0; i < len; ++i) {
                        arr[i] = Array.get(ret, i);
                    }
                    return arr;
                } else {
                    return (Object[])ret;
                }
            } else {
                return new Object[]{ret};
            }
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | ClassCastException e) {
            throw new LuaException(e.getMessage());
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof LuaException) {
                throw (LuaException)cause;
            } else {
                throw new LuaException(e.getMessage());
            }
        }
    }
    
    public static <V> Map<Integer, V> toLuaArray(Iterable<V> iterable) {
        Iterator<V> it = iterable.iterator();
        Map<Integer, V> map = new TreeMap<>();
        for (int i = 1; it.hasNext(); ++i) {
            map.put(i, it.next());
        }
        return map;
    }
}
