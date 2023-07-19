package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityPaintingFrame extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "paintingframe";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final String TAG_PICTURE_N = TileEntityPainting.TAG_PICTURE + "[%d]";
    
    protected final PictureHolder[] holders = new PictureHolder[N];
    
    public TileEntityPaintingFrame() {
        super(Core.instance.painting.tileEntityPaintingFrame);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        super.write(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                nbtTag.put(TAG_PICTURE_I, this.holders[i].picture.serializeNBT());
            }
        }
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        super.read(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.contains(TAG_PICTURE_I)) {
                Picture picture = this.createPicture(i);
                picture.deserializeNBT(nbtTag.getCompound(TAG_PICTURE_I));
            } else {
                this.clearPicture(i);
            }
        }
    }
    
    @Override
    public void markForUpdate() {
        this.markDirty();
        if (this.world != null) {
            BlockState state = this.getBlockState();
            BlockState actual = state;
            if (state.getBlock() instanceof BlockPaintingFrame) {
                BlockPaintingFrame blockPF = (BlockPaintingFrame)state.getBlock();
                actual = blockPF.getActualState(state, this);
            }
            if (state != actual) {
                this.world.setBlockState(this.pos, actual);
            } else {
                this.world.notifyBlockUpdate(this.pos, state, actual, 3);
            }
        }
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
                this.getWorld(),
                this.getPos(),
                side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
    }
    
    public void rotateY(int rotUp) {
        Direction rotator = Direction.UP;
        Direction begin = Direction.SOUTH;
        Picture pictureUp = this.getPicture(rotator.getIndex());
        if (pictureUp != null) {
            pictureUp.rotate(rotUp);
        }
        Picture pictureDown = this.getPicture(rotator.getOpposite().getIndex());
        if (pictureDown != null) {
            pictureDown.rotate((4 - rotUp) % 4);
        }
        for (int i = 0; i < rotUp; i++) {
            PictureHolder holderBegin = this.holders[begin.getIndex()];
            Direction current = begin;
            while (true) {
                Direction next = GeometryUtils.rotateBy(current, rotator.getOpposite());
                if (next == begin) {
                    break;
                }
                int c = current.getIndex();
                int n = next.getIndex();
                PictureHolder nextHolder = this.holders[n];
                this.holders[n] = null;
                if (nextHolder != null) {
                    nextHolder.setSide(c);
                }
                this.holders[c] = nextHolder;
                current = next;
            }
            int c = current.getIndex();
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
                                 TileEntityPaintingFrame.this.getPos().getX(),
                                 TileEntityPaintingFrame.this.getPos().getY(),
                                 TileEntityPaintingFrame.this.getPos().getZ(),
                                 Direction.byIndex(this.side));
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return Core.instance.painting.clientConfig.renderPaintingFrameTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPictures() {
        for (PictureHolder holder : this.holders) {
            if (holder != null) {
                holder.picture.unload();
            }
        }
    }
    
    @Override
    public void remove() {
        super.remove();
        this.unloadPictures();
    }
    
    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.unloadPictures();
    }
    
    @Override
    public void onWorldUnload() {
        this.unloadPictures();
    }
}
