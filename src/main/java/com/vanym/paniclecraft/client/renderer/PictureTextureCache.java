package com.vanym.paniclecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTBase;

@SideOnly(Side.CLIENT)
public class PictureTextureCache extends RenderKVCache<NBTBase, TextureHolder> {
    
    @Override
    protected void clear(TextureHolder value) {
        value.clear();
    }
}
