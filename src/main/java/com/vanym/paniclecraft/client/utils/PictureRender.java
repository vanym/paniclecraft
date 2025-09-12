package com.vanym.paniclecraft.client.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.client.renderer.TextureHolder;
import com.vanym.paniclecraft.core.component.painting.Picture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PictureRender {
    
    public static TextureAtlasSprite bindTexture(Picture picture) {
        TextureHolder texture = picture.getTexture();
        boolean newtexture = false;
        if (texture.isEmpty()) {
            texture.set(GlStateManager.genTexture());
            newtexture = true;
        }
        GlStateManager.bindTexture(texture.get());
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
                GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format,
                                  width, height, 0, format,
                                  GL11.GL_UNSIGNED_BYTE,
                                  textureBuffer);
            }
            picture.imageChangeProcessed = true;
        }
        return getIcon(picture);
    }
    
    protected static TextureAtlasSprite getIcon(Picture picture) {
        return IconUtils.full(picture.getWidth(), picture.getHeight());
    }
}
