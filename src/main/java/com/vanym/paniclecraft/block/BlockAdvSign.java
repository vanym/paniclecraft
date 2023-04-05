package com.vanym.paniclecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.TileOnSide;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAdvSign extends BlockContainerMod3 {
    
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    
    public BlockAdvSign() {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setUnlocalizedName("advSign");
        this.setHardness(1.0F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAdvSign();
    }
    
    @Override
    public IBlockState getStateForPlacement(
            World worldIn,
            BlockPos pos,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ,
            int meta,
            EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }
    
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Core.instance.advSign.itemAdvSign;
    }
    
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Core.instance.advSign.itemAdvSign);
    }
    
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(
            IBlockState state,
            IBlockAccess world,
            BlockPos pos) {
        return NULL_AABB;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (!TileEntityAdvSign.class.isInstance(tile)) {
            return FULL_BLOCK_AABB;
        }
        TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
        SignSide pside = SignSide.getSide(tileAS.getBlockMetadata());
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
        return box;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean canSpawnInBlock() {
        return true;
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getTileEntity(pos);
        spawnAsEntity(world, pos, ItemAdvSign.getSavedSign(tileAS));
        super.breakBlock(world, pos, state);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(
            IBlockState state,
            RayTraceResult target,
            World world,
            BlockPos pos,
            EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        return ItemAdvSign.getSavedSign(tile instanceof TileEntityAdvSign ? (TileEntityAdvSign)tile
                                                                          : null);
    }
    
    protected static enum SignSide {
        DOWN(EnumFacing.WEST), // -Y
        UP(EnumFacing.EAST), // +Y
        NORTH(EnumFacing.WEST), // -Z
        SOUTH(EnumFacing.EAST), // +Z
        WEST(EnumFacing.SOUTH), // -X
        EAST(EnumFacing.NORTH); // +X
        
        public final TileOnSide axes;
        
        SignSide(EnumFacing xDir) {
            this.axes = new TileOnSide(xDir, EnumFacing.getFront(this.ordinal()));
        }
        
        public static SignSide getSide(int side) {
            return values()[side % values().length];
        }
    }
}
