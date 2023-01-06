package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPainting extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "painting";
    public static final String ID = DEF.MOD_ID + "." + IN_MOD_ID;
    
    protected final Picture picture = new Picture(new PictureHolder());
    
    public static final String TAG_PICTURE = "Picture";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        NBTTagCompound pictureTag = new NBTTagCompound();
        this.getPicture().writeToNBT(pictureTag);
        nbtTag.setTag(TAG_PICTURE, pictureTag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        if (nbtTag.hasKey(TAG_PICTURE)) {
            this.getPicture().readFromNBT(nbtTag.getCompoundTag(TAG_PICTURE));
        }
    }
    
    public Picture getPicture() {
        return this.picture;
    }
    
    @Override
    public Picture getPicture(int side) {
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
            return tilePainting.getPicture(side);
        }
        return null;
    }
    
    @Override
    public void updateEntity() {
        super.updateEntity();
    }
    
    protected class PictureHolder extends TileEntityPaintingContainer.PictureHolder {
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            return TileEntityPainting.this.getPainting(TileEntityPainting.this.getBlockMetadata(),
                                                       offsetX, offsetY);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D; // 128 * 128
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.picture.unload();
    }
    
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.picture.unload();
    }
    
    @Override
    public void onWorldUnload() {
        this.picture.unload();
    }
}
