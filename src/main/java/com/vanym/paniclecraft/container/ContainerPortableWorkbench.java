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
        super(id, inventory, IWorldPosCallable.of(inventory.player.world,
                                                  inventory.player.getPosition()));
        Slot original = (Slot)this.inventorySlots.get(0);
        SlotPortableCrafting slot = new SlotPortableCrafting(
                inventory.player,
                this.field_75162_e,
                this.field_75160_f,
                original.getSlotIndex(),
                original.xPos,
                original.yPos);
        slot.slotNumber = original.slotNumber;
        this.inventorySlots.set(0, slot);
    }
    
    @Override
    public ContainerType<?> getType() {
        return Core.instance.portableworkbench.containerPortableWorkbench;
    }
    
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
        ItemStack stack = super.slotClick(slotId, dragType, clickType, player);
        if (slotId == 0) {
            this.detectAndSendChanges();
        }
        return stack;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
                     .map(player::getHeldItem)
                     .anyMatch(ItemWorkbench::canBeWorkbench);
    }
}
