package com.vanym.paniclecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignSide;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.WorldUtils;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
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

public class BlockAdvSign extends BlockContainerMod3 implements IWithCustomStateMapper {
    
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyEnum<AdvSignForm> FORM =
            PropertyEnum.create("form", AdvSignForm.class);
    
    public BlockAdvSign() {
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState()
                                            .withProperty(FACING, EnumFacing.UP)
                                            .withProperty(FORM, AdvSignForm.WALL));
        this.setRegistryName("advanced_sign");
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
        return new BlockStateContainer(this, FACING, FORM);
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WorldUtils.getTileEntity(world, pos, TileEntityAdvSign.class)
                         .map(sign->state.withProperty(FORM, sign.getForm()))
                         .orElse(state);
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
        AdvSignSide pside = AdvSignSide.getSide(tileAS.getBlockMetadata());
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
    public BlockFaceShape getBlockFaceShape(
            IBlockAccess world,
            IBlockState state,
            BlockPos pos,
            EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
    public IStateMapper getStateMapper() {
        return new StateMap.Builder().ignore(FACING, FORM).build();
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
}
