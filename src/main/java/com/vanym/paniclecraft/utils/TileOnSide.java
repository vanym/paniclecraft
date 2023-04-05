package com.vanym.paniclecraft.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public final class TileOnSide {
    
    public final EnumFacing xDir;
    public final EnumFacing yDir;
    public final EnumFacing zDir;
    
    public TileOnSide(EnumFacing xDir, EnumFacing zDir) {
        this(xDir, GeometryUtils.rotateBy(xDir, zDir), zDir);
    }
    
    public TileOnSide(EnumFacing xDir, EnumFacing yDir, EnumFacing zDir) {
        this.xDir = xDir;
        this.yDir = yDir;
        this.zDir = zDir;
    }
    
    public Vec3d toSideCoords(Vec3d vec) {
        return new Vec3d(
                getCoord(vec, this.xDir.getDirectionVec()),
                getCoord(vec, this.yDir.getDirectionVec()),
                getCoord(vec, this.zDir.getDirectionVec()));
    }
    
    public AxisAlignedBB toSideCoords(AxisAlignedBB box) {
        Vec3d min = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d max = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d picmin = this.toSideCoords(min);
        Vec3d picmax = this.toSideCoords(max);
        return GeometryUtils.makeBox(picmin, picmax);
    }
    
    public Vec3d fromSideCoords(Vec3d vec) {
        return Vec3d.ZERO.add(makeCoordOffset(this.xDir.getDirectionVec(), vec.x))
                         .add(makeCoordOffset(this.yDir.getDirectionVec(), vec.y))
                         .add(makeCoordOffset(this.zDir.getDirectionVec(), vec.z));
    }
    
    public AxisAlignedBB fromSideCoords(AxisAlignedBB box) {
        Vec3d picmin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d picmax = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d min = this.fromSideCoords(picmin);
        Vec3d max = this.fromSideCoords(picmax);
        return GeometryUtils.makeBox(min, max);
    }
    
    protected static double getCoord(Vec3d vec, Vec3i dir) {
        return (0.5D * Math.abs(dir.getX()) + (-0.5D + vec.x) * dir.getX()) +
               (0.5D * Math.abs(dir.getY()) + (-0.5D + vec.y) * dir.getY()) +
               (0.5D * Math.abs(dir.getZ()) + (-0.5D + vec.z) * dir.getZ());
    }
    
    protected static Vec3d makeCoordOffset(Vec3i dir, double coord) {
        return new Vec3d(
                0.5D * Math.abs(dir.getX()) + (-0.5D + coord) * dir.getX(),
                0.5D * Math.abs(dir.getY()) + (-0.5D + coord) * dir.getY(),
                0.5D * Math.abs(dir.getZ()) + (-0.5D + coord) * dir.getZ());
    }
}
