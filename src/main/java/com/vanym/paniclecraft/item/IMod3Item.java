package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.DEF;

import net.minecraft.item.Item;

public interface IMod3Item {
    
    default Item getItem() {
        return (Item)this;
    }
    
    default void setRegistryName(String name) {
        this.getItem().unlocalizedName = name;
        this.getItem().setTextureName(DEF.MOD_ID + ":" + name);
    }
    
    default String getRegistryName() {
        return this.getItem().unlocalizedName;
    }
    
    public static String getUnlocalizedName(String name) {
        return "item." + DEF.MOD_ID + "." + name;
    }
}
