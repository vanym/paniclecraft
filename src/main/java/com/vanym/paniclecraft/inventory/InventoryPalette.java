package com.vanym.paniclecraft.inventory;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryPalette extends Inventory {
    
    public InventoryPalette() {
        super(1);
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof IColorizeable;
    }
}
