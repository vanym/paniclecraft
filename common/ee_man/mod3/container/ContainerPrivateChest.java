package ee_man.mod3.container;

import ee_man.mod3.Core;
import ee_man.mod3.container.slot.SlotUpgrade;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPrivateChest extends Container{
	
	public boolean isOpen = true;
	
	public TileEntityPrivateChest tile;
	
	public InventoryPlayer playerInventory;
	
	public ContainerPrivateChest(InventoryPlayer par1InventoryPlayer, TileEntityPrivateChest par2Tile){
		playerInventory = par1InventoryPlayer;
		tile = par2Tile;
		this.init();
	}
	
	public void init(){
		int i = 71;
		int j;
		int k;
		
		for(j = 0; j < 8; ++j){
			for(k = 0; k < 8; ++k){
				this.addSlotToContainer(new Slot(tile.inventoryItems, k + j * 8, 8 + k * 18, 18 + j * 18));
			}
		}
		
		for(j = 0; j < 4; ++j){
			for(k = 0; k < 4; ++k){
				this.addSlotToContainer(new SlotUpgrade(tile, k + j * 4, 176 + k * 18, 103 + j * 18 + i));
			}
		}
		
		for(j = 0; j < 3; ++j){
			for(k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}
		
		for(j = 0; j < 9; ++j){
			this.addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(par2);
		
		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if(par2 < 64){
				if(!this.mergeItemStack(itemstack1, 80, 116, true)){
					return null;
				}
			}
			else
				if(itemstack.getItem() instanceof IUpgradeForPrivateChest && !tile.inventoryUpgrades.hasType(((IUpgradeForPrivateChest)itemstack.getItem()).getType(itemstack)) && ((IUpgradeForPrivateChest)itemstack1.getItem()).canBePuted(tile, itemstack1, par1EntityPlayer)){
					if(!this.mergeItemStack(itemstack1, 64, 80, false)){
						return null;
					}
					else
						((IUpgradeForPrivateChest)itemstack1.getItem()).onPut(tile, itemstack1, par1EntityPlayer);
				}
				else
					if(!this.mergeItemStack(itemstack1, 0, 64, false)){
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
	
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer){
		if(par1 >= 0 && this.getSlot(par1).inventory.equals(tile.inventoryUpgrades) && par3 != 0 && par3 != 1){
			return null;
		}
		if(par1 >= 0 && par3 == 1 && this.getSlot(par1).inventory.equals(tile.inventoryUpgrades) && this.getSlot(par1).getHasStack() && this.getSlot(par1).getSlotIndex() != this.tile.select){
			tile.select = (byte)this.getSlot(par1).getSlotIndex();
			tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			isOpen = false;
			for(Object player : this.crafters.toArray()){
				if(player instanceof EntityPlayerMP){
					((EntityPlayerMP)player).openGui(Core.instance, 3, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
				}
			}
			return null;
		}
		else
			if(par1 >= 0 && this.getSlot(par1).getSlotIndex() == tile.select && this.getSlot(par1).inventory.equals(tile.inventoryUpgrades)){
				tile.select = -1;
				tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
				isOpen = false;
				for(Object player : this.crafters.toArray()){
					if(player instanceof EntityPlayerMP){
						((EntityPlayerMP)player).openGui(Core.instance, 3, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
					}
				}
				return null;
			}
			else
				if(par1 >= 0 && this.getSlot(par1).inventory.equals(tile.inventoryUpgrades)){
					if(this.getSlot(par1).getHasStack() && !((IUpgradeForPrivateChest)this.getSlot(par1).getStack().getItem()).canBePulled(tile, this.getSlot(par1).getStack(), par4EntityPlayer))
						return null;
					if(par4EntityPlayer.inventory.getItemStack() != null && (!(par4EntityPlayer.inventory.getItemStack().getItem() instanceof IUpgradeForPrivateChest) || !((IUpgradeForPrivateChest)par4EntityPlayer.inventory.getItemStack().getItem()).canBePuted(tile, par4EntityPlayer.inventory.getItemStack(), par4EntityPlayer)))
						return null;
					if(this.getSlot(par1).getHasStack())
						((IUpgradeForPrivateChest)this.getSlot(par1).getStack().getItem()).onPull(tile, this.getSlot(par1).getStack(), par4EntityPlayer);
					if(par4EntityPlayer.inventory.getItemStack() != null)
						((IUpgradeForPrivateChest)par4EntityPlayer.inventory.getItemStack().getItem()).onPut(tile, par4EntityPlayer.inventory.getItemStack(), par4EntityPlayer);
				}
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		return isOpen && (tile == null ? false : !tile.isInvalid() && entityplayer.getDistanceSq((double)this.tile.xCoord + 0.5D, (double)this.tile.yCoord + 0.5D, (double)this.tile.zCoord + 0.5D) <= 64.0D);
	}
	
}