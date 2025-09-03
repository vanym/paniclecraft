package com.vanym.paniclecraft.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotWithValidCheck extends Slot {
    
    public SlotWithValidCheck(IInventory inv, int slotIndex, int x, int y) {
        super(inv, slotIndex, x, y);
    }
    
    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return this.container.canPlaceItem(this.getSlotIndex(), itemStack);
    }
}
