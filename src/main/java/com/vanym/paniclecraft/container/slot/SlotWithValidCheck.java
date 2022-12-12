package com.vanym.paniclecraft.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotWithValidCheck extends Slot {
    
    public SlotWithValidCheck(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }
    
    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return this.inventory.isItemValidForSlot(this.getSlotIndex(), par1ItemStack);
    }
}
