package ee_man.mod3.inventory;

import ee_man.mod3.Core;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.items.ItemPaintBrush;
import ee_man.mod3.utils.Localization;
import ee_man.mod3.utils.MainUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryPalette implements IInventory{
	
	public ContainerPalette container;
	
	public InventoryPalette(ContainerPalette par1Container){
		container = par1Container;
	}
	
	public ItemStack item;
	
	@Override
	public int getSizeInventory(){
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int i){
		return item;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j){
		if(item != null){
			ItemStack itemstack;
			
			if(item.stackSize <= j){
				itemstack = item;
				item = null;
				this.onInventoryChanged();
				return itemstack;
			}
			else{
				itemstack = item.splitStack(j);
				
				if(item.stackSize == 0){
					item = null;
				}
				
				this.onInventoryChanged();
				return itemstack;
			}
		}
		else{
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i){
		if(item != null){
			ItemStack itemstack = item;
			item = null;
			return itemstack;
		}
		else{
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack){
		item = itemstack;
	}
	
	@Override
	public String getInvName(){
		return Localization.get("inv.palette");
	}
	
	@Override
	public boolean isInvNameLocalized(){
		return false;
	}
	
	@Override
	public int getInventoryStackLimit(){
		return 1;
	}
	
	@Override
	public void onInventoryChanged(){
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return true;
	}
	
	@Override
	public void openChest(){
	}
	
	@Override
	public void closeChest(){
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return itemstack.getItem() == Core.itemPaintBrush;
	}
	
	public int getRGB(){
		return item == null ? 0 : ((ItemPaintBrush)Core.itemPaintBrush).getColor(item);
	}
	
	public int getRed(){
		return MainUtils.getRGBFromInt(this.getRGB())[0];
	}
	
	public int getGreen(){
		return MainUtils.getRGBFromInt(this.getRGB())[1];
	}
	
	public int getBlue(){
		return MainUtils.getRGBFromInt(this.getRGB())[2];
	}
	
	public void setRed(byte red){
		this.setColor(red, getGreen(), getBlue());
	}
	
	public void setGreen(byte green){
		this.setColor(getRed(), green, getBlue());
	}
	
	public void setBlue(byte blue){
		this.setColor(getRed(), getGreen(), blue);
	}
	
	public void setColor(int red, int green, int blue){
		if(item != null)
			((ItemPaintBrush)Core.itemPaintBrush).setColor(item, MainUtils.getIntFromRGB(red, green, blue));
	}
}
