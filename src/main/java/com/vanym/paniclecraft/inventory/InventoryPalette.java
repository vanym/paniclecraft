package com.vanym.paniclecraft.inventory;

import java.awt.Color;

import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.utils.MainUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryPalette implements IInventory {
    
    public ContainerPalette container;
    public ItemStack item;
    
    public InventoryPalette(ContainerPalette par1Container) {
        this.container = par1Container;
    }
    
    @Override
    public int getSizeInventory() {
        return 1;
    }
    
    @Override
    public ItemStack getStackInSlot(int i) {
        return this.item;
    }
    
    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (this.item != null) {
            ItemStack itemstack;
            
            if (this.item.stackSize <= j) {
                itemstack = this.item;
                this.item = null;
                return itemstack;
            } else {
                itemstack = this.item.splitStack(j);
                
                if (this.item.stackSize == 0) {
                    this.item = null;
                }
                
                return itemstack;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        if (this.item != null) {
            ItemStack itemstack = this.item;
            this.item = null;
            return itemstack;
        } else {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.item = itemstack;
    }
    
    @Override
    public String getInventoryName() {
        return "item.palette.inv";
    }
    
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    @Override
    public void markDirty() {
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }
    
    @Override
    public void openInventory() {
    }
    
    @Override
    public void closeInventory() {
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() instanceof ItemPaintBrush;
    }
    
    public int getRGB() {
        return this.item == null ? 0
                                 : ((ItemPaintBrush)ModItems.itemPaintBrush).getColor(this.item);
    }
    
    public void setRed(byte red) {
        Color color = MainUtils.getColorFromInt(this.getRGB());
        this.setColor(red, color.getGreen(), color.getBlue());
    }
    
    public void setGreen(byte green) {
        Color color = MainUtils.getColorFromInt(this.getRGB());
        this.setColor(color.getRed(), green, color.getBlue());
    }
    
    public void setBlue(byte blue) {
        Color color = MainUtils.getColorFromInt(this.getRGB());
        this.setColor(color.getRed(), color.getGreen(), blue);
    }
    
    public void setColor(int red, int green, int blue) {
        if (this.item != null) {
            ModItems.itemPaintBrush.setColor(this.item, MainUtils.getIntFromRGB(red, green, blue));
        }
    }
}
