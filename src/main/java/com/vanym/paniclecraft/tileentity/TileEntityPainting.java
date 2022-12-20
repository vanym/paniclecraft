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

public class TileEntityPainting extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "painting";
    
    protected final Picture picture = new Picture(new PictureHolder());
    
    public static final String TAG_PICTURE = "Picture";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        NBTTagCompound paintingTag = new NBTTagCompound();
        this.getPicture().writeToNBT(paintingTag);
        nbtTag.setTag(TAG_PICTURE, paintingTag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        if (nbtTag.hasKey("PictureData")) {
            nbtTag.setTag(TAG_PICTURE, nbtTag.getTag("PictureData"));
        }
        if (nbtTag.hasKey(TAG_PICTURE)) {
            this.getPicture().readFromNBT(nbtTag.getCompoundTag(TAG_PICTURE));
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
    
    public Picture getPicture() {
        return this.picture;
    }
    
    @Override
    public Picture getPainting(int side) {
        if (side == this.getBlockMetadata()) {
            return this.getPicture();
        } else {
            return null;
        }
    }
    
    protected Picture getPainting(int side, int xO, int yO) {
        if (side != this.getBlockMetadata()) {
            return null;
        }
        TileEntity tile = this.getNeighborTile(side, xO, yO);
        if (tile != null && tile instanceof TileEntityPainting) {
            TileEntityPainting tilePainting = (TileEntityPainting)tile;
            return tilePainting.getPainting(side);
        }
        return null;
    }
    
    @Override
    public void updateEntity() {
        super.updateEntity();
    }
    
    protected class PictureHolder implements IPictureHolder {
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            return TileEntityPainting.this.getPainting(TileEntityPainting.this.getBlockMetadata(),
                                                       offsetX, offsetY);
        }
        
        @Override
        public void update() {
            TileEntityPainting.this.markForUpdate();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D; // 128 * 128
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void invalidate() {
        super.invalidate();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().invalidate();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onChunkUnload() {
        super.onChunkUnload();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().invalidate();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onWorldUnload() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.getPicture().invalidate();
        }
    }
}
