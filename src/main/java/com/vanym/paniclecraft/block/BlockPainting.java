package com.vanym.paniclecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPainting extends BlockPaintingContainer {
    
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    
    public BlockPainting() {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setRegistryName("painting");
        this.setHardness(0.4F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPainting();
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
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isSideSolid(
            IBlockState state,
            IBlockAccess world,
            BlockPos pos,
            EnumFacing side) {
        return side == state.getValue(FACING).getOpposite();
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(
            IBlockAccess world,
            IBlockState state,
            BlockPos pos,
            EnumFacing face) {
        return this.isSideSolid(state, world, pos, face) ? BlockFaceShape.SOLID
                                                         : BlockFaceShape.UNDEFINED;
    }
    
    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(
            IBlockState state,
            IBlockAccess world,
            BlockPos pos) {
        return this.getBlockBoundsBasedOnState(this.getMetaFromState(state));
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Core.instance.painting.itemPainting;
    }
    
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Core.instance.painting.itemPainting);
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityPlayer entityPlayer,
            EnumHand hand,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ) {
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
        return this.removedByPlayer(state, world, pos, entityPlayer, false);
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public boolean removedByPlayer(
            IBlockState state,
            World world,
            BlockPos pos,
            EntityPlayer player,
            boolean willHarvest) {
        if (player != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityPainting) {
                TileEntityPainting tileP = (TileEntityPainting)tile;
                Picture picture = tileP.getPicture();
                int meta = tileP.getBlockMetadata();
                EnumFacing dir = EnumFacing.getFront(meta);
                rotatePicture(player, picture, dir, false);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPainting) {
            TileEntityPainting tileP = (TileEntityPainting)tile;
            Picture picture = tileP.getPicture();
            ItemStack itemStack = ItemPainting.getPictureAsItem(picture);
            spawnAsEntity(world, pos, itemStack);
        }
        super.breakBlock(world, pos, state);
    }
    
    public AxisAlignedBB getBlockBoundsBasedOnState(int meta) {
        int side = EnumFacing.getFront(meta).getOpposite().ordinal();
        return GeometryUtils.getBoundsBySide(side, this.getPaintingOutlineSize());
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(
            IBlockState state,
            RayTraceResult target,
            World world,
            BlockPos pos,
            EntityPlayer player) {
        TileEntityPainting tile = (TileEntityPainting)world.getTileEntity(pos);
        return ItemPainting.getPictureAsItem(tile.getPicture(tile.getBlockMetadata()));
    }
}
