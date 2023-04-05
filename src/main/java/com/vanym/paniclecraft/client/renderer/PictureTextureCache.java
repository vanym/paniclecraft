package com.vanym.paniclecraft.client.renderer;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PictureTextureCache {
    
    protected long textureTimeout = 60 * 60;
    
    protected long count = 0;
    
    protected HashMap<NBTBase, Integer> actualTextures = new HashMap<>();
    protected HashMap<NBTBase, Integer> staleTextures = new HashMap<>();
    
    public void setTextureTimeout(long textureTimeout) {
        this.textureTimeout = textureTimeout;
    }
    
    public int obtainTexture(NBTBase nbtImageTag) {
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
    
    public void putTexture(NBTBase nbtImageTag, int texture) {
        this.actualTextures.put(nbtImageTag, texture);
    }
    
    protected void clearTexture(int texture) {
        TextureUtil.deleteTexture(texture);
    }
    
    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        ++this.count;
        if (this.count % this.textureTimeout != 0) {
            return;
        }
        HashMap<NBTBase, Integer> oldStale = this.staleTextures;
        this.staleTextures = this.actualTextures;
        this.actualTextures = new HashMap<>();
        for (int texture : oldStale.values()) {
            this.clearTexture(texture);
        }
    }
}
