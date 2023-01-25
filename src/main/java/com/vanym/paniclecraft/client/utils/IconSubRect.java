package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconSubRect extends TextureAtlasSprite {
    
    public IconSubRect(
            int x,
            int y,
            int iconWidth,
            int iconHeight,
            int totalWidth,
            int totalHeight) {
        super(String.format("Sub[x=%d, y=%d, w=%d, h=%d, tw=%d, th=%d]", x, y,
                            iconWidth, iconHeight, totalWidth, totalHeight));
        this.setIconWidth(iconWidth);
        this.setIconHeight(iconHeight);
        this.initSprite(totalWidth, totalHeight, x, y, false);
    }
}
