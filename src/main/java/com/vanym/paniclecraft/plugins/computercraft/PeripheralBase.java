package com.vanym.paniclecraft.plugins.computercraft;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class PeripheralBase implements IPeripheral {
    
    protected final Method[] methods;
    
    public PeripheralBase() {
        Class<? extends PeripheralBase> clazz = this.getClass();
        this.methods = Arrays.stream(clazz.getDeclaredMethods())
                             .filter(m->m.isAnnotationPresent(PeripheralMethod.class))
                             .sorted(Comparator.comparing(m->m.getAnnotation(PeripheralMethod.class)
                                                              .value()))
                             .toArray(Method[]::new);
    }
    
    @Override
    public final String[] getMethodNames() {
        return Arrays.stream(this.methods).map(Method::getName).toArray(String[]::new);
    }
    
    @Override
    public final Object[] callMethod(
            IComputerAccess computer,
            ILuaContext context,
            int methodIndex,
            Object[] arguments) throws LuaException, InterruptedException {
        try {
            Method method = this.methods[methodIndex];
            PeripheralMethod annotation = method.getAnnotation(PeripheralMethod.class);
            Object ret;
            if (annotation.rawArgs()) {
                ret = method.invoke(this, computer, context, arguments);
            } else {
                Parameter[] params = method.getParameters();
                List<Object> methodArgs = new ArrayList<>();
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
                if ((params.length - paramIndex) > arguments.length) {
                    throw new LuaException("too few arguments");
                }
                if ((params.length - paramIndex) < arguments.length) {
                    throw new LuaException("too many arguments");
                }
                for (int i = 0; i < arguments.length; ++i, ++paramIndex) {
                    Parameter param = params[paramIndex];
                    Object arg = arguments[i];
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
                ret = method.invoke(this, methodArgs.toArray());
            }
            if (ret == null || ret.getClass().isArray()) {
                return (Object[])ret;
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
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException)cause;
            } else {
                throw new LuaException(e.getMessage());
            }
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface PeripheralMethod {
        int value();
        
        boolean rawArgs() default false;
    }
}
