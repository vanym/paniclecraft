package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IconShrinked extends TextureAtlasSprite {
    
    public IconShrinked(TextureAtlasSprite icon) {
        super(icon.atlas(),
              new TextureAtlasSprite.Info(
                      new ResourceLocation(
                              icon.getName().getNamespace(),
                              icon.getName().getPath() + "_shrinked"),
                      icon.getWidth(),
                      icon.getHeight(),
                      AnimationMetadataSection.EMPTY),
              0, 1, 1, 1, 1, new NativeImage(0, 0, false));
        this.u0 = icon.getU0();
        this.u1 = icon.getU1();
        this.v0 = icon.getV0();
        this.v1 = icon.getV1();
        float totalWidth = this.getWidth() / (this.u1 - this.u0);
        float totalHeight = this.getHeight() / (this.v1 - this.v0);
        float offsetU = (float)(0.01D / totalWidth);
        float offsetV = (float)(0.01D / totalHeight);
        this.u0 += offsetU;
        this.u1 -= offsetU;
        this.v0 += offsetV;
        this.v1 -= offsetV;
    }
}
