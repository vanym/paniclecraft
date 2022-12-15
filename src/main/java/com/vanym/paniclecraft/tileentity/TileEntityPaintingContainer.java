package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;

import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityPaintingContainer extends TileEntity
        implements
            ISidePictureProvider {
    
    public TileEntity getNeighborTile(int side, int xOffset, int yOffset) {
        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;
        switch (side) {
            case 0:
                x -= xOffset;
                z += yOffset;
            break;
            case 1:
                x -= xOffset;
                z -= yOffset;
            break;
            case 2:
                x -= xOffset;
                y -= yOffset;
            break;
            case 3:
                x += xOffset;
                y -= yOffset;
            break;
            case 4:
                z += xOffset;
                y -= yOffset;
            break;
            case 5:
                z -= xOffset;
                y -= yOffset;
            break;
        }
        TileEntity tile = this.getWorldObj().getTileEntity(x, y, z);
        return tile;
    }
    
    public void markForUpdate() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }
    
    public abstract void onWorldUnload();
}
