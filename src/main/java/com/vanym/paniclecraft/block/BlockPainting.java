package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPainting extends BlockPaintingContainer {
    
    public BlockPainting() {
        super(Material.wood);
        this.setBlockName("painting");
        this.setHardness(0.4F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int md) {
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
            case PAINTING:
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(
            World par1World,
            int par2,
            int par3,
            int par4) {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
        // return null;
    }
    
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Core.instance.painting.itemPainting;
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer entityPlayer,
            int p_149727_6_,
            float p_149727_7_,
            float p_149727_8_,
            float p_149727_9_) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        if (!world.isRemote) {
            TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            Picture picture = tileP.getPainting(md);
            if (md == 0) {
                int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
                while (rot >= 4) {
                    rot -= 4;
                }
                switch (rot) {
                    case 1:
                        picture.rotate90();
                    break;
                    case 2:
                        picture.rotate180();
                    break;
                    case 3:
                        picture.rotate270();
                    break;
                }
            }
            if (md == 1) {
                int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
                while (rot >= 4) {
                    rot -= 4;
                }
                switch (rot) {
                    case 1:
                        picture.rotate270();
                    break;
                    case 2:
                        picture.rotate180();
                    break;
                    case 3:
                        picture.rotate90();
                    break;
                }
            }
            EntityItem entityItem = new EntityItem(
                    world,
                    x + 0.5D,
                    y + 0.5D,
                    z + 0.5D,
                    getSavedPic(picture));
            world.spawnEntityInWorld(entityItem);
            world.setBlockToAir(x, y, z);
        }
        return true;
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
        return MainUtils.getBoundsBySide(side, this.getPaintingOutlineSize());
    }
    
    public static ItemStack getSavedPic(Picture picture) {
        ItemStack itemS = new ItemStack(Core.instance.painting.itemPainting);
        if (picture == null) {
            return itemS;
        }
        NBTTagCompound var1 = new NBTTagCompound();
        NBTTagCompound var2 = new NBTTagCompound();
        picture.writeToNBT(var2);
        itemS.setTagCompound(var1);
        var1.setTag(ItemPainting.TAG_PICTURE, var2);
        return itemS;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(World par1World, int par2, int par3, int par4) {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityPainting tile = (TileEntityPainting)world.getTileEntity(x, y, z);
        return getSavedPic(tile.getPainting(tile.getBlockMetadata()));
    }
}
