package ee_man.mod3.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ee_man.mod3.container.slot.SlotCanBeSelected;
import ee_man.mod3.container.slot.SlotWithValidCheck;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.inventory.InventoryPalette;
import ee_man.mod3.item.ItemPaintBrush;

public class ContainerPalette extends Container{
	
	public InventoryPalette inventoryPalette = new InventoryPalette(this);
	
	public InventoryPlayer inventoryPlayer;
	
	public ContainerPalette(InventoryPlayer par1InventoryPlayer){
		inventoryPlayer = par1InventoryPlayer;
		
		int i = -18;
		int j;
		int k;
		this.addSlotToContainer(new SlotWithValidCheck(inventoryPalette, 0, 8, 18));
		for(j = 0; j < 3; ++j){
			for(k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(par1InventoryPlayer, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}
		
		for(j = 0; j < 9; ++j){
			this.addSlotToContainer(new SlotCanBeSelected(par1InventoryPlayer, j, 8 + j * 18, 161 + i));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(par2);
		
		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			if(par2 == 0){
				if(!this.mergeItemStack(itemstack1, 1, 37, true)){
					return null;
				}
			}
			else
				if(itemstack1.getItem() instanceof ItemPaintBrush && this.mergeItemStack(itemstack1, 0, 1, true)){
					
				}
				else
					if(par2 >= 1 && par2 < 28){
						if(!this.mergeItemStack(itemstack1, 28, 37, false)){
							return null;
						}
					}
					else
						if(par2 >= 28 && par2 < 37){
							if(!this.mergeItemStack(itemstack1, 1, 28, false)){
								return null;
							}
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
	
	public void onContainerClosed(EntityPlayer par1EntityPlayer){
		super.onContainerClosed(par1EntityPlayer);
		if(inventoryPalette.item != null){
			par1EntityPlayer.dropPlayerItemWithRandomChoice(inventoryPalette.item, false);
			inventoryPalette.item = null;
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		return canBePalette(entityplayer.getHeldItem());
	}
	
	public static boolean canBePalette(ItemStack par1){
		return par1 == null ? false : par1.getItem() == ModItems.itemPalette && par1.stackSize > 0;
	}
}
