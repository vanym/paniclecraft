package com.vanym.paniclecraft.utils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JUtils {
    
    public static <T> T make(Supplier<T> sup) {
        return sup.get();
    }
    
    public static <T> T peek(T obj, Consumer<T> action) {
        return peek(obj, action);
    }
    
    public static <T> T peek(Consumer<T> action, T obj) {
        action.accept(obj);
        return obj;
    }
    
    public static <T> Function<T, T> peek(Consumer<T> action) {
        return obj-> {
            action.accept(obj);
            return obj;
        };
    }
    
    public static <T> T trap(Callable<T> sup) {
        return trap(sup, ()->null);
    }
    
    public static <T> T trap(Callable<T> sup, Supplier<T> orElse) {
        return trap(sup, (e)->orElse.get());
    }
    
    public static <T> T trap(Callable<T> sup, Function<Throwable, T> orElse) {
        try {
            return sup.call();
        } catch (Throwable e) {
            return orElse.apply(e);
        }
    }
    
    public static boolean trap(ThrowableRunnable action) {
        return trap(()-> {
            action.run();
            return true;
        }, ()->false);
    }
    
    public static void runIf(boolean doRun, Runnable action) {
        if (doRun) {
            action.run();
        }
    }
    
    public static <T> T callIf(boolean doCall, Callable<T> action) {
        if (doCall) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
    public static <T> T orElse(T value, T def) {
        return value != null ? value : def;
    }
    
    public static <T> T orElseGet(T value, Supplier<T> getter) {
        return value != null ? value : getter.get();
    }
    
    @SafeVarargs
    public static <T> T takeFirst(Supplier<T>... sups) {
        for (Supplier<T> sup : sups) {
            T value = sup.get();
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    @SafeVarargs
    public static <T> Optional<T> takeFirstOptional(Supplier<Optional<T>>... sups) {
        for (Supplier<Optional<T>> sup : sups) {
            Optional<T> value = sup.get();
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }
    
    @FunctionalInterface
    public static interface ThrowableRunnable {
        void run() throws Exception;
    }
}
