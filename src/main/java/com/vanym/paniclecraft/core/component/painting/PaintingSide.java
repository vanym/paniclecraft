package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.util.AxisAlignedBB;
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
    
    public static PaintingSide getSide(int id) {
        return values()[id % values().length];
    }
    
    public Vec3 toPaintingCoords(Vec3 vec) {
        return Vec3.createVectorHelper(getCoord(vec, this.xDir),
                                       getCoord(vec, this.yDir),
                                       getCoord(vec, this.zDir));
    }
    
    public AxisAlignedBB toPaintingCoords(AxisAlignedBB box) {
        Vec3 min = Vec3.createVectorHelper(box.minX, box.minY, box.minZ);
        Vec3 max = Vec3.createVectorHelper(box.maxX, box.maxY, box.maxZ);
        Vec3 picmin = this.toPaintingCoords(min);
        Vec3 picmax = this.toPaintingCoords(max);
        return AxisAlignedBB.getBoundingBox(picmin.xCoord, picmin.yCoord, picmin.zCoord,
                                            picmax.xCoord, picmax.yCoord, picmax.zCoord);
    }
    
    protected static double getCoord(Vec3 vec, ForgeDirection dir) {
        return (0.5D * Math.abs(dir.offsetX) + (-0.5D + vec.xCoord) * dir.offsetX) +
               (0.5D * Math.abs(dir.offsetY) + (-0.5D + vec.yCoord) * dir.offsetY) +
               (0.5D * Math.abs(dir.offsetZ) + (-0.5D + vec.zCoord) * dir.offsetZ);
    }
    
    public Vec3 fromPaintingCoords(Vec3 vec) {
        Vec3 ret = Vec3.createVectorHelper(0, 0, 0);
        putCoord(ret, vec.xCoord, this.xDir);
        putCoord(ret, vec.yCoord, this.yDir);
        putCoord(ret, vec.zCoord, this.zDir);
        return ret;
    }
    
    public AxisAlignedBB fromPaintingCoords(AxisAlignedBB box) {
        Vec3 picmin = Vec3.createVectorHelper(box.minX, box.minY, box.minZ);
        Vec3 picmax = Vec3.createVectorHelper(box.maxX, box.maxY, box.maxZ);
        Vec3 min = this.fromPaintingCoords(picmin);
        Vec3 max = this.fromPaintingCoords(picmax);
        return AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord,
                                            max.xCoord, max.yCoord, max.zCoord);
    }
    
    protected static void putCoord(Vec3 vec, double coord, ForgeDirection dir) {
        vec.xCoord += 0.5D * Math.abs(dir.offsetX) + (-0.5D + coord) * dir.offsetX;
        vec.yCoord += 0.5D * Math.abs(dir.offsetY) + (-0.5D + coord) * dir.offsetY;
        vec.zCoord += 0.5D * Math.abs(dir.offsetZ) + (-0.5D + coord) * dir.offsetZ;
    }
}
