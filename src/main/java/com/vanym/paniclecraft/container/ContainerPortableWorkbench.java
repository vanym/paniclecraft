package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
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
                original.slotNumber,
                original.xPos,
                original.yPos);
        this.inventorySlots.set(0, slot);
    }
    
    @Override
    public ContainerType<?> getType() {
        return Core.instance.portableworkbench.containerPortableWorkbench;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return ItemWorkbench.canBeWorkbench(player.getHeldItem(Hand.MAIN_HAND))
            || ItemWorkbench.canBeWorkbench(player.getHeldItem(Hand.OFF_HAND));
    }
}
