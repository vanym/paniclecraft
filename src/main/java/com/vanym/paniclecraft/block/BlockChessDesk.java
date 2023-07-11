package com.vanym.paniclecraft.block;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.client.gui.GuiChess;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.WorldUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockChessDesk extends HorizontalBlock {
    
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    
    protected static final VoxelShape CHESS_DESK_SHAPE =
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 3.0D / 16.0D, 1.0D);
    
    public BlockChessDesk() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(0.5F)
                              .noDrops());
        this.setRegistryName("chess_desk");
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
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
    public boolean onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit) {
        if (EffectiveSide.get().isClient()) {
            TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getTileEntity(pos);
            Minecraft.getInstance().displayGuiScreen(new GuiChess(tileCD));
        }
        return true;
    }
    
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getTileEntity(pos);
        spawnAsEntity(world, pos, ItemChessDesk.getSavedDesk(tileCD));
        super.onBlockHarvested(world, pos, state, player);
    }
    
    @Override
    public void onBlockPlacedBy(
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
        TileEntityChessDesk tile = (TileEntityChessDesk)world.getTileEntity(pos);
        return ItemChessDesk.getSavedDesk(tile);
    }
}
