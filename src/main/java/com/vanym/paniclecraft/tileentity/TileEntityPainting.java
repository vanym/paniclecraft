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
    public CompoundNBT write(CompoundNBT nbtTag) {
        return SideUtils.callSync(this.world != null && !this.world.isRemote,
                                  this, ()->this.writeAsync(nbtTag));
    }
    
    protected CompoundNBT writeAsync(CompoundNBT nbtTag) {
        super.write(nbtTag);
        nbtTag.put(TAG_PICTURE, this.getPicture().serializeNBT());
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, ()->this.readAsync(nbtTag));
    }
    
    protected void readAsync(CompoundNBT nbtTag) {
        super.read(nbtTag);
        if (nbtTag.contains(TAG_PICTURE)) {
            this.getPicture().deserializeNBT(nbtTag.getCompound(TAG_PICTURE));
        }
    }
    
    public Picture getPicture() {
        return this.picture;
    }
    
    @Override
    public Picture getPicture(int side) {
        if (side == this.getBlockState().get(BlockPainting.FACING).getIndex()) {
            return this.getPicture();
        } else {
            return null;
        }
    }
    
    protected Picture getNeighborPicture(int offsetX, int offsetY) {
        int side = this.getBlockState().get(BlockPainting.FACING).getIndex();
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
                             this.getBlockState().get(BlockPainting.FACING));
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
    public double getMaxRenderDistanceSquared() {
        return Core.instance.painting.clientConfig.renderPaintingTileMaxRenderDistanceSquared;
    }
    
    protected void unloadPicture() {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, this.picture::unload);
    }
    
    @Override
    public void remove() {
        super.remove();
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
