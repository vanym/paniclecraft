package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPainting extends BlockPaintingContainer {
    
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    
    public BlockPainting() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(0.4F)
                              .noDrops());
        this.setRegistryName("painting");
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }
    
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityPainting();
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
        builder.add(FACING);
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
    public VoxelShape getShape(
            BlockState state,
            IBlockReader world,
            BlockPos pos,
            ISelectionContext context) {
        return VoxelShapes.create(this.getBlockBoundsBasedOnState(state.get(FACING).getIndex()));
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
            PlayerEntity entityPlayer,
            Hand hand,
            BlockRayTraceResult hit) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileEntityPainting)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        return this.removedByPlayer(state, world, pos, entityPlayer, false,
                                    world.getFluidState(pos));
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
            if (tile != null && tile instanceof TileEntityPainting) {
                TileEntityPainting tileP = (TileEntityPainting)tile;
                Picture picture = tileP.getPicture();
                Direction dir = tileP.getBlockState().get(FACING);
                rotatePicture(player, picture, dir, false);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
    
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPainting) {
            TileEntityPainting tileP = (TileEntityPainting)tile;
            Picture picture = tileP.getPicture();
            ItemStack itemStack = ItemPainting.getPictureAsItem(picture);
            spawnAsEntity(world, pos, itemStack);
        }
        super.onBlockHarvested(world, pos, state, player);
    }
    
    public AxisAlignedBB getBlockBoundsBasedOnState(int meta) {
        int side = Direction.byIndex(meta).getOpposite().ordinal();
        return GeometryUtils.getBoundsBySide(side, this.getPaintingOutlineSize());
    }
    
    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player) {
        TileEntityPainting tile = (TileEntityPainting)world.getTileEntity(pos);
        return ItemPainting.getPictureAsItem(tile.getPicture(tile.getBlockState()
                                                                 .get(FACING)
                                                                 .getIndex()));
    }
}
