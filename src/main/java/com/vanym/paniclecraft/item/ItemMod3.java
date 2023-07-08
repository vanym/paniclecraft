package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;

import net.minecraft.item.Item;

public abstract class ItemMod3 extends Item {
    
    public ItemMod3(Item.Properties properties) {
        super(properties.group(Core.instance.tab));
    }
}
