package com.vanym.paniclecraft.tileentity;

import java.util.Arrays;
import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.utils.SideUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPaintingFrame extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "paintingframe";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final String TAG_PICTURE_N = TileEntityPainting.TAG_PICTURE + "[%d]";
    
    protected final PictureHolder[] holders = new PictureHolder[N];
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        SideUtils.runSync(this.worldObj != null && !this.worldObj.isRemote,
                          this, ()->this.writeAsync(nbtTag));
    }
    
    protected void writeAsync(NBTTagCompound nbtTag) {
        super.writeToNBT(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                nbtTag.setTag(TAG_PICTURE_I, this.holders[i].picture.serializeNBT());
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        SideUtils.runSync(this.worldObj != null && !this.worldObj.isRemote,
                          this, ()->this.readAsync(nbtTag));
    }
    
    protected void readAsync(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.hasKey(TAG_PICTURE_I)) {
                NBTTagCompound pictureTag = nbtTag.getCompoundTag(TAG_PICTURE_I);
                this.createPicture(i, pictureTag);
            } else {
                this.clearPicture(i);
            }
        }
    }
    
    public Picture createPicture(int side, NBTTagCompound pictureTag) {
        Picture picture = this.createPicture(side);
        picture.deserializeNBT(pictureTag);
        return picture;
    }
    
    public Picture createPicture(int side, ItemStack stack) {
        Picture picture = this.createPicture(side);
        ItemPainting.fillPicture(picture, stack);
        return picture;
    }
    
    public Picture createPicture(int side) {
        if (!this.isValidSide(side)) {
            return null;
        }
        if (this.holders[side] != null) {
            return this.holders[side].picture;
        } else {
            return (this.holders[side] = new PictureHolder(side)).picture;
        }
    }
    
    public boolean clearPicture(int side) {
        if (!this.isValidSide(side) || this.holders[side] == null) {
            return false;
        }
        this.holders[side].picture.unload();
        this.holders[side] = null;
        return true;
    }
    
    @Override
    public Picture getPicture(int side) {
        // don't need to synchronize when getting from main thread,
        // since holders changes done only from main thread
        if (this.isValidSide(side) && this.holders[side] != null) {
            return this.holders[side].picture;
        }
        return null;
    }
    
    protected boolean isValidSide(int side) {
        return side >= 0 && side < this.holders.length;
    }
    
    protected Picture getNeighborPicture(int side, int offsetX, int offsetY) {
        return new WorldPicturePoint(
                WorldPictureProvider.PAINTINGFRAME,
                this.getWorldObj(),
                this.xCoord,
                this.yCoord,
                this.zCoord,
                side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
    }
    
    public void rotateY(int rotUp) {
        ForgeDirection rotator = ForgeDirection.UP;
        ForgeDirection begin = ForgeDirection.SOUTH;
        Picture pictureUp = this.getPicture(rotator.ordinal());
        if (pictureUp != null) {
            pictureUp.rotate(rotUp);
        }
        Picture pictureDown = this.getPicture(rotator.getOpposite().ordinal());
        if (pictureDown != null) {
            pictureDown.rotate((4 - rotUp) % 4);
        }
        for (int i = 0; i < rotUp; i++) {
            PictureHolder holderBegin = this.holders[begin.ordinal()];
            ForgeDirection current = begin;
            while (true) {
                ForgeDirection next = current.getRotation(rotator.getOpposite());
                if (next == begin) {
                    break;
                }
                int c = current.ordinal();
                int n = next.ordinal();
                PictureHolder nextHolder = this.holders[n];
                this.holders[n] = null;
                if (nextHolder != null) {
                    nextHolder.setSide(c);
                }
                this.holders[c] = nextHolder;
                current = next;
            }
            int c = current.ordinal();
            if (holderBegin != null) {
                holderBegin.setSide(c);
            }
            this.holders[c] = holderBegin;
        }
    }
    
    protected class PictureHolder extends TileEntityPaintingContainer.PictureHolder {
        
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
            return TileEntityPaintingFrame.this.getNeighborPicture(this.side, offsetX, offsetY);
        }
        
        @Override
        public String toString() {
            return String.format("Frame[x=%d, y=%d, z=%d, side=%s]",
                                 TileEntityPaintingFrame.this.xCoord,
                                 TileEntityPaintingFrame.this.yCoord,
                                 TileEntityPaintingFrame.this.zCoord,
                                 ForgeDirection.getOrientation(this.side));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return Core.instance.painting.clientConfig.renderPaintingFrameTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPictures() {
        SideUtils.runSync(this.worldObj != null && !this.worldObj.isRemote, this,
                          ()->Arrays.stream(this.holders)
                                    .filter(Objects::nonNull)
                                    .map(h->h.picture)
                                    .forEach(Picture::unload));
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.unloadPictures();
    }
    
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.unloadPictures();
    }
    
    @Override
    public void onWorldUnload() {
        this.unloadPictures();
    }
}
