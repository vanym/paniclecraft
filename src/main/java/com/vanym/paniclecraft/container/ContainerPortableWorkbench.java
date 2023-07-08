package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.Hand;

public class ContainerPortableWorkbench extends WorkbenchContainer {
    
    public ContainerPortableWorkbench(int id, PlayerInventory inventory) {
        super(id, inventory);
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
    public boolean canInteractWith(PlayerEntity player) {
        return ItemWorkbench.canBeWorkbench(player.getHeldItem(Hand.MAIN_HAND))
            || ItemWorkbench.canBeWorkbench(player.getHeldItem(Hand.OFF_HAND));
    }
}
