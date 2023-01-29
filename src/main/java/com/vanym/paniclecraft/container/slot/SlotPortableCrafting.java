package com.vanym.paniclecraft.container.slot;

import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotPortableCrafting extends SlotCrafting {
    
    public SlotPortableCrafting(EntityPlayer player,
            IInventory craftMatrix,
            IInventory craftResult,
            int slotIndex,
            int x,
            int y) {
        super(player, craftMatrix, craftResult, slotIndex, x, y);
    }
    
    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem();
        if (ItemWorkbench.canBeWorkbench(heldStack)
            && heldStack.getItem().getMaxDamage() > 0) {
            heldStack.damageItem(1, player);
        }
        super.onPickupFromSlot(player, stack);
    }
}
