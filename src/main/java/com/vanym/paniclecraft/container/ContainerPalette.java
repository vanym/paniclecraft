package com.vanym.paniclecraft.container;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.slot.SlotWithValidCheck;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.inventory.InventoryPalette;
import com.vanym.paniclecraft.utils.MainUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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
        IColorizeable colorizeable = getColorizeable(stack);
        if (colorizeable == null) {
            return null;
        }
        int rgb = colorizeable.getColor(stack);
        return new Color(rgb);
    }
    
    public boolean setColor(Color color) {
        ItemStack stack = this.inventoryPalette.getStackInSlot(0);
        IColorizeable colorizeable = getColorizeable(stack);
        if (colorizeable == null) {
            return false;
        }
        colorizeable.setColor(stack, MainUtils.getAlphaless(color));
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
        }
        
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!entityPlayer.worldObj.isRemote) {
            int size = this.inventoryPalette.getSizeInventory();
            for (int i = 0; i < size; ++i) {
                ItemStack itemStack = this.inventoryPalette.getStackInSlotOnClosing(i);
                if (itemStack == null) {
                    continue;
                }
                entityPlayer.dropPlayerItemWithRandomChoice(itemStack, false);
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return canBePalette(entityplayer.getHeldItem());
    }
    
    public static boolean canBePalette(ItemStack itemStack) {
        if (itemStack == null || itemStack.stackSize == 0) {
            return false;
        }
        Item item = itemStack.getItem();
        return item == Core.instance.painting.itemPalette;
    }
    
    protected static IColorizeable getColorizeable(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        Item item = stack.getItem();
        if (!(item instanceof IColorizeable)) {
            return null;
        }
        return (IColorizeable)item;
    }
}
