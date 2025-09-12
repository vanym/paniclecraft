package com.vanym.paniclecraft.client.renderer;

import net.minecraft.nbt.INBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PictureTextureCache extends RenderKVCache<INBT, TextureHolder> {
    
    @Override
    protected void clear(TextureHolder value) {
        value.clear();
    }
}
