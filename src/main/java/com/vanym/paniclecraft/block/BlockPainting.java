package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
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
                        picture.getImage().rotate90();
                    break;
                    case 2:
                        picture.getImage().rotate180();
                    break;
                    case 3:
                        picture.getImage().rotate270();
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
                        picture.getImage().rotate270();
                    break;
                    case 2:
                        picture.getImage().rotate180();
                    break;
                    case 3:
                        picture.getImage().rotate90();
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
        return MainUtils.getBoundsBySide(side, this.getPaintingWidth());
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
        var1.setTag("PaintingData", var2);
        return itemS;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(World par1World, int par2, int par3, int par4) {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(
            World par1World,
            int par2,
            int par3,
            int par4) {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        if (Core.instance.painting.specialBoundingBox) {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack is = mc.thePlayer.inventory.getCurrentItem();
            if (is != null) {
                if (is.getItem() instanceof ItemPaintBrush
                    && (is.getItemDamage() == 0 || is.getItemDamage() == 1)) {
                    if (mc.objectMouseOver != null) {
                        if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                            TileEntity tile = par1World.getTileEntity(par2, par3, par4);
                            if (tile instanceof TileEntityPainting
                                && mc.objectMouseOver.sideHit == tile.getBlockMetadata()) {
                                Vec3 vec = mc.objectMouseOver.hitVec;
                                float f = (float)vec.xCoord - (float)par2;
                                float f1 = (float)vec.yCoord - (float)par3;
                                float f2 = (float)vec.zCoord - (float)par4;
                                int side = mc.objectMouseOver.sideHit;
                                TileEntityPainting tileP = (TileEntityPainting)tile;
                                Picture picture = tileP.getPainting(side);
                                int width = picture.getImage().getWidth();
                                int height = picture.getImage().getHeight();
                                int px = ItemPaintBrush.getXuse(width, mc.objectMouseOver.sideHit,
                                                                f, f1, f2);
                                int py = ItemPaintBrush.getYuse(height, mc.objectMouseOver.sideHit,
                                                                f, f1, f2);
                                double mxdx = (1.0D / width) * px;
                                double mxdy = (1.0D / height) * py;
                                double mndx = (1.0D / width) * (width - px) - (1.0D / width);
                                double mndy = (1.0D / height) * (height - py) -
                                              (1.0D / height);
                                switch (tileP.getBlockMetadata()) {
                                    case 0:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX + mndx,
                                                                            (double)par3 + this.minY,
                                                                            (double)par4 + this.minZ + mxdy,
                                                                            (double)par2 + this.maxX - mxdx,
                                                                            (double)par3 + this.maxY,
                                                                            (double)par4 + this.maxZ - mndy);
                                    case 1:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX + mndx,
                                                                            (double)par3 + this.minY,
                                                                            (double)par4 + this.minZ + mndy,
                                                                            (double)par2 + this.maxX - mxdx,
                                                                            (double)par3 + this.maxY,
                                                                            (double)par4 + this.maxZ - mxdy);
                                    case 2:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX + mndx,
                                                                            (double)par3 + this.minY +
                                                                                              mndy,
                                                                            (double)par4 + this.minZ,
                                                                            (double)par2 + this.maxX - mxdx,
                                                                            (double)par3 + this.maxY - mxdy,
                                                                            (double)par4 + this.maxZ);
                                    case 3:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX + mxdx,
                                                                            (double)par3 + this.minY +
                                                                                              mndy,
                                                                            (double)par4 + this.minZ,
                                                                            (double)par2 + this.maxX - mndx,
                                                                            (double)par3 + this.maxY - mxdy,
                                                                            (double)par4 + this.maxZ);
                                    case 4:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX,
                                                                            (double)par3 +
                                                                                       this.minY +
                                                                                       mndy,
                                                                            (double)par4 + this.minZ +
                                                                                             mxdx,
                                                                            (double)par2 + this.maxX,
                                                                            (double)par3 + this.maxY - mxdy,
                                                                            (double)par4 + this.maxZ - mndx);
                                    case 5:
                                        return AxisAlignedBB.getBoundingBox((double)par2 +
                                                                            this.minX,
                                                                            (double)par3 +
                                                                                       this.minY +
                                                                                       mndy,
                                                                            (double)par4 + this.minZ +
                                                                                             mndx,
                                                                            (double)par2 + this.maxX,
                                                                            (double)par3 + this.maxY - mxdy,
                                                                            (double)par4 + this.maxZ - mxdx);
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityPainting tile = (TileEntityPainting)world.getTileEntity(x, y, z);
        return getSavedPic(tile.getPainting(tile.getBlockMetadata()));
    }
}
