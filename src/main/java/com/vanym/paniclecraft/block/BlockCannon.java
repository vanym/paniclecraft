package com.vanym.paniclecraft.block;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCannon extends BlockContainerMod3 {
    
    public BlockCannon() {
        super(Material.ANVIL);
        this.setUnlocalizedName("cannon");
        this.setHardness(1.5F);
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
        if (!world.isRemote) {
            player.openGui(Core.instance, GUIs.CANNON.ordinal(),
                           world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCannon();
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
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
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
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }
    
    @Override
    public boolean isSideSolid(
            IBlockState state,
            IBlockAccess world,
            BlockPos pos,
            EnumFacing side) {
        return side == EnumFacing.DOWN;
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
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityLivingBase entity,
            ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityCannon) {
            TileEntityCannon tileCannon = (TileEntityCannon)tile;
            double direction = Math.round(180.0D + entity.rotationYaw);
            tileCannon.setDirection(direction);
            double height = Math.round(entity.rotationPitch);
            tileCannon.setHeight(Math.max(0.0D, Math.min(90.0D, height)));
        }
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
            world.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(world, pos, state);
    }
}
