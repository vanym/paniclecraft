package com.vanym.paniclecraft.block;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.client.gui.GuiChess;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.SideUtils;
import com.vanym.paniclecraft.utils.WorldUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockChessDesk extends HorizontalBlock implements IWaterLoggable {
    
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    protected static final VoxelShape CHESS_DESK_SHAPE =
            VoxelShapes.box(0.0D, 0.0D, 0.0D, 1.0D, 3.0D / 16.0D, 1.0D);
    
    public BlockChessDesk() {
        super(Block.Properties.of(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .strength(0.5F)
                              .noDrops());
        this.setRegistryName("chess_desk");
        this.registerDefaultState(this.stateDefinition.any()
                                                      .setValue(FACING, Direction.NORTH)
                                                      .setValue(WATERLOGGED, false));
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityChessDesk();
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false)
                                           : super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                   .setValue(FACING, context.getHorizontalDirection())
                   .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState facingState,
            IWorld world,
            BlockPos currentPos,
            BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks()
                 .scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos,
                                 facingPos);
    }
    
    @Override
    public VoxelShape getShape(
            BlockState state,
            IBlockReader worldIn,
            BlockPos pos,
            ISelectionContext context) {
        return CHESS_DESK_SHAPE;
    }
    
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public ActionResultType use(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        SideUtils.crun(()->new Runnable() {
            @Override
            public void run() {
                TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getBlockEntity(pos);
                Minecraft.getInstance().setScreen(new GuiChess(tileCD));
            }
        });
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public void playerWillDestroy(
            World world,
            BlockPos pos,
            BlockState state,
            PlayerEntity player) {
        TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getBlockEntity(pos);
        popResource(world, pos, ItemChessDesk.getSavedDesk(tileCD));
        super.playerWillDestroy(world, pos, state, player);
    }
    
    @Override
    public void setPlacedBy(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack) {
        ItemChessDesk.getMoves(stack).ifPresent(list-> {
            WorldUtils.getTileEntity(world, pos, TileEntityChessDesk.class)
                      .ifPresent(tileCD->tileCD.readMoves(list));
        });
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntityChessDesk tile = (TileEntityChessDesk)world.getBlockEntity(pos);
        return ItemChessDesk.getSavedDesk(tile);
    }
}
