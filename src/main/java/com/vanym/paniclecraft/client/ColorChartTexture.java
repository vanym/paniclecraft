package com.vanym.paniclecraft.client;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorChartTexture extends SimpleTexture {
    
    protected NativeImage img;
    
    public ColorChartTexture(ResourceLocation textureLocation) {
        super(textureLocation);
    }
    
    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }
    
    @Override
    protected SimpleTexture.TextureData func_215246_b(IResourceManager resourceManager) {
        SimpleTexture.TextureData data = super.func_215246_b(resourceManager);
        try {
            this.img = data.func_217800_b();
        } catch (IOException e) {
        }
        return data;
    }
    
    public Color getColor(int x, int y) {
        if (this.img == null) {
            return null;
        }
        try {
            return new Color(this.img.getPixelRGBA(x, y), true);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
