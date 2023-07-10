package com.vanym.paniclecraft.utils;

import net.minecraft.nbt.NBTBase;

public interface INBTSerializable<T extends NBTBase> {
    
    T serializeNBT();
    
    void deserializeNBT(T nbt);
}
