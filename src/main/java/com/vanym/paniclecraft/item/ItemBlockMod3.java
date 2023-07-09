package com.vanym.paniclecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMod3 extends ItemBlock {
    
    public ItemBlockMod3(Block block) {
        super(block);
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.block.getLocalizedName();
    }
}
