package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public enum WorldPictureProvider {
    PAINTING(TileEntityPainting.class),
    PAINTINGFRAME(TileEntityPaintingFrame.class),
    ANYTILE(ISidePictureProvider.class),
    PAINTONBLOCK(EntityPaintOnBlock.class) {
        @Override
        public Picture getPicture(World world, int x, int y, int z, int side) {
            return EntityPaintOnBlock.getOrCreateEntityPicture(world, x, y, z, side);
        }
    };
    
    protected final Class<? extends ISidePictureProvider> providerClass;
    
    WorldPictureProvider(Class<? extends ISidePictureProvider> providerClass) {
        this.providerClass = providerClass;
    }
    
    public Picture getPicture(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && this.providerClass.isAssignableFrom(tile.getClass())) {
            return ((ISidePictureProvider)tile).getPicture(side);
        }
        return null;
    }
    
    public Picture getNeighborPicture(
            World world,
            int x,
            int y,
            int z,
            int side,
            int xOffset,
            int yOffset) {
        PaintingSide pside = PaintingSide.getSide(side);
        x += pside.xDir.offsetX * xOffset + pside.yDir.offsetX * yOffset;
        y += pside.xDir.offsetY * xOffset + pside.yDir.offsetY * yOffset;
        z += pside.xDir.offsetZ * xOffset + pside.yDir.offsetZ * yOffset;
        return this.getPicture(world, x, y, z, side);
    }
}
