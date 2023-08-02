package com.vanym.paniclecraft.utils;

public class NumberUtils {
    
    public static float finite(float num) {
        return Float.isFinite(num) ? num : 0.0F;
    }
    
    public static double finite(double num) {
        return Double.isFinite(num) ? num : 0.0D;
    }
}
