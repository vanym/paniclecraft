package com.vanym.paniclecraft.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;

public class GeometryUtils {
    
    protected static final AxisAlignedBB FULL_BLOCK = VoxelShapes.fullCube().getBoundingBox();
    
    public static AxisAlignedBB getFullBlockBox() {
        return FULL_BLOCK;
    }
    
    public static AxisAlignedBB setMinX(AxisAlignedBB box, double minX) {
        return new AxisAlignedBB(minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }
    
    public static AxisAlignedBB setMinY(AxisAlignedBB box, double minY) {
        return new AxisAlignedBB(box.minX, minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }
    
    public static AxisAlignedBB setMinZ(AxisAlignedBB box, double minZ) {
        return new AxisAlignedBB(box.minX, box.minY, minZ, box.maxX, box.maxY, box.maxZ);
    }
    
    public static AxisAlignedBB setMaxX(AxisAlignedBB box, double maxX) {
        return new AxisAlignedBB(box.minX, box.minY, box.minZ, maxX, box.maxY, box.maxZ);
    }
    
    public static AxisAlignedBB setMaxY(AxisAlignedBB box, double maxY) {
        return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, maxY, box.maxZ);
    }
    
    public static AxisAlignedBB setMaxZ(AxisAlignedBB box, double maxZ) {
        return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, maxZ);
    }
    
    public static AxisAlignedBB getPointBox(double x, double y, double z) {
        return new AxisAlignedBB(x, y, z, x, y, z);
    }
    
    public static AxisAlignedBB makeBox(Vec3d f, Vec3d s) {
        return new AxisAlignedBB(f.x, f.y, f.z, s.x, s.y, s.z);
    }
    
    public static AxisAlignedBB getBoundsBySide(int side, double width) {
        AxisAlignedBB box = setMaxZ(FULL_BLOCK, width);
        Direction zdir = Direction.byIndex(side).getOpposite();
        TileOnSide tside = getZTileOnSide(zdir);
        return tside.fromSideCoords(box);
    }
    
    public static boolean isTouchingSide(Direction side, AxisAlignedBB box) {
        if (box == null) {
            return false;
        }
        Direction zdir = side.getOpposite();
        TileOnSide tside = getZTileOnSide(zdir);
        AxisAlignedBB sideBox = tside.toSideCoords(box);
        return sideBox.minZ <= 0.0D;
    }
    
    public static Vec3d getInBlockVec(BlockRayTraceResult target) {
        return target.getHitVec().subtract(new Vec3d(target.getPos()));
    }
    
    public static AxisAlignedBB rotateXYInnerEdge(AxisAlignedBB box, double radians) {
        return rotateXYInnerEdge(box, radians, 0.5D, 0.5D);
    }
    
    public static AxisAlignedBB rotateXYInnerEdge(
            AxisAlignedBB box,
            double radians,
            double centerX,
            double centerY) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        
        double xl = box.minX - centerX, xr = box.maxX - centerX;
        double yt = box.minY - centerY, yb = box.maxY - centerY;
        
        double nxlt = xl * cos - yt * sin;
        double nylt = yt * cos + xl * sin;
        
        double nxrt = xr * cos - yt * sin;
        double nyrt = yt * cos + xr * sin;
        
        double nxlb = xl * cos - yb * sin;
        double nylb = yb * cos + xl * sin;
        
        double nxrb = xr * cos - yb * sin;
        double nyrb = yb * cos + xr * sin;
        
        double mul2 = (2.0D + Math.atan2(sin, cos) * 2.0D / Math.PI) % 1.0D;
        double mul1 = 1.0D - mul2;
        
        double nx1 = nxlt * mul1 + nxlb * mul2;
        double ny1 = nylt * mul1 + nylb * mul2;
        
        double nx2 = nxrb * mul1 + nxrt * mul2;
        double ny2 = nyrb * mul1 + nyrt * mul2;
        
        return new AxisAlignedBB(
                centerX + nx1,
                centerY + ny1,
                box.minZ,
                centerX + nx2,
                centerY + ny2,
                box.maxZ);
    }
    
    public static Direction getDirectionByVec(Vec3d lookVec) {
        return Direction.getFacingFromVector((float)lookVec.x, (float)lookVec.y, (float)lookVec.z);
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player, double distance) {
        Vec3d pos = player.getEyePosition(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d posTo = pos.add(look.scale(distance));
        return player.world.rayTraceBlocks(new RayTraceContext(
                pos,
                posTo,
                RayTraceContext.BlockMode.OUTLINE,
                RayTraceContext.FluidMode.NONE,
                player));
    }
    
    public static Direction rotateBy(Direction dir, Direction axis) {
        if (dir == null) {
            return null;
        }
        if (dir.getAxis() == axis.getAxis()) {
            return dir;
        }
        dir = dir.rotateAround(axis.getAxis());
        if (axis.getAxisDirection() == AxisDirection.NEGATIVE) {
            dir = dir.getOpposite();
        }
        return dir;
    }
    
    protected static TileOnSide getZTileOnSide(Direction zdir) {
        Direction xdir = Direction.byIndex((zdir.getIndex() + 2) % 6);
        return new TileOnSide(xdir, zdir);
    }
}
