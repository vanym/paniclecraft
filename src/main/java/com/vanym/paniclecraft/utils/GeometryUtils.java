package com.vanym.paniclecraft.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class GeometryUtils {
    
    protected static final AxisAlignedBB FULL_BLOCK =
            AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    
    public static AxisAlignedBB getFullBlockBox() {
        return FULL_BLOCK.copy();
    }
    
    public static AxisAlignedBB getPointBox(double x, double y, double z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
    }
    
    public static AxisAlignedBB getBoundsBySide(int side, double width) {
        AxisAlignedBB box = getFullBlockBox();
        box.maxZ = width;
        ForgeDirection zdir = ForgeDirection.getOrientation(side).getOpposite();
        TileOnSide tside = getZTileOnSide(zdir);
        return tside.fromSideCoords(box);
    }
    
    public static boolean isTouchingSide(int side, AxisAlignedBB box) {
        if (box == null) {
            return false;
        }
        ForgeDirection zdir = ForgeDirection.getOrientation(side).getOpposite();
        TileOnSide tside = getZTileOnSide(zdir);
        AxisAlignedBB sideBox = tside.toSideCoords(box);
        return sideBox.minZ <= 0.0D;
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
        
        return AxisAlignedBB.getBoundingBox(centerX + nx1, centerY + ny1, box.minZ,
                                            centerX + nx2, centerY + ny2, box.maxZ);
    }
    
    public static Vec3 getInBlockVec(MovingObjectPosition target) {
        return Vec3.createVectorHelper(target.blockX, target.blockY, target.blockZ)
                   .subtract(target.hitVec);
    }
    
    public static Vec3 getVecByDirection(ForgeDirection dir) {
        return Vec3.createVectorHelper(dir.offsetX, dir.offsetY, dir.offsetZ);
    }
    
    public static ForgeDirection getDirectionRoration(double yaw, double pitch) {
        ForgeDirection south = ForgeDirection.SOUTH; // +Z
        ForgeDirection up = ForgeDirection.UP; // +Y
        ForgeDirection pitchRotator = south.getRotation(up);
        ForgeDirection current = south;
        int pitchRot = MathHelper.floor_double((pitch * 4.0F / 360.0F) + 0.5D) & 3;
        int yawRot = MathHelper.floor_double((yaw * 4.0F / 360.0F) + 0.5D) & 3;
        for (int i = 0; i < pitchRot; ++i) {
            current = current.getRotation(pitchRotator);
        }
        for (int i = 0; i < yawRot; ++i) {
            current = current.getRotation(up);
        }
        return current;
    }
    
    public static ForgeDirection getDirectionByVec(Vec3 lookVec) {
        double lookXZ = lookVec.addVector(0, -lookVec.yCoord, 0).lengthVector();
        double pitch = -Math.atan2(lookVec.yCoord, lookXZ) * 180.0D / Math.PI;
        double yaw = -Math.atan2(lookVec.xCoord, lookVec.zCoord) * 180.0D / Math.PI;
        return getDirectionRoration(yaw, pitch);
    }
    
    public static MovingObjectPosition rayTraceBlocks(EntityPlayer player, double distance) {
        Vec3 pos = Vec3.createVectorHelper(player.posX,
                                           player.posY + player.getEyeHeight(),
                                           player.posZ);
        Vec3 look = player.getLookVec();
        Vec3 posTo = pos.addVector(look.xCoord * distance,
                                   look.yCoord * distance,
                                   look.zCoord * distance);
        return player.worldObj.rayTraceBlocks(pos, posTo);
    }
    
    protected static TileOnSide getZTileOnSide(ForgeDirection zdir) {
        ForgeDirection xdir = ForgeDirection.getOrientation((zdir.ordinal() + 2) % 6);
        return new TileOnSide(xdir, zdir);
    }
}
