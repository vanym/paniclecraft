package com.vanym.paniclecraft.container.slot;

import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class SlotPortableCrafting extends CraftingResultSlot {
    
    public SlotPortableCrafting(PlayerEntity player,
            CraftingInventory craftMatrix,
            IInventory craftResult,
            int slotIndex,
            int x,
            int y) {
        super(player, craftMatrix, craftResult, slotIndex, x, y);
    }
    
    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem(Hand.MAIN_HAND);
        if (ItemWorkbench.canBeWorkbench(heldStack)
            && heldStack.getItem().getMaxDamage(heldStack) > 0) {
            heldStack.damageItem(1, player, (p)->p.sendBreakAnimation(Hand.MAIN_HAND));
        } else {
            ItemStack offStack = player.getHeldItem(Hand.OFF_HAND);
            if (ItemWorkbench.canBeWorkbench(offStack)
                && offStack.getItem().getMaxDamage(offStack) > 0) {
                offStack.damageItem(1, player, (p)->p.sendBreakAnimation(Hand.OFF_HAND));
            }
        }
        return super.onTake(player, stack);
    }
}
