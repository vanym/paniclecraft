package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCannon extends ContainerBase {
    
    public final InventoryPlayer playerInv;
    public final TileEntityCannon cannon;
    
    public ContainerCannon(InventoryPlayer playerInv, TileEntityCannon cannon) {
        this.playerInv = playerInv;
        this.cannon = cannon;
        cannon.openInventory(playerInv.player);
        this.addSlotToContainer(new Slot(cannon, 0, 8, 18));
        this.addPlayerInventorySlots(playerInv);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.cannon.isUsableByPlayer(entityplayer);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (slotIndex == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                return null;
            }
            
            if (itemstack1.isEmpty()) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return null;
            }
            
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.cannon.closeInventory(player);
    }
}
