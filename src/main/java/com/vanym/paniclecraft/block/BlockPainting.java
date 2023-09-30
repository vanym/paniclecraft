package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPainting extends BlockPaintingContainer implements IWaterLoggable {
    
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    public BlockPainting() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(0.4F)
                              .noDrops());
        this.setRegistryName("painting");
        this.setDefaultState(this.stateContainer.getBaseState()
                                                .with(FACING, Direction.NORTH)
                                                .with(WATERLOGGED, false));
    }
    
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityPainting();
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState()
                   .with(FACING, context.getFace())
                   .with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.toRotation(state.get(FACING)));
    }
    
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false)
                                      : super.getFluidState(state);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(
            BlockState state,
            Direction facing,
            BlockState facingState,
            IWorld world,
            BlockPos currentPos,
            BlockPos facingPos) {
        if (state.get(WATERLOGGED)) {
            world.getPendingFluidTicks()
                 .scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.updatePostPlacement(state, facing, facingState, world, currentPos,
                                         facingPos);
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
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public VoxelShape getShape(
            BlockState state,
            IBlockReader world,
            BlockPos pos,
            ISelectionContext context) {
        return VoxelShapes.create(this.getBlockBoundsBasedOnState(state.get(FACING).getIndex()));
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean isReplaceable(BlockState state, BlockItemUseContext context) {
        if (context.isPlacerSneaking()) {
            return super.isReplaceable(state, context);
        }
        return context.getItem().getItem() == this.asItem()
            && state.get(FACING) == context.getFace();
    }
    
    @Override
    public Item asItem() {
        return Core.instance.painting.itemPainting;
    }
    
    @Override
    public boolean onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        if (super.onBlockActivated(state, world, pos, player, hand, hit)) {
            return true;
        }
        if (!player.isSneaking()) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileEntityPainting)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        return this.removedByPlayer(state, world, pos, player, false, world.getFluidState(pos));
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
            if (tile instanceof TileEntityPainting) {
                TileEntityPainting tileP = (TileEntityPainting)tile;
                Picture picture = tileP.getPicture();
                Direction dir = tileP.getBlockState().get(FACING);
                SideUtils.runSync(!world.isRemote, tileP,
                                  ()->rotatePicture(player, picture, dir, false));
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
    
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPainting) {
            TileEntityPainting tileP = (TileEntityPainting)tile;
            Picture picture = tileP.getPicture();
            ItemStack stack = SideUtils.callSync(!world.isRemote, tileP,
                                                 ()->ItemPainting.getPictureAsItem(picture));
            spawnAsEntity(world, pos, stack);
        }
        super.onBlockHarvested(world, pos, state, player);
    }
    
    public AxisAlignedBB getBlockBoundsBasedOnState(int meta) {
        int side = Direction.byIndex(meta).getOpposite().getIndex();
        return GeometryUtils.getBoundsBySide(side, this.getPaintingOutlineSize());
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(pos);
        return SideUtils.callSync(tileP.hasWorld() && !tileP.getWorld().isRemote(), tileP,
                                  ()->ItemPainting.getPictureAsItem(tileP.getPicture()));
    }
}
