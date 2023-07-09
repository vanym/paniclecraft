package com.vanym.paniclecraft.container;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.slot.SlotWithValidCheck;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.inventory.InventoryPalette;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ContainerPalette extends ContainerBase implements IInventoryChangedListener {
    
    public final InventoryPalette inventoryPalette = new InventoryPalette();
    
    public final PlayerInventory inventoryPlayer;
    
    public ContainerPalette(int id, PlayerInventory playerInv) {
        super(Core.instance.painting.containerPalette, id);
        this.inventoryPlayer = playerInv;
        this.addSlot(new SlotWithValidCheck(this.inventoryPalette, 0, 8, 18));
        this.addPlayerInventorySlots(playerInv);
        this.inventoryPalette.addListener(this);
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
    public void onInventoryChanged(IInventory inv) {
        this.onCraftMatrixChanged(inv);
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotNum) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(slotNum);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (slotNum == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventoryPalette.isItemValidForSlot(0, itemstack1)
                && this.mergeItemStack(itemstack1, 0, 1, true)) {
            } else if (slotNum >= 1 && slotNum < 28) {
                if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotNum >= 28 && slotNum < 37) {
                if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, itemstack1);
        }
        
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!entityPlayer.world.isRemote) {
            this.clearContainer(entityPlayer, entityPlayer.world, this.inventoryPalette);
        }
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return ItemPalette.canBePalette(player.getHeldItem(Hand.MAIN_HAND))
            || ItemPalette.canBePalette(player.getHeldItem(Hand.OFF_HAND));
    }
}
