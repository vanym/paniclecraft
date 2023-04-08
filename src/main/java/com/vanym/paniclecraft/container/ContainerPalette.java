package com.vanym.paniclecraft.container;

import java.awt.Color;

import com.vanym.paniclecraft.container.slot.SlotWithValidCheck;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.inventory.InventoryPalette;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPalette extends ContainerBase implements IInvBasic {
    
    public final InventoryPalette inventoryPalette = new InventoryPalette();
    
    public final InventoryPlayer inventoryPlayer;
    
    public ContainerPalette(InventoryPlayer playerInv) {
        this.inventoryPlayer = playerInv;
        this.addSlotToContainer(new SlotWithValidCheck(this.inventoryPalette, 0, 8, 18));
        this.addPlayerInventorySlots(playerInv);
        this.inventoryPalette.func_110134_a(this);
    }
    
    public Color getColor() {
        ItemStack stack = this.inventoryPalette.getStackInSlot(0);
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            return null;
        }
        int rgb = colorizeable.getColor(stack);
        return new Color(rgb);
    }
    
    public boolean setColor(Color color) {
        ItemStack stack = this.inventoryPalette.getStackInSlot(0);
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            return false;
        }
        colorizeable.setColor(stack, ColorUtils.getAlphaless(color));
        return true;
    }
    
    @Override
    public void onInventoryChanged(InventoryBasic inv) {
        this.onCraftMatrixChanged(inv);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNum);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (slotNum == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return null;
                }
            } else if (this.inventoryPalette.isItemValidForSlot(0, itemstack1)
                && this.mergeItemStack(itemstack1, 0, 1, true)) {
            } else if (slotNum >= 1 && slotNum < 28) {
                if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                    return null;
                }
            } else if (slotNum >= 28 && slotNum < 37) {
                if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
                    return null;
                }
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            
            slot.onPickupFromSlot(player, itemstack1);
        }
        
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!entityPlayer.worldObj.isRemote) {
            InventoryUtils.dropOnClosing(this.inventoryPalette, entityPlayer);
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return ItemPalette.canBePalette(entityplayer.getHeldItem());
    }
}
