package com.vanym.paniclecraft.core.component.advsign;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.util.EnumFacing;

public enum AdvSignSide {
    DOWN(EnumFacing.WEST, -1), // -Y
    UP(EnumFacing.EAST), // +Y
    NORTH(EnumFacing.WEST), // -Z
    SOUTH(EnumFacing.EAST), // +Z
    WEST(EnumFacing.SOUTH), // -X
    EAST(EnumFacing.NORTH); // +X
    
    public final TileOnSide axes;
    public final int zAxis;
    
    AdvSignSide(EnumFacing xDir) {
        this(xDir, 1);
    }
    
    AdvSignSide(EnumFacing xDir, int zAxis) {
        this.axes = new TileOnSide(xDir, EnumFacing.getFront(this.ordinal()));
        this.zAxis = zAxis;
    }
    
    public static AdvSignSide getSide(int side) {
        return values()[Math.abs(side) % values().length];
    }
}
