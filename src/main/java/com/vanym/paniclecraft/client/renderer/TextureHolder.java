package com.vanym.paniclecraft.client.renderer;

import java.util.Objects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureUtil;

@SideOnly(Side.CLIENT)
public final class TextureHolder {
    
    protected Integer value;
    
    public void set(Integer value) {
        if (this.value != null) {
            throw new IllegalStateException("Current value of TextureHolder is not null");
        }
        this.value = Objects.requireNonNull(value);
    }
    
    public Integer get() {
        return this.value;
    }
    
    public boolean isEmpty() {
        return this.value == null;
    }
    
    public void clear() {
        if (this.value != null) {
            TextureUtil.deleteTexture(this.value);
            this.value = null;
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
