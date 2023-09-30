package com.vanym.paniclecraft.block;

import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class BlockPaintingContainer extends ContainerBlock {
    
    protected final double paintingOutlineSize;
    
    public BlockPaintingContainer(Block.Properties properties) {
        super(properties);
        this.paintingOutlineSize = (1.0D / 16D);
    }
    
    public double getPaintingOutlineSize() {
        return this.paintingOutlineSize;
    }
    
    @Override
    public boolean onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        if (!Core.instance.painting.config.openViewByClick
            || player.isSneaking()
            || Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
                     .map(player::getHeldItem)
                     .anyMatch(stack->!stack.isEmpty())) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        WorldPicturePoint point =
                new WorldPicturePoint(
                        WorldPictureProvider.ANYTILE,
                        world,
                        pos,
                        hit.getFace().getIndex());
        ContainerPaintingViewServer.Provider view =
                ContainerPaintingViewServer.makeFullView(point, 128);
        if (view != null && player instanceof ServerPlayerEntity) {
            view.setEditable(player.abilities.isCreativeMode && player.hasPermissionLevel(2));
            NetworkHooks.openGui((ServerPlayerEntity)player, view, view);
        }
        return true;
    }
    
    public static int getRotate(Entity player, Direction side, boolean place) {
        if (side != Direction.DOWN && side != Direction.UP) {
            return 0;
        }
        int rot = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if ((side == Direction.UP) != place) {
            rot = (4 - rot) % 4;
        }
        return rot;
    }
    
    public static void rotatePicture(
            PlayerEntity player,
            Picture picture,
            Direction side,
            boolean place) {
        int rot = getRotate(player, side, place);
        picture.rotate(rot);
    }
    
    public static Direction getStackDirection(PlayerEntity player, Direction side) {
        Direction dir = side.getOpposite();
        Vec3d dirvec = new Vec3d(dir.getDirectionVec());
        Vec3d lookvec = new Vec3d(Direction.SOUTH.getDirectionVec());
        lookvec = lookvec.rotatePitch(-(player.rotationPitch * 0.999F) * (float)Math.PI / 180.0F);
        lookvec = lookvec.rotateYaw(-player.rotationYaw * (float)Math.PI / 180.0F);
        Vec3d stackvec = lookvec.subtract(dirvec);
        Direction stackdir = GeometryUtils.getDirectionByVec(stackvec);
        if (stackdir == dir || stackdir == side) {
            return null;
        }
        return stackdir;
    }
}
