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
        cannon.startOpen(playerInv.player);
        this.addSlot(new Slot(cannon, 0, 8, 18));
        this.addPlayerInventorySlots(playerInv);
    }
    
    @Override
    public boolean stillValid(PlayerEntity entityplayer) {
        return this.cannon.stillValid(entityplayer);
    }
    
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            if (slotIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
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
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.cannon.stopOpen(player);
    }
    
    public static ContainerCannon create(
            int id,
            PlayerInventory playerInv,
            PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();
        TileEntity tile = playerInv.player.level.getBlockEntity(pos);
        TileEntityCannon cannon;
        if (tile instanceof TileEntityCannon) {
            cannon = (TileEntityCannon)tile;
        } else {
            cannon = new TileEntityCannon();
        }
        return new ContainerCannon(id, playerInv, cannon);
    }
}
