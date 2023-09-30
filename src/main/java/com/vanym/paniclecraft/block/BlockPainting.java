package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPainting extends BlockPaintingContainer {
    
    public BlockPainting() {
        super(Material.wood);
        this.setRegistryName("painting");
        this.setHardness(0.4F);
    }
    
    @Override
    public Class<? extends ItemBlock> getItemClass() {
        return null;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPainting();
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        if (!this.specialRendererPhase.isNone()) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            int meta = world.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
            boolean flag = this.shouldSideBeRendered(side, meta, null);
            if (!flag) {
                return false;
            }
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(int side, int meta, TileEntity tile) {
        boolean contains = (side == meta);
        switch (this.specialRendererPhase) {
            default:
            case FRAME:
                return !contains;
            case PICTURE:
                return contains;
        }
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)) == side.getOpposite();
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Core.instance.painting.itemPainting;
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
            return true;
        }
        if (!player.isSneaking()) {
            return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof TileEntityPainting)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        return this.removedByPlayer(world, player, x, y, z, false);
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public boolean removedByPlayer(
            World world,
            EntityPlayer player,
            int x,
            int y,
            int z,
            boolean willHarvest) {
        if (player != null) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityPainting) {
                TileEntityPainting tileP = (TileEntityPainting)tile;
                Picture picture = tileP.getPicture();
                int meta = tileP.getBlockMetadata();
                ForgeDirection dir = ForgeDirection.getOrientation(meta);
                SideUtils.runSync(!world.isRemote, tileP,
                                  ()->rotatePicture(player, picture, dir, false));
            }
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPainting) {
            TileEntityPainting tileP = (TileEntityPainting)tile;
            Picture picture = tileP.getPicture();
            ItemStack stack = SideUtils.callSync(!world.isRemote, tileP,
                                                 ()->ItemPainting.getPictureAsItem(picture));
            this.dropBlockAsItem(world, x, y, z, stack);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        AxisAlignedBB box = this.getBlockBoundsBasedOnState(meta);
        this.setBlockBounds((float)box.minX, (float)box.minY, (float)box.minZ,
                            (float)box.maxX, (float)box.maxY, (float)box.maxZ);
    }
    
    public AxisAlignedBB getBlockBoundsBasedOnState(int meta) {
        int side = ForgeDirection.getOrientation(meta).getOpposite().ordinal();
        return GeometryUtils.getBoundsBySide(side, this.getPaintingOutlineSize());
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(x, y, z);
        return ItemPainting.getPictureAsItem(tileP.getPicture());
    }
}
