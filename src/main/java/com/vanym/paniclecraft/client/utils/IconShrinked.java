package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IconShrinked extends TextureAtlasSprite {
    
    public IconShrinked(TextureAtlasSprite icon) {
        super(new ResourceLocation(
                icon.getName().getNamespace(),
                icon.getName().getPath() + "_shrinked"),
              icon.getWidth(), icon.getHeight());
        this.minU = icon.getMinU();
        this.maxU = icon.getMaxU();
        this.minV = icon.getMinV();
        this.maxV = icon.getMaxV();
        float totalWidth = this.width / (this.maxU - this.minU);
        float totalHeight = this.height / (this.maxV - this.minV);
        float offsetU = (float)(0.01D / totalWidth);
        float offsetV = (float)(0.01D / totalHeight);
        this.minU += offsetU;
        this.maxU -= offsetU;
        this.minV += offsetV;
        this.maxV -= offsetV;
    }
}
