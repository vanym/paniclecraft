package com.vanym.paniclecraft.client.utils;

import net.minecraft.util.IIcon;

public class IconUtils {
    
    public static IIcon flip(IIcon icon, boolean flipU, boolean flipV) {
        return new IconFlipped(icon, flipU, flipV);
    }
    
    public static IIcon sub(
            int x,
            int y,
            int iconWidth,
            int iconHeight,
            int totalWidth,
            int totalHeight) {
        return new IconSubRect(x, y, iconWidth, iconHeight, totalWidth, totalHeight);
    }
    
    public static IIcon full(int width, int height) {
        return sub(0, 0, width, height, width, height);
    }
}
