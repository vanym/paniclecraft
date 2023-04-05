package com.vanym.paniclecraft.client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorChartTexture extends AbstractTexture {
    
    public final ResourceLocation textureLocation;
    
    protected BufferedImage img;
    
    public ColorChartTexture(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }
    
    @Override
    public void loadTexture(IResourceManager manager) throws IOException {
        this.deleteGlTexture();
        InputStream inputstream = null;
        
        try {
            IResource iresource = manager.getResource(this.textureLocation);
            inputstream = iresource.getInputStream();
            this.img = ImageIO.read(inputstream);
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), this.img, false, false);
        } finally {
            if (inputstream != null) {
                inputstream.close();
            }
        }
    }
    
    public Color getColor(int x, int y) {
        if (this.img == null) {
            return null;
        }
        try {
            return new Color(this.img.getRGB(x, y), true);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
