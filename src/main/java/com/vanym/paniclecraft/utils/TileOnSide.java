package com.vanym.paniclecraft.utils;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public final class TileOnSide {
    
    public final Direction xDir;
    public final Direction yDir;
    public final Direction zDir;
    
    public TileOnSide(Direction xDir, Direction zDir) {
        this(xDir, GeometryUtils.rotateBy(xDir, zDir), zDir);
    }
    
    public TileOnSide(Direction xDir, Direction yDir, Direction zDir) {
        this.xDir = xDir;
        this.yDir = yDir;
        this.zDir = zDir;
    }
    
    public Vector3d toSideCoords(Vector3d vec) {
        return new Vector3d(
                getCoord(vec, this.xDir.getNormal()),
                getCoord(vec, this.yDir.getNormal()),
                getCoord(vec, this.zDir.getNormal()));
    }
    
    public AxisAlignedBB toSideCoords(AxisAlignedBB box) {
        Vector3d min = new Vector3d(box.minX, box.minY, box.minZ);
        Vector3d max = new Vector3d(box.maxX, box.maxY, box.maxZ);
        Vector3d picmin = this.toSideCoords(min);
        Vector3d picmax = this.toSideCoords(max);
        return GeometryUtils.makeBox(picmin, picmax);
    }
    
    public Vector3d fromSideCoords(Vector3d vec) {
        return Vector3d.ZERO.add(makeCoordOffset(this.xDir.getNormal(), vec.x))
                            .add(makeCoordOffset(this.yDir.getNormal(), vec.y))
                            .add(makeCoordOffset(this.zDir.getNormal(), vec.z));
    }
    
    public AxisAlignedBB fromSideCoords(AxisAlignedBB box) {
        Vector3d picmin = new Vector3d(box.minX, box.minY, box.minZ);
        Vector3d picmax = new Vector3d(box.maxX, box.maxY, box.maxZ);
        Vector3d min = this.fromSideCoords(picmin);
        Vector3d max = this.fromSideCoords(picmax);
        return GeometryUtils.makeBox(min, max);
    }
    
    protected static double getCoord(Vector3d vec, Vector3i dir) {
        return (0.5D * Math.abs(dir.getX()) + (-0.5D + vec.x) * dir.getX()) +
               (0.5D * Math.abs(dir.getY()) + (-0.5D + vec.y) * dir.getY()) +
               (0.5D * Math.abs(dir.getZ()) + (-0.5D + vec.z) * dir.getZ());
    }
    
    protected static Vector3d makeCoordOffset(Vector3i dir, double coord) {
        return new Vector3d(
                0.5D * Math.abs(dir.getX()) + (-0.5D + coord) * dir.getX(),
                0.5D * Math.abs(dir.getY()) + (-0.5D + coord) * dir.getY(),
                0.5D * Math.abs(dir.getZ()) + (-0.5D + coord) * dir.getZ());
    }
}
