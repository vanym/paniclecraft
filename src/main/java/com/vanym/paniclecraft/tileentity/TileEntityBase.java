package com.vanym.paniclecraft.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

public abstract class TileEntityBase extends TileEntity {
    
    public void markForUpdate() {
        this.markDirty();
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }
    
    @Override
    public boolean shouldRefresh(
            World world,
            BlockPos pos,
            IBlockState oldState,
            IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        NBTTagCompound nbt = packet.getNbtCompound();
        this.readFromNBT(nbt);
    }
    
    @Override
    public ITextComponent getDisplayName() {
        if (this instanceof IWorldNameable) {
            IWorldNameable nameable = (IWorldNameable)this;
            if (nameable.hasCustomName()) {
                return new TextComponentString(nameable.getName());
            } else {
                return new TextComponentTranslation(nameable.getName());
            }
        } else {
            return new TextComponentString(this.toString());
        }
    }
}
