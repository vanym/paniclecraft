package com.vanym.paniclecraft.utils;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.VoxelShapes;

public class GeometryUtils {
    
    protected static final AxisAlignedBB FULL_BLOCK = VoxelShapes.block().bounds();
    protected static final Vec3d CENTER_VEC3D = new Vec3d(0.5D, 0.5D, 0.5D);
    
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
        Direction zdir = Direction.from3DDataValue(side).getOpposite();
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
        return target.getLocation().subtract(new Vec3d(target.getBlockPos()));
    }
    
    public static Vec3d getCenterVec3d() {
        return CENTER_VEC3D;
    }
    
    public static Vec3d getCenter(Vec3i vec) {
        return new Vec3d(vec).add(CENTER_VEC3D);
    }
    
    public static Vec3d createVec3d(Entity entity) {
        return new Vec3d(entity.getX(), entity.getY(), entity.getZ());
    }
    
    public static Vec3i mul(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(
                vec1.getX() * vec2.getX(),
                vec1.getY() * vec2.getY(),
                vec1.getZ() * vec2.getZ());
    }
    
    public static Vec3d mul(Vec3i vec1, Vec3d vec2) {
        return mul(vec2, vec1);
    }
    
    public static Vec3d mul(Vec3d vec1, Vec3i vec2) {
        return mul(vec1, new Vec3d(vec2));
    }
    
    public static Vec3d mul(Vec3d vec1, Vec3d vec2) {
        return vec1.multiply(vec2);
    }
    
    public static void acceptVec3d(Vec3d vec, Vec3dConsumer consumer) {
        consumer.accept(vec.x, vec.y, vec.z);
    }
    
    public static void acceptVec3f(Vec3d vec, Vec3fConsumer consumer) {
        consumer.accept((float)vec.x, (float)vec.y, (float)vec.z);
    }
    
    public static void acceptVec3d(Vector3f vec, Vec3dConsumer consumer) {
        consumer.accept(vec.x(), vec.y(), vec.z());
    }
    
    public static void acceptVec3f(Vector3f vec, Vec3fConsumer consumer) {
        consumer.accept(vec.x(), vec.y(), vec.z());
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
        return Direction.getNearest((float)lookVec.x, (float)lookVec.y, (float)lookVec.z);
    }
    
    public static BlockRayTraceResult rayTraceBlocks(PlayerEntity player, double distance) {
        Vec3d pos = player.getEyePosition(1.0F);
        Vec3d look = player.getLookAngle();
        Vec3d posTo = pos.add(look.scale(distance));
        return player.level.clip(new RayTraceContext(
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
        dir = rotateBy(dir, axis.getAxis());
        if (axis.getAxisDirection() == AxisDirection.NEGATIVE) {
            dir = dir.getOpposite();
        }
        return dir;
    }
    
    protected static Direction rotateBy(Direction dir, Direction.Axis axis) {
        switch (axis) {
            case X:
                if (dir != Direction.WEST && dir != Direction.EAST) {
                    return rotateByX(dir);
                }
                return dir;
            case Y:
                if (dir != Direction.UP && dir != Direction.DOWN) {
                    return dir.getClockWise();
                }
                return dir;
            case Z:
                if (dir != Direction.NORTH && dir != Direction.SOUTH) {
                    return rotateByZ(dir);
                }
                return dir;
            default:
                throw new IllegalStateException();
        }
    }
    
    protected static Direction rotateByX(Direction dir) {
        switch (dir) {
            case NORTH:
                return Direction.DOWN;
            case EAST:
            case WEST:
            default:
                throw new IllegalStateException();
            case SOUTH:
                return Direction.UP;
            case UP:
                return Direction.NORTH;
            case DOWN:
                return Direction.SOUTH;
        }
    }
    
    protected static Direction rotateByZ(Direction dir) {
        switch (dir) {
            case EAST:
                return Direction.DOWN;
            case SOUTH:
            default:
                throw new IllegalStateException();
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.EAST;
            case DOWN:
                return Direction.WEST;
        }
    }
    
    protected static TileOnSide getZTileOnSide(Direction zdir) {
        Direction xdir = Direction.from3DDataValue((zdir.get3DDataValue() + 2) % 6);
        return new TileOnSide(xdir, zdir);
    }
    
    @FunctionalInterface
    public static abstract interface Vec3dConsumer {
        public abstract void accept(double x, double y, double z);
    }
    
    @FunctionalInterface
    public static abstract interface Vec3fConsumer {
        public abstract void accept(float x, float y, float z);
    }
}
