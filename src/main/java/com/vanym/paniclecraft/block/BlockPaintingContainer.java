package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class BlockPaintingContainer extends BlockContainerMod3 {
    
    protected final double paintingOutlineSize;
    
    public BlockPaintingContainer(Material material) {
        super(material);
        this.paintingOutlineSize = (1.0D / 16D);
    }
    
    public double getPaintingOutlineSize() {
        return this.paintingOutlineSize;
    }
    
    public static int getRotate(Entity player, EnumFacing side, boolean place) {
        if (side != EnumFacing.DOWN && side != EnumFacing.UP) {
            return 0;
        }
        int rot = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if ((side == EnumFacing.UP) != place) {
            rot = (4 - rot) % 4;
        }
        return rot;
    }
    
    public static void rotatePicture(
            EntityPlayer player,
            Picture picture,
            EnumFacing side,
            boolean place) {
        int rot = getRotate(player, side, place);
        picture.rotate(rot);
    }
    
    public static EnumFacing getStackDirection(EntityPlayer player, EnumFacing side) {
        EnumFacing dir = side.getOpposite();
        Vec3d dirvec = new Vec3d(dir.getDirectionVec());
        Vec3d lookvec = new Vec3d(EnumFacing.SOUTH.getDirectionVec());
        lookvec = lookvec.rotatePitch(-(player.rotationPitch * 0.999F) * (float)Math.PI / 180.0F);
        lookvec = lookvec.rotateYaw(-player.rotationYaw * (float)Math.PI / 180.0F);
        Vec3d stackvec = lookvec.subtract(dirvec);
        EnumFacing stackdir = GeometryUtils.getDirectionByVec(stackvec);
        if (stackdir == dir || stackdir == side) {
            return null;
        }
        return stackdir;
    }
}
