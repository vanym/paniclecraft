package com.vanym.paniclecraft.tileentity;

import java.util.Arrays;
import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
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
    public CompoundNBT save(CompoundNBT nbtTag) {
        return SideUtils.callSync(this.level != null && !this.level.isClientSide,
                                  this, ()->this.writeAsync(nbtTag));
    }
    
    protected CompoundNBT writeAsync(CompoundNBT nbtTag) {
        super.save(nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (this.holders[i] != null) {
                nbtTag.put(TAG_PICTURE_I, this.holders[i].picture.serializeNBT());
            }
        }
        return nbtTag;
    }
    
    @Override
    public void load(BlockState state, CompoundNBT nbtTag) {
        SideUtils.runSync(this.level != null && !this.level.isClientSide,
                          this, ()->this.readAsync(state, nbtTag));
    }
    
    protected void readAsync(BlockState state, CompoundNBT nbtTag) {
        super.load(state, nbtTag);
        for (int i = 0; i < this.holders.length; i++) {
            final String TAG_PICTURE_I = String.format(TAG_PICTURE_N, i);
            if (nbtTag.contains(TAG_PICTURE_I)) {
                CompoundNBT pictureTag = nbtTag.getCompound(TAG_PICTURE_I);
                this.createPicture(i, pictureTag);
            } else {
                this.clearPicture(i);
            }
        }
    }
    
    @Override
    public void markForUpdate() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.getBlockState();
            BlockState actual = state;
            if (state.getBlock() instanceof BlockPaintingFrame) {
                BlockPaintingFrame blockPF = (BlockPaintingFrame)state.getBlock();
                actual = blockPF.getActualState(state, this);
            }
            if (state != actual) {
                this.level.setBlockAndUpdate(this.worldPosition, actual);
            } else {
                this.level.sendBlockUpdated(this.worldPosition, state, actual, 3);
            }
        }
    }
    
    public Picture createPicture(int side, CompoundNBT pictureTag) {
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
                this.getLevel(),
                this.getBlockPos(),
                side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
    }
    
    public void rotateY(int rotUp) {
        Direction rotator = Direction.UP;
        Direction begin = Direction.SOUTH;
        Picture pictureUp = this.getPicture(rotator.get3DDataValue());
        if (pictureUp != null) {
            pictureUp.rotate(rotUp);
        }
        Picture pictureDown = this.getPicture(rotator.getOpposite().get3DDataValue());
        if (pictureDown != null) {
            pictureDown.rotate((4 - rotUp) % 4);
        }
        for (int i = 0; i < rotUp; i++) {
            PictureHolder holderBegin = this.holders[begin.get3DDataValue()];
            Direction current = begin;
            while (true) {
                Direction next = GeometryUtils.rotateBy(current, rotator.getOpposite());
                if (next == begin) {
                    break;
                }
                int c = current.get3DDataValue();
                int n = next.get3DDataValue();
                PictureHolder nextHolder = this.holders[n];
                this.holders[n] = null;
                if (nextHolder != null) {
                    nextHolder.setSide(c);
                }
                this.holders[c] = nextHolder;
                current = next;
            }
            int c = current.get3DDataValue();
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
                                 TileEntityPaintingFrame.this.getBlockPos().getX(),
                                 TileEntityPaintingFrame.this.getBlockPos().getY(),
                                 TileEntityPaintingFrame.this.getBlockPos().getZ(),
                                 Direction.from3DDataValue(this.side));
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return Core.instance.painting.clientConfig.renderPaintingFrameTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPictures() {
        SideUtils.runSync(this.level != null && !this.level.isClientSide, this,
                          ()->Arrays.stream(this.holders)
                                    .filter(Objects::nonNull)
                                    .map(h->h.picture)
                                    .forEach(Picture::unload));
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
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
