package com.vanym.paniclecraft.client;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
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
    
    @Override
    protected SimpleTexture.TextureData func_215246_b(IResourceManager resourceManager) {
        SimpleTexture.TextureData data = super.func_215246_b(resourceManager);
        try {
            this.img = data.func_217800_b();
        } catch (IOException e) {
        }
        return TextureDataUncloseable.wrap(data);
    }
    
    public Color getColor(int x, int y) {
        if (this.img == null) {
            return null;
        }
        try {
            return new Color(ColorUtils.swapRB(this.img.getPixelRGBA(x, y)), true);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    protected static class TextureDataUncloseable extends SimpleTexture.TextureData {
        
        protected TextureDataUncloseable(IOException e) {
            super(e);
        }
        
        protected TextureDataUncloseable(@Nullable TextureMetadataSection meta, NativeImage img) {
            super(meta, img);
        }
        
        @Override
        public void close() {
            // do nothing
        }
        
        public static TextureDataUncloseable wrap(SimpleTexture.TextureData data) {
            try {
                return new TextureDataUncloseable(data.func_217798_a(), data.func_217800_b());
            } catch (IOException e) {
                return new TextureDataUncloseable(e);
            }
        }
    }
}
