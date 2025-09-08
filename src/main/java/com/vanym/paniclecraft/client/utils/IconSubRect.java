package com.vanym.paniclecraft.client.utils;

import com.vanym.paniclecraft.DEF;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
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
        super(new AtlasTexture(
                new ResourceLocation(
                        DEF.MOD_ID,
                        String.format("sub_x%dy%dw%dh%dtw%dth%d", x, y,
                                      iconWidth, iconHeight, totalWidth, totalHeight))),
              new TextureAtlasSprite.Info(
                      new ResourceLocation(
                              DEF.MOD_ID,
                              String.format("sub_x%dy%dw%dh%dtw%dth%d", x, y,
                                            iconWidth, iconHeight, totalWidth, totalHeight)),
                      iconWidth,
                      iconHeight,
                      AnimationMetadataSection.EMPTY),
              0,
              totalWidth,
              totalHeight,
              x,
              y,
              new NativeImage(0, 0, false));
    }
}
