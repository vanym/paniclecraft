package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockAdvSign extends DirectionalBlock {
    
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    
    public BlockAdvSign() {
        super(Block.Properties.create(Material.WOOD)
                              .sound(SoundType.WOOD)
                              .hardnessAndResistance(1.0F)
                              .doesNotBlockMovement()
                              .noDrops());
        this.setRegistryName("advanced_sign");
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
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
        builder.add(FACING);
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
        SignSide pside = SignSide.getSide(state.get(FACING).getIndex());
        AxisAlignedBB box;
        if (tileAS.onStick()) {
            box = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
        } else {
            double direction = MathHelper.wrapDegrees(tileAS.getDirection());
            if (pside == SignSide.DOWN) {
                direction *= -1.0D;
            }
            double radians = Math.toRadians(direction);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            
            double xl = -0.5D, xr = 0.5D;
            double yt = -0.28125D, yb = 0.5D - 0.28125D;
            
            double nxlt = xl * cos - yt * sin;
            double nylt = yt * cos + xl * sin;
            
            double nxrt = xr * cos - yt * sin;
            double nyrt = yt * cos + xr * sin;
            
            double nxlb = xl * cos - yb * sin;
            double nylb = yb * cos + xl * sin;
            
            double nxrb = xr * cos - yb * sin;
            double nyrb = yb * cos + xr * sin;
            
            double mul2 = ((180.0D + direction) % 90.0D) / 90.0D;
            double mul1 = 1.0D - mul2;
            
            double nx1 = nxlt * mul1 + nxlb * mul2;
            double ny1 = nylt * mul1 + nylb * mul2;
            
            double nx2 = nxrb * mul1 + nxrt * mul2;
            double ny2 = nyrb * mul1 + nyrt * mul2;
            
            box = new AxisAlignedBB(
                    0.5D + nx1,
                    0.5D + ny1,
                    0.0D,
                    0.5D + nx2,
                    0.5D + ny2,
                    0.125D);
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
    
    protected static enum SignSide {
        DOWN(Direction.WEST), // -Y
        UP(Direction.EAST), // +Y
        NORTH(Direction.WEST), // -Z
        SOUTH(Direction.EAST), // +Z
        WEST(Direction.SOUTH), // -X
        EAST(Direction.NORTH); // +X
        
        public final TileOnSide axes;
        
        SignSide(Direction xDir) {
            this.axes = new TileOnSide(xDir, Direction.byIndex(this.ordinal()));
        }
        
        public static SignSide getSide(int side) {
            return values()[Math.abs(side) % values().length];
        }
    }
}
