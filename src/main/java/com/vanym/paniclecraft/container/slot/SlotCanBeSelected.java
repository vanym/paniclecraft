package com.vanym.paniclecraft.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotCanBeSelected extends Slot{
	
	public SlotCanBeSelected(IInventory par1iInventory, int par2, int par3, int par4){
		super(par1iInventory, par2, par3, par4);
	}
	
	public boolean canTakeStack(EntityPlayer par1EntityPlayer){
		return this.getSlotIndex() != par1EntityPlayer.inventory.currentItem;
	}
}
