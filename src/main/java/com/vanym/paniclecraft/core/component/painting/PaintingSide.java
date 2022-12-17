package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public enum PaintingSide {
    DOWN(ForgeDirection.WEST, ForgeDirection.SOUTH), // -Y
    UP(ForgeDirection.WEST, ForgeDirection.NORTH), // +Y
    NORTH(ForgeDirection.WEST, ForgeDirection.DOWN), // -Z
    SOUTH(ForgeDirection.EAST, ForgeDirection.DOWN), // +Z
    WEST(ForgeDirection.SOUTH, ForgeDirection.DOWN), // -X
    EAST(ForgeDirection.NORTH, ForgeDirection.DOWN), // +X
    UNKNOWN(ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN);
    
    public final ForgeDirection xDir;
    public final ForgeDirection yDir;
    public final ForgeDirection zDir;
    
    PaintingSide(ForgeDirection xDir, ForgeDirection yDir) {
        this.xDir = xDir;
        this.yDir = yDir;
        this.zDir = ForgeDirection.getOrientation(this.ordinal());
    }
    
    public static PaintingSide getSize(int id) {
        return values()[id % values().length];
    }
    
    public Vec3 toPaintingVec(Vec3 vec) {
        return Vec3.createVectorHelper(getCoord(vec, this.xDir),
                                       getCoord(vec, this.yDir),
                                       getCoord(vec, this.zDir));
    }
    
    protected static double getCoord(Vec3 vec, ForgeDirection dir) {
        if (dir.offsetX > 0) {
            return vec.xCoord;
        } else if (dir.offsetX < 0) {
            return 1.0D - vec.xCoord;
        } else if (dir.offsetY > 0) {
            return vec.yCoord;
        } else if (dir.offsetY < 0) {
            return 1.0D - vec.yCoord;
        } else if (dir.offsetZ > 0) {
            return vec.zCoord;
        } else if (dir.offsetZ < 0) {
            return 1.0D - vec.zCoord;
        }
        return 0.0D;
    }
    
    public Vec3 fromPaintingVec(Vec3 vec) {
        Vec3 ret = Vec3.createVectorHelper(0, 0, 0);
        putCoord(ret, vec.xCoord, this.xDir);
        putCoord(ret, vec.yCoord, this.yDir);
        putCoord(ret, vec.zCoord, this.zDir);
        return ret;
    }
    
    protected static void putCoord(Vec3 vec, double coord, ForgeDirection dir) {
        if (dir.offsetX > 0) {
            vec.xCoord = coord;
        } else if (dir.offsetX < 0) {
            vec.xCoord = 1.0D - coord;
        } else if (dir.offsetY > 0) {
            vec.yCoord = coord;
        } else if (dir.offsetY < 0) {
            vec.yCoord = 1.0D - coord;
        } else if (dir.offsetZ > 0) {
            vec.zCoord = coord;
        } else if (dir.offsetZ < 0) {
            vec.zCoord = 1.0D - coord;
        }
    }
}
