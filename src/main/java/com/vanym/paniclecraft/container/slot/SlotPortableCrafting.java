package com.vanym.paniclecraft.container.slot;

import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class SlotPortableCrafting extends SlotCrafting {
    
    public SlotPortableCrafting(EntityPlayer player,
            InventoryCrafting craftMatrix,
            IInventory craftResult,
            int slotIndex,
            int x,
            int y) {
        super(player, craftMatrix, craftResult, slotIndex, x, y);
    }
    
    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (ItemWorkbench.canBeWorkbench(heldStack)
            && heldStack.getItem().getMaxDamage(heldStack) > 0) {
            heldStack.damageItem(1, player);
        } else {
            ItemStack offStack = player.getHeldItem(EnumHand.OFF_HAND);
            if (ItemWorkbench.canBeWorkbench(offStack)
                && offStack.getItem().getMaxDamage(offStack) > 0) {
                offStack.damageItem(1, player);
            }
        }
        return super.onTake(player, stack);
    }
}
