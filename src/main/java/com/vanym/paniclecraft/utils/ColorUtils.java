package com.vanym.paniclecraft.utils;

import java.awt.Color;

public class ColorUtils {
    
    public static Color addColor(Color b, Color s) {
        // https://www.w3.org/TR/compositing-1/#porterduffcompositingoperators_srcover
        float[] Cs = s.getRGBComponents(null);
        float[] Cb = b.getRGBComponents(null);
        float[] co = new float[3];
        float as = Cs[3];
        float ab = Cb[3];
        float Fb = (1 - as);
        for (int i = 0; i < co.length; ++i) {
            co[i] = as * Cs[i] + ab * Cb[i] * Fb;
        }
        float ao = as + ab * Fb;
        return new Color(co[0], co[1], co[2], ao);
    }
    
    public static int getAlphaless(Color color) {
        if (color == null) {
            return 0;
        }
        return color.getRGB() & 0xffffff;
    }
    
    public static int swapRB(int color) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color >> 0 & 0xFF;
        return a << 24 | b << 16 | g << 8 | r << 0;
    }
}
