package com.vanym.paniclecraft.container.slot;

import java.util.stream.Stream;

import com.vanym.paniclecraft.item.ItemWorkbench;
import com.vanym.paniclecraft.utils.ItemUtils;

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
        Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
              .map(player::getHeldItem)
              .filter(ItemWorkbench::canBeWorkbench)
              .filter(held->held.getItem().isDamageable())
              .findFirst()
              .ifPresent(held->held.damageItem(1, player, ItemUtils.onBroken(held)));
        player.openContainer.detectAndSendChanges();
        return super.onTake(player, stack);
    }
}
