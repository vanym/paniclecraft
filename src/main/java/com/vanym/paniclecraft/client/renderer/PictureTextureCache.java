package com.vanym.paniclecraft.client.renderer;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PictureTextureCache extends RenderKVCache<NBTBase, TextureHolder> {
    
    @Override
    protected void clear(TextureHolder value) {
        value.clear();
    }
}
