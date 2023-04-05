package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IconUtils {
    
    public static TextureAtlasSprite sub(
            int x,
            int y,
            int iconWidth,
            int iconHeight,
            int totalWidth,
            int totalHeight) {
        return new IconSubRect(x, y, iconWidth, iconHeight, totalWidth, totalHeight);
    }
    
    public static TextureAtlasSprite full(int width, int height) {
        return sub(0, 0, width, height, width, height);
    }
}
