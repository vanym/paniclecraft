package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum WorldPictureProvider {
    PAINTING(TileEntityPainting.class),
    PAINTINGFRAME(TileEntityPaintingFrame.class),
    ANYTILE(ISidePictureProvider.class),
    PAINTONBLOCK(EntityPaintOnBlock.class) {
        @Override
        public Picture getOrCreatePicture(World world, BlockPos pos, int side) {
            return EntityPaintOnBlock.getOrCreateEntityPicture(world, pos, side);
        }
        
        @Override
        public Picture getPicture(World world, BlockPos pos, int side) {
            return EntityPaintOnBlock.getExistingPicture(world, pos, side);
        }
        
        @Override
        public boolean hasAlpha() {
            return true;
        }
    };
    
    protected final Class<? extends ISidePictureProvider> providerClass;
    
    WorldPictureProvider(Class<? extends ISidePictureProvider> providerClass) {
        this.providerClass = providerClass;
    }
    
    public Picture getOrCreatePicture(World world, BlockPos pos, int side) {
        return this.getPicture(world, pos, side);
    }
    
    public Picture getPicture(World world, BlockPos pos, int side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && this.providerClass.isAssignableFrom(tile.getClass())) {
            ISidePictureProvider provider = (ISidePictureProvider)tile;
            return provider.getPicture(side);
        }
        return null;
    }
    
    public boolean hasAlpha() {
        return false;
    }
}
