package com.vanym.paniclecraft.block;

import java.util.Optional;

import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignSide;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.WorldUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockAdvSign extends DirectionalBlock implements IWaterLoggable {
    
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<AdvSignForm> FORM =
            EnumProperty.create("form", AdvSignForm.class);
    public static final IntegerProperty ROTATION = StandingSignBlock.ROTATION;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    public BlockAdvSign() {
        super(Block.Properties.of(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .strength(1.0F)
                              .noCollission()
                              .noDrops());
        this.setRegistryName("advanced_sign");
        this.registerDefaultState(this.stateDefinition.any()
                                                      .setValue(FACING, Direction.UP)
                                                      .setValue(FORM, AdvSignForm.WALL)
                                                      .setValue(ROTATION, 0)
                                                      .setValue(WATERLOGGED, false));
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) {
        return new TileEntityAdvSign();
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                   .setValue(FACING, context.getClickedFace())
                   .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, FORM, ROTATION, WATERLOGGED);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false)
                                           : super.getFluidState(state);
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
            IBlockReader world,
            BlockPos pos,
            ISelectionContext context) {
        Optional<TileEntityAdvSign> oSign =
                WorldUtils.getTileEntity(world, pos, TileEntityAdvSign.class);
        AdvSignSide pside = AdvSignSide.getSide(state.getValue(FACING).get3DDataValue());
        AdvSignForm form = oSign.map(TileEntityAdvSign::getForm)
                                .orElseGet(()->state.getValue(FORM));
        AxisAlignedBB box;
        if (form == AdvSignForm.WALL) {
            double direction = oSign.map(TileEntityAdvSign::getDirection)
                                    .orElseGet(()->state.getValue(ROTATION) * 22.5D);
            direction = MathHelper.wrapDegrees(direction);
            direction *= pside.zAxis;
            box = new AxisAlignedBB(0.0D, 0.21875D, 0.0D, 1.0D, 0.71875D, 0.125D);
            box = GeometryUtils.rotateXYInnerEdge(box, Math.toRadians(direction));
        } else {
            box = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
        }
        box = pside.axes.fromSideCoords(box);
        return VoxelShapes.create(box);
    }
    
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }
    
    @Override
    public void playerWillDestroy(
            World world,
            BlockPos pos,
            BlockState state,
            PlayerEntity player) {
        TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getBlockEntity(pos);
        popResource(world, pos, ItemAdvSign.getSavedSign(tileAS));
        super.playerWillDestroy(world, pos, state, player);
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntity tile = world.getBlockEntity(pos);
        return ItemAdvSign.getSavedSign(tile instanceof TileEntityAdvSign ? (TileEntityAdvSign)tile
                                                                          : null);
    }
}
