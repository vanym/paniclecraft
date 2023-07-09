package com.vanym.paniclecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public abstract class ItemMod3 extends Item implements IMod3Item {
    
    @Override
    public String getUnlocalizedName() {
        return IMod3Item.getUnlocalizedName(this.getRegistryName());
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal(this.getUnlocalizedName(stack)).trim();
    }
}
