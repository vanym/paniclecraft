package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.item.ItemBlockMod3;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public interface IMod3Block {
    
    default Block getBlock() {
        return (Block)this;
    }
    
    default void setRegistryName(String name) {
        this.getBlock().unlocalizedName = name;
        this.getBlock().setBlockTextureName(DEF.MOD_ID + ":" + name);
    }
    
    default String getRegistryName() {
        return this.getBlock().unlocalizedName;
    }
    
    default Class<? extends ItemBlock> getItemClass() {
        return ItemBlockMod3.class;
    }
    
    default Object[] getItemArgs() {
        return new Object[]{};
    }
    
    public static String getUnlocalizedName(String name) {
        return "block." + DEF.MOD_ID + "." + name;
    }
}
