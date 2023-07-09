package com.vanym.paniclecraft.container.slot;

import java.util.stream.Stream;

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
        Stream.of(EnumHand.MAIN_HAND, EnumHand.OFF_HAND)
              .map(player::getHeldItem)
              .filter(ItemWorkbench::canBeWorkbench)
              .filter(held->held.getItem().isDamageable())
              .findFirst()
              .ifPresent(held->held.damageItem(1, player));
        player.inventory.markDirty();
        return super.onTake(player, stack);
    }
}
