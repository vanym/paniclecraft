package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.utils.DistUtils;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.ClientPlayNetHandler;
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
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }
    
    public void safeMarkForUpdate() {
        Core.instance.syncTileEntityUpdater.safeMarkForUpdate(this);
    }
    
    @Override
    public CompoundNBT save(CompoundNBT nbtTag) {
        nbtTag = super.save(nbtTag);
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
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        DistUtils.crun(()->new Runnable() {
            @Override
            public void run() {
                ClientPlayNetHandler handler = (ClientPlayNetHandler)manager.getPacketListener();
                BlockState state = handler.getLevel().getBlockState(packet.getPos());
                CompoundNBT nbt = packet.getTag();
                TileEntityBase.this.handleUpdateTag(state, nbt);
            }
        });
    }
}
