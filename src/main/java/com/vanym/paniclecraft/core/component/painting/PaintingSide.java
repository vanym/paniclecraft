package com.vanym.paniclecraft.core.component.painting;

import javax.annotation.Nonnull;

import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.util.Direction;

public enum PaintingSide {
    DOWN(Direction.WEST, Direction.SOUTH), // -Y
    UP(Direction.WEST, Direction.NORTH), // +Y
    NORTH(Direction.WEST, Direction.DOWN), // -Z
    SOUTH(Direction.EAST, Direction.DOWN), // +Z
    WEST(Direction.SOUTH, Direction.DOWN), // -X
    EAST(Direction.NORTH, Direction.DOWN); // +X
    
    public final TileOnSide axes;
    
    PaintingSide(Direction xDir, Direction yDir) {
        this.axes = new TileOnSide(xDir, yDir, Direction.byIndex(this.ordinal()));
    }
    
    public static PaintingSide getSide(@Nonnull Direction side) {
        return getSide(side.ordinal());
    }
    
    public static PaintingSide getSide(int side) {
        return values()[Math.abs(side) % values().length];
    }
}
