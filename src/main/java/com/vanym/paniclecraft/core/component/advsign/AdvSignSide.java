package com.vanym.paniclecraft.core.component.advsign;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraftforge.common.util.ForgeDirection;

public enum AdvSignSide {
    DOWN(ForgeDirection.WEST, -1), // -Y
    UP(ForgeDirection.EAST), // +Y
    NORTH(ForgeDirection.WEST), // -Z
    SOUTH(ForgeDirection.EAST), // +Z
    WEST(ForgeDirection.SOUTH), // -X
    EAST(ForgeDirection.NORTH), // +X
    UNKNOWN(ForgeDirection.UNKNOWN);
    
    public final TileOnSide axes;
    public final int zAxis;
    
    AdvSignSide(ForgeDirection xDir) {
        this(xDir, 1);
    }
    
    AdvSignSide(ForgeDirection xDir, int zAxis) {
        this.axes = new TileOnSide(xDir, ForgeDirection.getOrientation(this.ordinal()));
        this.zAxis = zAxis;
    }
    
    public static AdvSignSide getSide(int side) {
        return values()[Math.abs(side) % values().length];
    }
}
