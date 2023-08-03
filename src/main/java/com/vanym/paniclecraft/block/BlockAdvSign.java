package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignSide;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockAdvSign extends DirectionalBlock {
    
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<AdvSignForm> FORM =
            EnumProperty.create("form", AdvSignForm.class);
    public static final IntegerProperty ROTATION = StandingSignBlock.ROTATION;
    
    public BlockAdvSign() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(1.0F)
                              .doesNotBlockMovement()
                              .noDrops());
        this.setRegistryName("advanced_sign");
        this.setDefaultState(this.stateContainer.getBaseState()
                                                .with(FACING, Direction.UP)
                                                .with(FORM, AdvSignForm.WALL)
                                                .with(ROTATION, 0));
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
        return this.getDefaultState().with(FACING, context.getFace());
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
        builder.add(FACING, FORM, ROTATION);
    }
    
    @Override
    public VoxelShape getShape(
            BlockState state,
            IBlockReader world,
            BlockPos pos,
            ISelectionContext context) {
        TileEntity tile = world.getTileEntity(pos);
        if (!TileEntityAdvSign.class.isInstance(tile)) {
            return VoxelShapes.fullCube();
        }
        TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
        AdvSignSide pside = AdvSignSide.getSide(state.get(FACING).getIndex());
        AxisAlignedBB box;
        if (tileAS.getForm() == AdvSignForm.WALL) {
            double direction = MathHelper.wrapDegrees(tileAS.getDirection());
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
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public boolean canSpawnInBlock() {
        return true;
    }
    
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getTileEntity(pos);
        spawnAsEntity(world, pos, ItemAdvSign.getSavedSign(tileAS));
        super.onBlockHarvested(world, pos, state, player);
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        return ItemAdvSign.getSavedSign(tile instanceof TileEntityAdvSign ? (TileEntityAdvSign)tile
                                                                          : null);
    }
}
