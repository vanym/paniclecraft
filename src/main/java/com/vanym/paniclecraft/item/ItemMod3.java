package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.item.Item;

public abstract class ItemMod3 extends Item {
    
    public ItemMod3() {
        this.setCreativeTab(Core.instance.tab);
    }
    
    @Override
    public Item setUnlocalizedName(String name) {
        this.setRegistryName(DEF.MOD_ID, name);
        return super.setUnlocalizedName(name);
    }
    
    public String getName() {
        String unlocalizedName = this.getUnlocalizedName();
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
