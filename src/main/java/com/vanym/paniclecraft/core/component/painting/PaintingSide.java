package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraftforge.common.util.ForgeDirection;

public enum PaintingSide {
    DOWN(ForgeDirection.WEST, ForgeDirection.SOUTH), // -Y
    UP(ForgeDirection.WEST, ForgeDirection.NORTH), // +Y
    NORTH(ForgeDirection.WEST, ForgeDirection.DOWN), // -Z
    SOUTH(ForgeDirection.EAST, ForgeDirection.DOWN), // +Z
    WEST(ForgeDirection.SOUTH, ForgeDirection.DOWN), // -X
    EAST(ForgeDirection.NORTH, ForgeDirection.DOWN), // +X
    UNKNOWN(ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN);
    
    public final TileOnSide axes;
    
    PaintingSide(ForgeDirection xDir, ForgeDirection yDir) {
        this.axes = new TileOnSide(xDir, yDir, ForgeDirection.getOrientation(this.ordinal()));
    }
    
    public static PaintingSide getSide(int side) {
        return values()[side % values().length];
    }
}
