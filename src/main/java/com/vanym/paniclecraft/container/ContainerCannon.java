package com.vanym.paniclecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

public class ContainerCannon extends Container{
	
	public TileEntityCannon tileCannon;
	
	public ContainerCannon(IInventory par1IInventory, TileEntityCannon par2TileEntityCannon){
		this.tileCannon = par2TileEntityCannon;
		par2TileEntityCannon.openChest();
		int i = -18;
		int j;
		int k;
		this.addSlotToContainer(new Slot(par2TileEntityCannon, 0, 8, 18));
		for(j = 0; j < 3; ++j){
			for(k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(par1IInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}
		
		for(j = 0; j < 9; ++j){
			this.addSlotToContainer(new Slot(par1IInventory, j, 8 + j * 18, 161 + i));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		return this.tileCannon.isUseableByPlayer(entityplayer);
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(par2);
		
		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if(par2 == 0){
				if(!this.mergeItemStack(itemstack1, 1, 37, true)){
					return null;
				}
			}
			else
				if(!this.mergeItemStack(itemstack1, 0, 1, false)){
					return null;
				}
			
			if(itemstack1.stackSize == 0){
				slot.putStack((ItemStack)null);
			}
			else{
				slot.onSlotChanged();
			}
		}
		
		return itemstack;
	}
	
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer){
		super.onContainerClosed(par1EntityPlayer);
		this.tileCannon.closeChest();
	}
	
	public IInventory getLowerChestInventory(){
		return this.tileCannon;
	}
}
