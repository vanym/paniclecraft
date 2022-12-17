package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;

import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityPaintingContainer extends TileEntity
        implements
            ISidePictureProvider {
    
    public TileEntity getNeighborTile(int side, int xOffset, int yOffset) {
        PaintingSide pside = PaintingSide.getSize(side);
        int x = this.xCoord + pside.xDir.offsetX * xOffset + pside.yDir.offsetX * yOffset;
        int y = this.yCoord + pside.xDir.offsetY * xOffset + pside.yDir.offsetY * yOffset;
        int z = this.zCoord + pside.xDir.offsetZ * xOffset + pside.yDir.offsetZ * yOffset;
        TileEntity tile = this.getWorldObj().getTileEntity(x, y, z);
        return tile;
    }
    
    public void markForUpdate() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }
    
    public abstract void onWorldUnload();
}
