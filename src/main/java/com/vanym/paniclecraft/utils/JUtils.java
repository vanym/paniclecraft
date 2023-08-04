package com.vanym.paniclecraft.utils;

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
}
