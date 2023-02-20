package com.vanym.paniclecraft.utils;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public final class TileOnSide {
    
    public final ForgeDirection xDir;
    public final ForgeDirection yDir;
    public final ForgeDirection zDir;
    
    public TileOnSide(ForgeDirection xDir, ForgeDirection zDir) {
        this(xDir, xDir.getRotation(zDir), zDir);
    }
    
    public TileOnSide(ForgeDirection xDir, ForgeDirection yDir, ForgeDirection zDir) {
        this.xDir = xDir;
        this.yDir = yDir;
        this.zDir = zDir;
    }
    
    public Vec3 toSideCoords(Vec3 vec) {
        return Vec3.createVectorHelper(getCoord(vec, this.xDir),
                                       getCoord(vec, this.yDir),
                                       getCoord(vec, this.zDir));
    }
    
    public AxisAlignedBB toSideCoords(AxisAlignedBB box) {
        Vec3 min = Vec3.createVectorHelper(box.minX, box.minY, box.minZ);
        Vec3 max = Vec3.createVectorHelper(box.maxX, box.maxY, box.maxZ);
        Vec3 picmin = this.toSideCoords(min);
        Vec3 picmax = this.toSideCoords(max);
        return makeBox(picmin, picmax);
    }
    
    public Vec3 fromSideCoords(Vec3 vec) {
        Vec3 ret = Vec3.createVectorHelper(0, 0, 0);
        putCoord(ret, vec.xCoord, this.xDir);
        putCoord(ret, vec.yCoord, this.yDir);
        putCoord(ret, vec.zCoord, this.zDir);
        return ret;
    }
    
    public AxisAlignedBB fromSideCoords(AxisAlignedBB box) {
        Vec3 picmin = Vec3.createVectorHelper(box.minX, box.minY, box.minZ);
        Vec3 picmax = Vec3.createVectorHelper(box.maxX, box.maxY, box.maxZ);
        Vec3 min = this.fromSideCoords(picmin);
        Vec3 max = this.fromSideCoords(picmax);
        return makeBox(min, max);
    }
    
    protected static double getCoord(Vec3 vec, ForgeDirection dir) {
        return (0.5D * Math.abs(dir.offsetX) + (-0.5D + vec.xCoord) * dir.offsetX) +
               (0.5D * Math.abs(dir.offsetY) + (-0.5D + vec.yCoord) * dir.offsetY) +
               (0.5D * Math.abs(dir.offsetZ) + (-0.5D + vec.zCoord) * dir.offsetZ);
    }
    
    protected static void putCoord(Vec3 vec, double coord, ForgeDirection dir) {
        vec.xCoord += 0.5D * Math.abs(dir.offsetX) + (-0.5D + coord) * dir.offsetX;
        vec.yCoord += 0.5D * Math.abs(dir.offsetY) + (-0.5D + coord) * dir.offsetY;
        vec.zCoord += 0.5D * Math.abs(dir.offsetZ) + (-0.5D + coord) * dir.offsetZ;
    }
    
    protected static AxisAlignedBB makeBox(Vec3 f, Vec3 s) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(f.xCoord, f.yCoord, f.zCoord,
                                                         s.xCoord, s.yCoord, s.zCoord);
        if (box.minX > box.maxX) {
            double t = box.maxX;
            box.maxX = box.minX;
            box.minX = t;
        }
        if (box.minY > box.maxY) {
            double t = box.maxY;
            box.maxY = box.minY;
            box.minY = t;
        }
        if (box.minZ > box.maxZ) {
            double t = box.maxZ;
            box.maxZ = box.minZ;
            box.minZ = t;
        }
        return box;
    }
}
