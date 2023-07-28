package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPainting extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "painting";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    protected final Picture picture = new Picture(new PictureHolder());
    
    public static final String TAG_PICTURE = "Picture";
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTag) {
        return SideUtils.callSync(this.world != null && !this.world.isRemote,
                                  this, ()->this.writeAsync(nbtTag));
    }
    
    protected NBTTagCompound writeAsync(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        nbtTag.setTag(TAG_PICTURE, this.getPicture().serializeNBT());
        return nbtTag;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, ()->this.readAsync(nbtTag));
    }
    
    protected void readAsync(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        if (nbtTag.hasKey(TAG_PICTURE)) {
            this.getPicture().deserializeNBT(nbtTag.getCompoundTag(TAG_PICTURE));
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
    
    protected Picture getNeighborPicture(int offsetX, int offsetY) {
        int side = this.getBlockMetadata();
        return new WorldPicturePoint(
                WorldPictureProvider.PAINTING,
                this.getWorld(),
                this.getPos(),
                side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
    }
    
    @Override
    public String toString() {
        return String.format("Painting[x=%d, y=%d, z=%d, facing=%s]",
                             this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                             EnumFacing.getFront(this.getBlockMetadata()));
    }
    
    protected class PictureHolder extends TileEntityPaintingContainer.PictureHolder {
        
        @Override
        public Picture getNeighborPicture(int offsetX, int offsetY) {
            return TileEntityPainting.this.getNeighborPicture(offsetX, offsetY);
        }
        
        @Override
        public String toString() {
            return TileEntityPainting.this.toString();
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return Core.instance.painting.clientConfig.renderPaintingTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPicture() {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, this.picture::unload);
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.unloadPicture();
    }
    
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.unloadPicture();
    }
    
    @Override
    public void onWorldUnload() {
        this.unloadPicture();
    }
}
