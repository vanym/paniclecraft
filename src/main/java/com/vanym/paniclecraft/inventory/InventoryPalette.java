package com.vanym.paniclecraft.inventory;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryPalette extends InventoryBasic {
    
    public InventoryPalette() {
        super(Core.instance.painting.itemPalette.getUnlocalizedName() + ".inventory", false, 1);
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof IColorizeable;
    }
}
