package com.vanym.paniclecraft.container;

import java.util.stream.Stream;

import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ContainerPortableWorkbench extends ContainerWorkbench {
    
    public ContainerPortableWorkbench(EntityPlayer player, World world) {
        super(player.inventory, world, player.getPosition());
        Slot original = (Slot)this.inventorySlots.get(0);
        SlotPortableCrafting slot = new SlotPortableCrafting(
                player,
                this.craftMatrix,
                this.craftResult,
                original.getSlotIndex(),
                original.xPos,
                original.yPos);
        slot.slotNumber = original.slotNumber;
        this.inventorySlots.set(0, slot);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return Stream.of(EnumHand.MAIN_HAND, EnumHand.OFF_HAND)
                     .map(player::getHeldItem)
                     .anyMatch(ItemWorkbench::canBeWorkbench);
    }
}
