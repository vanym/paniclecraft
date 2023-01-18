package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerPaintingViewBase extends Container {
    
    public final int sizeX;
    public final int sizeY;
    
    public final IPictureSize pictureSize;
    
    public final PictureInv inv = new PictureInv();
    
    protected ContainerPaintingViewBase(IPictureSize pictureSize, int sizeX, int sizeY) {
        this.pictureSize = pictureSize;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        for (int y = 0; y < this.sizeY; ++y) {
            for (int x = 0; x < this.sizeX; ++x) {
                int i = y * this.sizeX + x;
                this.addSlotToContainer(new Slot(this.inv, i, x * 16, y * 16));
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
    
    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.inv.closeInventory();
    }
    
    protected abstract Picture getPicture(int x, int y);
    
    protected final Picture getPicture(int slot) {
        return this.getPicture(slot % this.sizeX, slot / this.sizeX);
    }
    
    protected abstract Picture getOrCreatePicture(int x, int y);
    
    protected final Picture getOrCreatePicture(int slot) {
        return this.getOrCreatePicture(slot % this.sizeX, slot / this.sizeX);
    }
    
    protected abstract boolean clearPicture(int x, int y);
    
    protected final boolean clearPicture(int slot) {
        return this.clearPicture(slot % this.sizeX, slot / this.sizeX);
    }
    
    protected final int getSize() {
        return this.sizeX * this.sizeY;
    }
    
    protected class PictureInv implements IInventory {
        
        @Override
        public ItemStack getStackInSlot(int slot) {
            Picture picture = ContainerPaintingViewBase.this.getPicture(slot);
            if (!IPictureSize.equals(picture, ContainerPaintingViewBase.this.pictureSize)) {
                return null;
            }
            return ItemPainting.getPictureAsItem(picture);
        }
        
        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (stack == null || stack.stackSize <= 0
                || stack.getItem() != Core.instance.painting.itemPainting) {
                ContainerPaintingViewBase.this.clearPicture(slot);
                return;
            }
            Picture picture = ContainerPaintingViewBase.this.getOrCreatePicture(slot);
            if (IPictureSize.equals(picture, ContainerPaintingViewBase.this.pictureSize)) {
                ItemPainting.fillPicture(picture, stack);
            }
        }
        
        @Override
        public int getSizeInventory() {
            return ContainerPaintingViewBase.this.getSize();
        }
        
        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return null;
        }
        
        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return null;
        }
        
        @Override
        public String getInventoryName() {
            return "PictureInv";
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
        public void markDirty() {}
        
        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }
        
        @Override
        public void openInventory() {}
        
        @Override
        public void closeInventory() {
            int size = this.getSizeInventory();
            for (int i = 0; i < size; ++i) {
                ContainerPaintingViewBase.this.clearPicture(i);
            }
        }
        
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return false;
        }
    }
}
