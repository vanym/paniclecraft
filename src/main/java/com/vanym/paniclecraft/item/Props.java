package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;

import net.minecraft.item.Item;

public class Props {
    
    static Item.Properties create() {
        return new Item.Properties().group(Core.instance.tab);
    }
}
