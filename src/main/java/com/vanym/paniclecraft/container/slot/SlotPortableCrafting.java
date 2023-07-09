package com.vanym.paniclecraft.container.slot;

import java.util.stream.Stream;

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
        Stream.of(player.getHeldItem())
              .filter(ItemWorkbench::canBeWorkbench)
              .filter(held->held.getItem().isDamageable())
              .findFirst()
              .ifPresent(held->held.damageItem(1, player));
        player.inventory.markDirty();
        super.onPickupFromSlot(player, stack);
    }
}
