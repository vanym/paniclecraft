package com.vanym.paniclecraft.core.component.painting;

import javax.annotation.Nonnull;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.util.EnumFacing;

public enum PaintingSide {
    DOWN(EnumFacing.WEST, EnumFacing.SOUTH), // -Y
    UP(EnumFacing.WEST, EnumFacing.NORTH), // +Y
    NORTH(EnumFacing.WEST, EnumFacing.DOWN), // -Z
    SOUTH(EnumFacing.EAST, EnumFacing.DOWN), // +Z
    WEST(EnumFacing.SOUTH, EnumFacing.DOWN), // -X
    EAST(EnumFacing.NORTH, EnumFacing.DOWN); // +X
    
    public final TileOnSide axes;
    
    PaintingSide(EnumFacing xDir, EnumFacing yDir) {
        this.axes = new TileOnSide(xDir, yDir, EnumFacing.getFront(this.ordinal()));
    }
    
    public static PaintingSide getSide(@Nonnull EnumFacing side) {
        return getSide(side.ordinal());
    }
    
    public static PaintingSide getSide(int side) {
        return values()[side % values().length];
    }
}
