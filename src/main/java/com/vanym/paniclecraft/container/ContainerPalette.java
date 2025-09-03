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
        ItemStack stack = this.inventoryPalette.getItem(0);
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            return null;
        }
        int rgb = colorizeable.getColor(stack);
        return new Color(rgb);
    }
    
    public boolean setColor(Color color) {
        ItemStack stack = this.inventoryPalette.getItem(0);
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            return false;
        }
        colorizeable.setColor(stack, ColorUtils.getAlphaless(color));
        return true;
    }
    
    @Override
    public void containerChanged(IInventory inv) {
        this.slotsChanged(inv);
    }
    
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotNum) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(slotNum);
        
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            if (slotNum == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventoryPalette.canPlaceItem(0, itemstack1)
                && this.moveItemStackTo(itemstack1, 0, 1, true)) {
            } else if (slotNum >= 1 && slotNum < 28) {
                if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotNum >= 28 && slotNum < 37) {
                if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, itemstack1);
        }
        
        return itemstack;
    }
    
    @Override
    public void removed(PlayerEntity entityPlayer) {
        super.removed(entityPlayer);
        if (!entityPlayer.level.isClientSide) {
            this.clearContainer(entityPlayer, entityPlayer.level, this.inventoryPalette);
        }
    }
    
    @Override
    public boolean stillValid(PlayerEntity player) {
        return ItemPalette.canBePalette(player.getItemInHand(Hand.MAIN_HAND))
            || ItemPalette.canBePalette(player.getItemInHand(Hand.OFF_HAND));
    }
}
