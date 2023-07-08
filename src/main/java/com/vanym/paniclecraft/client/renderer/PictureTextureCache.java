package com.vanym.paniclecraft.client.renderer;

import java.util.HashMap;

import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraft.nbt.INBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PictureTextureCache {
    
    protected long textureTimeout = 60 * 60;
    
    protected long count = 0;
    
    protected HashMap<INBT, Integer> actualTextures = new HashMap<>();
    protected HashMap<INBT, Integer> staleTextures = new HashMap<>();
    
    public void setTextureTimeout(long textureTimeout) {
        this.textureTimeout = textureTimeout;
    }
    
    public int obtainTexture(INBT nbtImageTag) {
        Integer texture;
        texture = this.actualTextures.get(nbtImageTag);
        if (texture != null) {
            return texture;
        }
        texture = this.staleTextures.get(nbtImageTag);
        if (texture == null) {
            return -1;
        }
        this.staleTextures.remove(nbtImageTag, texture);
        this.actualTextures.put(nbtImageTag, texture);
        return texture;
    }
    
    public void putTexture(INBT nbtImageTag, int texture) {
        this.actualTextures.put(nbtImageTag, texture);
    }
    
    protected void clearTexture(int texture) {
        TextureUtil.releaseTextureId(texture);
    }
    
    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        ++this.count;
        if (this.count % this.textureTimeout != 0) {
            return;
        }
        HashMap<INBT, Integer> oldStale = this.staleTextures;
        this.staleTextures = this.actualTextures;
        this.actualTextures = new HashMap<>();
        for (int texture : oldStale.values()) {
            this.clearTexture(texture);
        }
    }
}
