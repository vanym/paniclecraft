package com.vanym.paniclecraft.inventory;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.utils.MainUtils;

public class InventoryPalette implements IInventory{
	
	public ContainerPalette container;
	public ItemStack item;
	
	public InventoryPalette(ContainerPalette par1Container){
		container = par1Container;
	}
	
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
				return itemstack;
			}
			else{
				itemstack = item.splitStack(j);
				
				if(item.stackSize == 0){
					item = null;
				}
				
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
	public String getInventoryName(){
		return "item.palette.inv";
	}
	
	@Override
	public boolean hasCustomInventoryName(){
		return false;
	}
	
	@Override
	public int getInventoryStackLimit(){
		return 1;
	}
	
	@Override
	public void markDirty(){
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player){
		return true;
	}
	
	@Override
	public void openInventory(){
	}
	
	@Override
	public void closeInventory(){
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return itemstack != null && itemstack.getItem() instanceof ItemPaintBrush;
	}
	
	public int getRGB(){
		return item == null ? 0 : ((ItemPaintBrush)ModItems.itemPaintBrush).getColor(item);
	}
	
	public void setRed(byte red){
		Color color = MainUtils.getColorFromInt(getRGB());
		this.setColor(red, color.getGreen(), color.getBlue());
	}
	
	public void setGreen(byte green){
		Color color = MainUtils.getColorFromInt(getRGB());
		this.setColor(color.getRed(), green, color.getBlue());
	}
	
	public void setBlue(byte blue){
		Color color = MainUtils.getColorFromInt(getRGB());
		this.setColor(color.getRed(), color.getGreen(), blue);
	}
	
	public void setColor(int red, int green, int blue){
		if(item != null)
			ModItems.itemPaintBrush.setColor(item, MainUtils.getIntFromRGB(red, green, blue));
	}
}
