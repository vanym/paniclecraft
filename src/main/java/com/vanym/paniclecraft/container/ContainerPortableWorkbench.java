package com.vanym.paniclecraft.container;

import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;

public class ContainerPortableWorkbench extends WorkbenchContainer {
    
    public ContainerPortableWorkbench(int id, PlayerInventory inventory) {
        super(id, inventory, IWorldPosCallable.create(inventory.player.level,
                                                      inventory.player.blockPosition()));
        Slot original = (Slot)this.slots.get(0);
        SlotPortableCrafting slot = new SlotPortableCrafting(
                inventory.player,
                this.craftSlots,
                this.resultSlots,
                original.getSlotIndex(),
                original.x,
                original.y);
        slot.index = original.index;
        this.slots.set(0, slot);
    }
    
    @Override
    public ContainerType<?> getType() {
        return Core.instance.portableworkbench.containerPortableWorkbench;
    }
    
    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        ItemStack stack = super.clicked(slotId, dragType, clickType, player);
        if (slotId == 0) {
            this.broadcastChanges();
        }
        return stack;
    }
    
    @Override
    public boolean stillValid(PlayerEntity player) {
        return Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
                     .map(player::getItemInHand)
                     .anyMatch(ItemWorkbench::canBeWorkbench);
    }
}
