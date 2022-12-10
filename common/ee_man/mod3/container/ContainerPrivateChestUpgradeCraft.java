package ee_man.mod3.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;

public class ContainerPrivateChestUpgradeCraft extends ContainerPrivateChest{
	
	public InventoryCrafting craftMatrix;
	public InventoryCraftResult craftResult;
	
	public int mySlotsStart;
	
	public ContainerPrivateChestUpgradeCraft(InventoryPlayer par1InventoryPlayer, TileEntityPrivateChest par2Tile){
		super(par1InventoryPlayer, par2Tile);
	}
	
	public void init(){
		super.init();
		this.mySlotsStart = this.inventorySlots.size();
		craftMatrix = new InventoryCrafting(this, 3, 3);
		craftResult = new InventoryCraftResult();
		this.addSlotToContainer(new SlotCrafting(this.playerInventory.player, this.craftMatrix, this.craftResult, 0, 221, 71));
		for(int i1 = 0; i1 < 3; i1++){
			for(int j1 = 0; j1 < 3; j1++){
				this.addSlotToContainer(new Slot(this.craftMatrix, j1 + i1 * 3, 157 + j1 * 18, 18 + i1 * 18));
			}
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){
		if(par2 < this.mySlotsStart)
			return super.transferStackInSlot(par1EntityPlayer, par2);
		else{
			ItemStack itemstack = null;
			Slot slot = (Slot)this.inventorySlots.get(par2);
			
			if(slot != null && slot.getHasStack()){
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();
				if(par2 == this.mySlotsStart){
					if(!this.mergeItemStack(itemstack1, 80, this.mySlotsStart, true)){
						return null;
					}
					
					slot.onSlotChange(itemstack1, itemstack);
				}
				else
					if(!this.mergeItemStack(itemstack1, 80, this.mySlotsStart, true)){
						return null;
					}
				
				if(itemstack1.stackSize == 0){
					slot.putStack((ItemStack)null);
				}
				else{
					slot.onSlotChanged();
				}
				
				if(itemstack1.stackSize == itemstack.stackSize){
					return null;
				}
				
				slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
			}
			
			return itemstack;
		}
	}
	
	public void onCraftMatrixChanged(IInventory par1IInventory){
		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, tile.worldObj));
	}
	
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer){
		super.onContainerClosed(par1EntityPlayer);
		if(!this.tile.worldObj.isRemote){
			for(int i = 0; i < 9; ++i){
				ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);
				if(itemstack != null){
					par1EntityPlayer.dropPlayerItem(itemstack);
				}
			}
		}
	}
}
