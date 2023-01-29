package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;

public class ContainerPortableWorkbench extends ContainerWorkbench {
    
    @SuppressWarnings("unchecked")
    public ContainerPortableWorkbench(EntityPlayer player, World world) {
        super(player.inventory, world, (int)player.posX, (int)player.posY, (int)player.posZ);
        Slot original = (Slot)this.inventorySlots.get(0);
        SlotPortableCrafting slot = new SlotPortableCrafting(
                player,
                this.craftMatrix,
                this.craftResult,
                original.slotNumber,
                original.xDisplayPosition,
                original.yDisplayPosition);
        this.inventorySlots.set(0, slot);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return ItemWorkbench.canBeWorkbench(player.getHeldItem());
    }
}
