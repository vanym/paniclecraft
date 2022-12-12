package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.container.slot.SlotCanBeSelected;
import com.vanym.paniclecraft.container.slot.SlotWithValidCheck;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.inventory.InventoryPalette;
import com.vanym.paniclecraft.item.ItemPaintBrush;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPalette extends Container {
    
    public InventoryPalette inventoryPalette = new InventoryPalette(this);
    
    public InventoryPlayer inventoryPlayer;
    
    public ContainerPalette(InventoryPlayer par1InventoryPlayer) {
        this.inventoryPlayer = par1InventoryPlayer;
        
        int i = -18;
        int j;
        int k;
        this.addSlotToContainer(new SlotWithValidCheck(this.inventoryPalette, 0, 8, 18));
        for(j = 0; j < 3; ++j) {
            for(k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(
                        par1InventoryPlayer,
                        k + j * 9 + 9,
                        8 + k * 18,
                        103 + j * 18 + i));
            }
        }
        
        for(j = 0; j < 9; ++j) {
            this.addSlotToContainer(new SlotCanBeSelected(
                    par1InventoryPlayer,
                    j,
                    8 + j * 18,
                    161 + i));
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            if (par2 == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return null;
                }
            } else if (itemstack1.getItem() instanceof ItemPaintBrush
                && this.mergeItemStack(itemstack1, 0, 1, true)) {
                
            } else if (par2 >= 1 && par2 < 28) {
                if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                    return null;
                }
            } else if (par2 >= 28 && par2 < 37) {
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
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (this.inventoryPalette.item != null) {
            par1EntityPlayer.dropPlayerItemWithRandomChoice(this.inventoryPalette.item, false);
            this.inventoryPalette.item = null;
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return canBePalette(entityplayer.getHeldItem());
    }
    
    public static boolean canBePalette(ItemStack par1) {
        return par1 == null ? false : par1.getItem() == ModItems.itemPalette && par1.stackSize > 0;
    }
}
