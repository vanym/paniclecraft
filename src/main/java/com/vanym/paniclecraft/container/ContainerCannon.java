package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerCannon extends ContainerBase {
    
    public final PlayerInventory playerInv;
    public final TileEntityCannon cannon;
    
    public ContainerCannon(int id, PlayerInventory playerInv, TileEntityCannon cannon) {
        super(Core.instance.cannon.containerCannon, id);
        this.playerInv = playerInv;
        this.cannon = cannon;
        cannon.openInventory(playerInv.player);
        this.addSlot(new Slot(cannon, 0, 8, 18));
        this.addPlayerInventorySlots(playerInv);
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity entityplayer) {
        return this.cannon.isUsableByPlayer(entityplayer);
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (slotIndex == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
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
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);
        this.cannon.closeInventory(player);
    }
    
    public static ContainerCannon create(
            int id,
            PlayerInventory playerInv,
            PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();
        TileEntity tile = playerInv.player.world.getTileEntity(pos);
        TileEntityCannon cannon;
        if (tile instanceof TileEntityCannon) {
            cannon = (TileEntityCannon)tile;
        } else {
            cannon = new TileEntityCannon();
        }
        return new ContainerCannon(id, playerInv, cannon);
    }
}
