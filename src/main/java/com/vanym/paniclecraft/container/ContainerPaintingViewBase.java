package com.vanym.paniclecraft.container;

import java.io.File;
import java.io.IOException;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.ImageUtils;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public abstract class ContainerPaintingViewBase extends Container {
    
    public final int sizeX;
    public final int sizeY;
    
    public final IPictureSize pictureSize;
    
    protected final PictureInv inv = new PictureInv();
    
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
    
    public final int getWidth() {
        return this.sizeX * this.pictureSize.getWidth();
    }
    
    public final int getHeight() {
        return this.sizeY * this.pictureSize.getHeight();
    }
    
    public IChatComponent savePainting(File file) {
        try {
            ImageUtils.savePainting(file, this.pictureSize, this.sizeX, this.sizeY,
                                    (x, y)->this.getPicture(x, y));
            ChatComponentText message = new ChatComponentText(file.getName());
            ChatStyle style = message.getChatStyle();
            style.setChatClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_FILE,
                    file.getAbsolutePath()));
            style.setUnderlined(true);
            return new ChatComponentTranslation("painting.export.success", message);
        } catch (IOException e) {
            return new ChatComponentTranslation("painting.export.failure", e.getMessage());
        }
    }
    
    public boolean addPicture(int inputX, int inputY, Picture input) {
        boolean changed = false;
        int pictureWidth = this.pictureSize.getWidth();
        int pictureHeight = this.pictureSize.getHeight();
        int inputEndX = inputX + input.getWidth();
        int inputEndY = inputY + input.getHeight();
        int w = Math.min(this.sizeX, inputEndX / pictureWidth +
                                     ((inputEndX % pictureWidth) == 0 ? 0 : 1));
        int h = Math.min(this.sizeY, inputEndY / pictureHeight +
                                     ((inputEndY % pictureHeight) == 0 ? 0 : 1));
        for (int y = Math.max(0, inputY / pictureHeight); y < h; ++y) {
            int paintingY = y * pictureHeight;
            for (int x = Math.max(0, inputX / pictureWidth); x < w; ++x) {
                Picture picture = this.getPicture(x, y);
                if (picture == null) {
                    continue;
                }
                int paintingX = x * pictureWidth;
                changed |= picture.addPicture(inputX - paintingX, inputY - paintingY, input);
            }
        }
        if (changed) {
            this.detectAndSendChanges();
        }
        return changed;
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
