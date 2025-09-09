package com.vanym.paniclecraft.client.utils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ModelUtils {
    
    public static BakedQuad tintless(BakedQuad quad) {
        return new BakedQuad(
                quad.getVertexData(),
                -1,
                quad.getFace(),
                quad.getSprite(),
                quad.shouldApplyDiffuseLighting(),
                quad.getFormat());
    }
    
    public static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite texture) {
        return new BakedQuadRetextured(quad, texture);
    }
}
