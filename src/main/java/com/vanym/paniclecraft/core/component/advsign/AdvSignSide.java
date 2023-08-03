package com.vanym.paniclecraft.core.component.advsign;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.util.Direction;

public enum AdvSignSide {
    DOWN(Direction.WEST, -1), // -Y
    UP(Direction.EAST), // +Y
    NORTH(Direction.WEST), // -Z
    SOUTH(Direction.EAST), // +Z
    WEST(Direction.SOUTH), // -X
    EAST(Direction.NORTH); // +X
    
    public final TileOnSide axes;
    public final int zAxis;
    
    AdvSignSide(Direction xDir) {
        this(xDir, 1);
    }
    
    AdvSignSide(Direction xDir, int zAxis) {
        this.axes = new TileOnSide(xDir, Direction.byIndex(this.ordinal()));
        this.zAxis = zAxis;
    }
    
    public static AdvSignSide getSide(int side) {
        return values()[Math.abs(side) % values().length];
    }
}
