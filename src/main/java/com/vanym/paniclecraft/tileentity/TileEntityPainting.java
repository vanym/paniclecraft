package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityPainting extends TileEntityPaintingContainer {
    
    public static final String IN_MOD_ID = "painting";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    protected final Picture picture = new Picture(new PictureHolder());
    
    public static final String TAG_PICTURE = "Picture";
    
    public TileEntityPainting() {
        super(Core.instance.painting.tileEntityPainting);
    }
    
    @Override
    public CompoundNBT save(CompoundNBT nbtTag) {
        return SideUtils.callSync(this.level != null && !this.level.isClientSide,
                                  this, ()->this.writeAsync(nbtTag));
    }
    
    protected CompoundNBT writeAsync(CompoundNBT nbtTag) {
        super.save(nbtTag);
        nbtTag.put(TAG_PICTURE, this.getPicture().serializeNBT());
        return nbtTag;
    }
    
    @Override
    public void load(CompoundNBT nbtTag) {
        SideUtils.runSync(this.level != null && !this.level.isClientSide,
                          this, ()->this.readAsync(nbtTag));
    }
    
    protected void readAsync(CompoundNBT nbtTag) {
        super.load(nbtTag);
        if (nbtTag.contains(TAG_PICTURE)) {
            this.getPicture().deserializeNBT(nbtTag.getCompound(TAG_PICTURE));
        }
    }
    
    public Picture getPicture() {
        return this.picture;
    }
    
    @Override
    public Picture getPicture(int side) {
        if (side == this.getBlockState().getValue(BlockPainting.FACING).get3DDataValue()) {
            return this.getPicture();
        } else {
            return null;
        }
    }
    
    protected Picture getNeighborPicture(int offsetX, int offsetY) {
        int side = this.getBlockState().getValue(BlockPainting.FACING).get3DDataValue();
        return new WorldPicturePoint(
                WorldPictureProvider.PAINTING,
                this.getLevel(),
                this.getBlockPos(),
                side).getNeighborPoint(offsetX, offsetY).getOrCreatePicture();
    }
    
    @Override
    public String toString() {
        return String.format("Painting[x=%d, y=%d, z=%d, facing=%s]",
                             this.getBlockPos().getX(), this.getBlockPos().getY(),
                             this.getBlockPos().getZ(),
                             this.getBlockState().getValue(BlockPainting.FACING));
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
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return Core.instance.painting.clientConfig.renderPaintingTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPicture() {
        SideUtils.runSync(this.level != null && !this.level.isClientSide,
                          this, this.picture::unload);
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        this.unloadPicture();
    }
    
    @Override
    public void onChunkUnloaded() {
        this.unloadPicture();
    }
    
    @Override
    public void onWorldUnload() {
        this.unloadPicture();
    }
}
