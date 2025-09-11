package com.vanym.paniclecraft.client.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.core.component.painting.Picture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class PictureRender {
    
    public static IIcon bindTexture(Picture picture) {
        TextureHolder texture = picture.getTexture();
        boolean newtexture = false;
        if (texture.isEmpty()) {
            texture.set(GL11.glGenTextures());
            newtexture = true;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.get());
        if (newtexture || !picture.imageChangeProcessed) {
            ByteBuffer textureBuffer = picture.getImageAsDirectByteBuffer();
            if (textureBuffer != null) {
                final int width = picture.getWidth();
                final int height = picture.getHeight();
                final int format = picture.hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB;
                textureBuffer.order(ByteOrder.nativeOrder());
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format,
                                  width, height, 0, format,
                                  GL11.GL_UNSIGNED_BYTE,
                                  textureBuffer);
            }
            picture.imageChangeProcessed = true;
        }
        return getIcon(picture);
    }
    
    public static IIcon bindTexture(Picture picture, int side) {
        IIcon icon = bindTexture(picture);
        switch (side) {
            case 0:
                icon = IconUtils.flip(icon, true, false);
            break;
            case 1:
                icon = IconUtils.flip(icon, true, true);
            break;
        }
        return icon;
    }
    
    protected static IIcon getIcon(Picture picture) {
        return IconUtils.full(picture.getWidth(), picture.getHeight());
    }
}
