package com.vanym.paniclecraft.utils;

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
    
    public static <T> T trap(Callable<T> sup, Function<Exception, T> orElse) {
        try {
            return sup.call();
        } catch (Exception e) {
            return orElse.apply(e);
        }
    }
    
    public static void runIf(boolean doRun, Runnable action) {
        if (doRun) {
            action.run();
        }
    }
}
