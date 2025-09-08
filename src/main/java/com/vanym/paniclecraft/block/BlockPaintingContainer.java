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
import net.minecraft.util.ActionResultType;
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
    public ActionResultType use(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        if (!Core.instance.painting.config.openViewByClick
            || player.isSecondaryUseActive()
            || Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
                     .map(player::getItemInHand)
                     .anyMatch(stack->!stack.isEmpty())) {
            return ActionResultType.PASS;
        }
        WorldPicturePoint point =
                new WorldPicturePoint(
                        WorldPictureProvider.ANYTILE,
                        world,
                        pos,
                        hit.getDirection().get3DDataValue());
        if (world.isClientSide) {
            return point.getPicture() != null ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        ContainerPaintingViewServer.Provider view =
                ContainerPaintingViewServer.makeFullView(point, 128);
        if (view == null) {
            return ActionResultType.PASS;
        }
        if (player instanceof ServerPlayerEntity) {
            view.setEditable(player.abilities.instabuild && player.hasPermissions(2));
            NetworkHooks.openGui((ServerPlayerEntity)player, view, view);
        }
        return ActionResultType.SUCCESS;
    }
    
    public static int getRotate(Entity player, Direction side, boolean place) {
        if (side != Direction.DOWN && side != Direction.UP) {
            return 0;
        }
        int rot = MathHelper.floor((double)(player.yRot * 4.0F / 360.0F) + 0.5D) & 3;
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
        Vec3d dirvec = new Vec3d(dir.getNormal());
        Vec3d lookvec = new Vec3d(Direction.SOUTH.getNormal());
        lookvec = lookvec.xRot(-(player.xRot * 0.999F) * (float)Math.PI / 180.0F);
        lookvec = lookvec.yRot(-player.yRot * (float)Math.PI / 180.0F);
        Vec3d stackvec = lookvec.subtract(dirvec);
        Direction stackdir = GeometryUtils.getDirectionByVec(stackvec);
        if (stackdir == dir || stackdir == side) {
            return null;
        }
        return stackdir;
    }
}
