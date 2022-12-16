package com.vanym.paniclecraft.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPaintingFrame extends BlockPaintingContainer {
    
    @SideOnly(Side.CLIENT)
    protected int specialRendererSide = -1;
    
    protected final double frameWidth;
    
    protected final List<AxisAlignedBB> frameBoxes;
    
    public BlockPaintingFrame() {
        super(Material.wood);
        this.setBlockName("paintingFrame");
        this.setHardness(0.6F);
        this.frameWidth = (1.0D / 16D) * 2.0D;
        this.frameBoxes = Collections.unmodifiableList(getFrameBoxes(this.frameWidth));
    }
    
    @SideOnly(Side.CLIENT)
    public void setRendererSide(int sRS) {
        this.specialRendererSide = sRS;
    }
    
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityPaintingFrame();
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer entityPlayer,
            int side,
            float p_149727_7_,
            float p_149727_8_,
            float p_149727_9_) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        if (tileP != null) {
            if (tileP.getPainting(side) == null) {
                return false;
            }
            if (world.isRemote) {
                return true;
            }
            Picture picture = tileP.getPainting(side);
            if (side == 0) {
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
            if (side == 1) {
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
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            EntityItem entityItem = new EntityItem(
                    world,
                    x + 0.5D + (dir.offsetX * 0.6),
                    y + 0.5D + (dir.offsetY * 0.6),
                    z + 0.5D + (dir.offsetZ * 0.6),
                    BlockPainting.getSavedPic(picture));
            world.spawnEntityInWorld(entityItem);
            tileP.clearPicture(side);
            tileP.markForUpdate();
        }
        return false;
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
            TileEntity tile =
                    world.getTileEntity(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
            if (tile != null) {
                boolean flag = this.shouldSideBeRendered(side, tile.getBlockMetadata(), tile);
                if (!flag) {
                    return false;
                }
            }
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(int side, int meta, TileEntity tile) {
        TileEntityPaintingFrame tileFrame = (TileEntityPaintingFrame)tile;
        boolean contains = (tileFrame.getPainting(side) != null);
        if (this.specialRendererPhase == SpecialRendererPhase.PAINTING) {
            return contains && side == this.specialRendererSide;
        } else if (this.specialRendererPhase == SpecialRendererPhase.FRAME && contains) {
            return !MainUtils.isTouchingSide(side, this.specialRendererBox);
        } else if (this.specialRendererPhase == SpecialRendererPhase.FRAMEINSIDE) {
            return ForgeDirection.OPPOSITES[side] == this.specialRendererSide;
        }
        return true;
    }
    
    public List<AxisAlignedBB> getFrameBoxes() {
        return this.frameBoxes;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntityPaintingFrame tile = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        if (tile.getPainting(side.ordinal()) != null) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
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
                            if (tile instanceof TileEntityPaintingFrame
                                && ((TileEntityPaintingFrame)tile).getPainting(mc.objectMouseOver.sideHit) != null) {
                                Vec3 vec = mc.objectMouseOver.hitVec;
                                float f = (float)vec.xCoord - (float)par2;
                                float f1 = (float)vec.yCoord - (float)par3;
                                float f2 = (float)vec.zCoord - (float)par4;
                                int side = mc.objectMouseOver.sideHit;
                                TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)tile;
                                Picture picture = tileP.getPainting(side);
                                int width = picture.getWidth();
                                int height = picture.getHeight();
                                int px = ItemPaintBrush.getXuse(width, side, f, f1, f2);
                                int py = ItemPaintBrush.getYuse(height, side, f, f1, f2);
                                double mxdx = (1.0D / width) * px;
                                double mxdy = (1.0D / height) * py;
                                double mndx = (1.0D / width) *
                                              (width - px) -
                                              (1.0D / width);
                                double mndy = (1.0D / height) *
                                              (height - py) -
                                              (1.0D / height);
                                switch (side) {
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addCollisionBoxesToList(
            World world,
            int x,
            int y,
            int z,
            AxisAlignedBB mask,
            List list,
            Entity entity) {
        TileEntityPaintingFrame tile = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        Builder<AxisAlignedBB> facades = Stream.builder();
        for (int i = 0; i < 6; ++i) {
            if (tile.getPainting(i) != null) {
                AxisAlignedBB box = MainUtils.getBoundsBySide(i, this.getPaintingWidth());
                facades.add(box);
            }
        }
        List<AxisAlignedBB> boxes = this.getFrameBoxes();
        Stream.concat(boxes.stream(), facades.build()).forEach(box-> {
            AxisAlignedBB absoluteBox = MainUtils.absolutizeBox(x, y, z, box);
            if (mask.intersectsWith(absoluteBox)) {
                list.add(absoluteBox);
            }
        });
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        TileEntityPaintingFrame tile = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        for (int side = 0; side < 6; side++) {
            Picture picture = tile.getPainting(side);
            if (picture != null) {
                this.dropBlockAsItem(world, x, y, z,
                                     new ItemStack(Core.instance.painting.itemPainting));
            }
        }
        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int md) {
        return Core.instance.painting.blockPainting.getIcon(par1, md);
    }
    
    public static List<AxisAlignedBB> getFrameBoxes(final double frameWidth) {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (int i = 0; i < 12; ++i) {
            int stage = i / 4;
            int i2d = i % 4;
            int i2dx = i2d / 2;
            int i2dy = i2d % 2;
            double minX2d = (i2dx == 1) ? 0.0D : (1.0D - frameWidth);
            double maxX2d = (i2dx == 1) ? frameWidth : 1.0D;
            double minY2d = (i2dy == 1) ? 0.0D : (1.0D - frameWidth);
            double maxY2d = (i2dy == 1) ? frameWidth : 1.0D;
            double minX = 0.0D, maxX = 1.0D, minY = 0.0D, maxY = 1.0D, minZ = 0.0D, maxZ = 1.0D;
            switch (stage) {
                case 0: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minY = minY2d;
                    maxY = maxY2d;
                    minZ = frameWidth;
                    maxZ = 1.0D - frameWidth;
                }
                break;
                case 1: {
                    minX = minX2d;
                    maxX = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
                case 2: {
                    minX = frameWidth;
                    maxX = 1.0D - frameWidth;
                    minY = minX2d;
                    maxY = maxX2d;
                    minZ = minY2d;
                    maxZ = maxY2d;
                }
                break;
            }
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            list.add(box);
        }
        return list;
    }
}
