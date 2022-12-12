package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.container.slot.SlotPortableCrafting;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerPortableWorkbench extends ContainerWorkbench {
    
    @SuppressWarnings("unchecked")
    public ContainerPortableWorkbench(InventoryPlayer invPlayer, World world, int x, int y, int z) {
        super(invPlayer, world, x, y, z);
        this.inventorySlots.set(0,
                                new SlotPortableCrafting(
                                        invPlayer.player,
                                        this.craftMatrix,
                                        this.craftResult,
                                        0,
                                        124,
                                        35));
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return canBeWorkbench(par1EntityPlayer.getHeldItem());
    }
    
    public static boolean canBeWorkbench(ItemStack par1) {
        return par1 != null && par1.getItem() instanceof ItemWorkbench && par1.stackSize > 0;
    }
}
