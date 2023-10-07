package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityBase extends TileEntity {
    
    public TileEntityBase(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }
    
    public void markForUpdate() {
        this.markDirty();
        if (this.world != null) {
            BlockState state = this.getBlockState();
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }
    
    public void safeMarkForUpdate() {
        Core.instance.syncTileEntityUpdater.safeMarkForUpdate(this);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        nbtTag = super.write(nbtTag);
        if (nbtTag.contains("ForgeCaps", 10) &&
            nbtTag.getCompound("ForgeCaps").isEmpty()) {
            nbtTag.remove("ForgeCaps");
        }
        return nbtTag;
    }
    
    @Override
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }
    
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        CompoundNBT nbt = packet.getNbtCompound();
        this.handleUpdateTag(nbt);
    }
}
