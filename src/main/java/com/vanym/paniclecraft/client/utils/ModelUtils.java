package com.vanym.paniclecraft.client.utils;

import java.util.Arrays;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtils {
    
    public static BakedQuad tintless(BakedQuad quad) {
        return new BakedQuad(
                quad.getVertices(),
                -1,
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade());
    }
    
    public static BakedQuad retexture(
            BakedQuad quad,
            TextureAtlasSprite texture,
            VertexFormat format) {
        int[] vertices = Arrays.copyOf(quad.getVertices(), quad.getVertices().length);
        TextureAtlasSprite oldTexture = quad.getSprite();
        int vsize = format.getIntegerSize();
        int uvIndex = getByteOffset(format, DefaultVertexFormats.ELEMENT_UV0) / 4;
        for (int offset = 0; offset < vertices.length; offset += vsize) {
            int index = offset + uvIndex;
            float oldU = Float.intBitsToFloat(vertices[index + 0]);
            float oldV = Float.intBitsToFloat(vertices[index + 1]);
            float uOffset = (oldU - oldTexture.getU0()) / (oldTexture.getU1() - oldTexture.getU0());
            float vOffset = (oldV - oldTexture.getV0()) / (oldTexture.getV1() - oldTexture.getV0());
            vertices[index + 0] = Float.floatToRawIntBits(texture.getU(uOffset * 16.0F));
            vertices[index + 1] = Float.floatToRawIntBits(texture.getV(vOffset * 16.0F));
        }
        return new BakedQuad(
                vertices,
                quad.getTintIndex(),
                quad.getDirection(),
                texture,
                quad.isShade());
    }
    
    public static int getByteOffset(VertexFormat format, VertexFormatElement element) {
        return format.getOffset(format.getElements().indexOf(element));
    }
}
