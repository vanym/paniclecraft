package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.core.component.painting.IPictureHolder;
import com.vanym.paniclecraft.core.component.painting.Picture;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPaintingFrame extends TileEntityPaintingContainer {
    
    protected final PictureHolder[] holders = new PictureHolder[6];
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE + "[%d]";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE, i);
            if (this.holders[i] != null) {
                NBTTagCompound paintingTag = new NBTTagCompound();
                this.holders[i].picture.writeToNBT(paintingTag);
                nbtTag.setTag(TAG_PICTURE_I, paintingTag);
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String OLD_TAG_PICTURE_I = String.format("PictureData[%d]", i);
            final String TAG_PICTURE_I = String.format(TAG_PICTURE, i);
            if (nbtTag.hasKey(OLD_TAG_PICTURE_I)) {
                nbtTag.setTag(TAG_PICTURE_I, nbtTag.getTag(OLD_TAG_PICTURE_I));
            }
            if (nbtTag.hasKey(TAG_PICTURE_I)) {
                Picture picture = this.createPicture(i);
                picture.readFromNBT(nbtTag.getCompoundTag(TAG_PICTURE_I));
            } else {
                this.clearPicture(i);
            }
        }
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound dataTag = new NBTTagCompound();
        this.writeToNBT(dataTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbtData = packet.func_148857_g();
        this.readFromNBT(nbtData);
    }
    
    public Picture createPicture(int side) {
        if (side < 0 || side >= this.holders.length) {
            return null;
        }
        if (this.holders[side] != null) {
            return this.holders[side].picture;
        } else {
            return (this.holders[side] = new PictureHolder(side)).picture;
        }
    }
    
    public boolean clearPicture(int side) {
        if (side < 0 || side >= this.holders.length || this.holders[side] == null) {
            return false;
        }
        // TODO WIP invalidate
        this.holders[side] = null;
        return true;
    }
    
    @Override
    public Picture getPainting(int side) {
        if (side >= 0 && side < this.holders.length && this.holders[side] != null) {
            return this.holders[side].picture;
        }
        return null;
    }
    
    protected Picture getPainting(int side, int xO, int yO) {
        TileEntity tile = this.getNeighborTile(side, xO, yO);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePaintingFrame = (TileEntityPaintingFrame)tile;
            return tilePaintingFrame.getPainting(side);
        }
        return null;
    }
    
    @Override
    public void updateEntity() {
        super.updateEntity();
    }
    
    protected class PictureHolder implements IPictureHolder {
        
        protected final Picture picture = new Picture(this);
        
        protected int side;
        
        public PictureHolder(int side) {
            this.side = side;
        }
        
        public void setSide(int side) {
            this.side = side;
        }
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            return TileEntityPaintingFrame.this.getPainting(this.side, offsetX, offsetY);
        }
        
        @Override
        public void update() {
            TileEntityPaintingFrame.this.markForUpdate();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D; // 128 * 128
    }
    
    @SideOnly(Side.CLIENT)
    protected void invalidatePictures() {
        for (PictureHolder holder : this.holders) {
            if (holder != null) {
                holder.picture.invalidate();
            }
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void invalidate() {
        super.invalidate();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.invalidatePictures();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onChunkUnload() {
        super.onChunkUnload();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.invalidatePictures();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onWorldUnload() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.invalidatePictures();
        }
    }
    
}
