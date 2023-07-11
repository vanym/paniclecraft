package com.vanym.paniclecraft.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPaintingFrame extends BlockPaintingContainer {
    
    protected static final BooleanProperty[] SIDES = Arrays.stream(Direction.values())
                                                           .map(Direction::getName2)
                                                           .map(BooleanProperty::create)
                                                           .toArray(BooleanProperty[]::new);
    
    protected final double frameOutlineSize;
    
    protected final List<AxisAlignedBB> frameBoxes;
    
    public BlockPaintingFrame() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(0.6F)
                              .noDrops());
        this.setRegistryName("paintingframe");
        this.frameOutlineSize = (1.0D / 16D) * 2.0D;
        this.frameBoxes = Collections.unmodifiableList(getFrameBoxes(this.frameOutlineSize));
        BlockState state = this.stateContainer.getBaseState();
        for (BooleanProperty side : SIDES) {
            state = state.with(side, false);
        }
        this.setDefaultState(state);
    }
    
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityPaintingFrame();
    }
    
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SIDES);
    }
    
    @Override
    public boolean onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity entityPlayer,
            Hand hand,
            BlockRayTraceResult hit) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)world.getTileEntity(pos);
        if (tileP == null) {
            return false;
        }
        Direction side = hit.getFace();
        Picture picture = tileP.getPicture(side.getIndex());
        if (picture == null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        if (entityPlayer != null) {
            rotatePicture(entityPlayer, picture, side, false);
        }
        Vec3d ePos = new Vec3d(pos).add(0.5, 0.5, 0.5)
                                   .add(new Vec3d(side.getDirectionVec()).scale(0.6D));
        ItemStack itemStack = ItemPainting.getPictureAsItem(picture);
        ItemEntity entityItem = new ItemEntity(world, ePos.x, ePos.y, ePos.z, itemStack);
        entityItem.setPickupDelay(3);
        world.addEntity(entityItem);
        tileP.clearPicture(side.getIndex());
        tileP.markForUpdate();
        world.notifyNeighborsOfStateChange(pos, this);
        return true;
    }
    
    @Override
    public boolean removedByPlayer(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            boolean willHarvest,
            IFluidState fluid) {
        if (player != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                int rot = getRotate(player, Direction.UP, false);
                tilePF.rotateY(rot);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
    
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame timePF = (TileEntityPaintingFrame)tile;
            ItemStack itemStack = ItemPaintingFrame.getFrameAsItem(timePF);
            spawnAsEntity(world, pos, itemStack);
        }
        super.onBlockHarvested(world, pos, state, player);
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity entity,
            ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        if (!stack.hasTag()) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
            for (Direction pside : Direction.values()) {
                ItemPaintingFrame.getPictureTag(stack, pside).ifPresent(tag-> {
                    Picture picture = tilePF.createPicture(pside.getIndex());
                    picture.deserializeNBT(tag);
                });
            }
            int rot = getRotate(entity, Direction.UP, true);
            tilePF.rotateY(rot);
        }
    }
    
    public BlockState getActualState(BlockState state, TileEntityPaintingFrame tilePF) {
        for (int i = 0; i < SIDES.length; ++i) {
            state = state.with(SIDES[i], tilePF.getPicture(i) != null);
        }
        return state;
    }
    
    public List<AxisAlignedBB> getFrameBoxes() {
        return this.frameBoxes;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            IBlockReader worldIn,
            BlockPos pos,
            ISelectionContext context) {
        Builder<AxisAlignedBB> facades = Stream.builder();
        for (int i = 0; i < 6; ++i) {
            if (state.get(SIDES[i])) {
                AxisAlignedBB box = GeometryUtils.getBoundsBySide(i, this.getPaintingOutlineSize());
                facades.add(box);
            }
        }
        List<AxisAlignedBB> boxes = this.getFrameBoxes();
        return Stream.concat(boxes.stream(), facades.build())
                     .map(VoxelShapes::create)
                     .reduce(VoxelShapes::or)
                     .orElseGet(VoxelShapes::empty);
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)world.getTileEntity(pos);
        return ItemPaintingFrame.getFrameAsItem(tilePF);
    }
    
    public static List<AxisAlignedBB> getFrameBoxes(final double frameWidth) {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (int i = 0; i < 12; ++i) {
            int stage = i / 4;
            int i2d = i % 4;
            int i2dx = i2d / 2;
            int i2dy = i2d % 2;
            double minX2d = (i2dx == 1) ? 0.0D : (1.0D - frameWidth);
            double maxX2d = (i2dx == 1) ? frameWidth : 1.0D;
            double minY2d = (i2dy == 1) ? 0.0D : (1.0D - frameWidth);
            double maxY2d = (i2dy == 1) ? frameWidth : 1.0D;
            double minX = 0.0D, maxX = 1.0D, minY = 0.0D, maxY = 1.0D, minZ = 0.0D, maxZ = 1.0D;
            switch (stage) {
                case 0: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minY = minY2d;
                    maxY = maxY2d;
                    minZ = frameWidth;
                    maxZ = 1.0D - frameWidth;
                }
                break;
                case 1: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
                case 2: {
                    minX = frameWidth;
                    maxX = 1.0D - frameWidth;
                    minY = minX2d;
                    maxY = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
            }
            AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
            list.add(box);
        }
        return list;
    }
}
