package com.vanym.paniclecraft.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotWithValidCheck extends Slot {
    
    public SlotWithValidCheck(IInventory inv, int slotIndex, int x, int y) {
        super(inv, slotIndex, x, y);
    }
    
    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return this.inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
    }
}
