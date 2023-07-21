package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IconShrinked extends TextureAtlasSprite {
    
    public IconShrinked(TextureAtlasSprite icon) {
        super(icon.getIconName() + "Shrinked");
        this.copyFrom(icon);
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
