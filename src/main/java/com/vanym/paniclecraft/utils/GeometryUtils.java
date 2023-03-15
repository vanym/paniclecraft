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
