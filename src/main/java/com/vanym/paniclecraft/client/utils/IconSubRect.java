package com.vanym.paniclecraft.client.utils;

import com.vanym.paniclecraft.DEF;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IconSubRect extends TextureAtlasSprite {
    
    public IconSubRect(
            int x,
            int y,
            int iconWidth,
            int iconHeight,
            int totalWidth,
            int totalHeight) {
        super(new ResourceLocation(
                DEF.MOD_ID,
                String.format("sub_x%dy%dw%dh%dtw%dth%d", x, y,
                              iconWidth, iconHeight, totalWidth, totalHeight)),
              iconWidth, iconHeight);
        this.func_217789_a(totalWidth, totalHeight, x, y);
    }
}
