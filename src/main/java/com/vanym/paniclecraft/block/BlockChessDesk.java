package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
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

public class BlockChessDesk extends BlockContainerMod3 {
    
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    
    protected static final AxisAlignedBB CHESS_DESK_AABB =
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 3.0D / 16.0D, 1.0D);
    
    public BlockChessDesk() {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setUnlocalizedName("chess_desk");
        this.setHardness(0.5F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChessDesk();
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    
    @Override
    public IBlockState getStateForPlacement(
            World world,
            BlockPos pos,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ,
            int meta,
            EntityLivingBase placer) {
        return this.getDefaultState()
                   .withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }
    
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHESS_DESK_AABB;
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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityPlayer player,
            EnumHand hand,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ) {
        player.openGui(Core.instance, GUIs.CHESS.ordinal(),
                       world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getTileEntity(pos);
        spawnAsEntity(world, pos, ItemChessDesk.getSavedDesk(tileCD));
        super.breakBlock(world, pos, state);
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityLivingBase placer,
            ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(ItemChessDesk.TAG_MOVES, 9)) {
            return;
        }
        NBTTagList list = tag.getTagList(ItemChessDesk.TAG_MOVES, 10);
        TileEntity tile = world.getTileEntity(pos);
        if (!TileEntityChessDesk.class.isInstance(tile)) {
            return;
        }
        TileEntityChessDesk tileCD = (TileEntityChessDesk)tile;
        tileCD.readMovesFromNBT(list);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(
            IBlockState state,
            RayTraceResult target,
            World world,
            BlockPos pos,
            EntityPlayer player) {
        TileEntityChessDesk tile = (TileEntityChessDesk)world.getTileEntity(pos);
        return ItemChessDesk.getSavedDesk(tile);
    }
}
