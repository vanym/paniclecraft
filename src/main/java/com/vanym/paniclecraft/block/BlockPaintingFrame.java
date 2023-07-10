package com.vanym.paniclecraft.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.GeometryUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPaintingFrame extends BlockPaintingContainer {
    
    @SideOnly(Side.CLIENT)
    protected int specialRendererSide;
    
    protected final double frameOutlineSize;
    
    protected final List<AxisAlignedBB> frameBoxes;
    
    public BlockPaintingFrame() {
        super(Material.wood);
        this.setRegistryName("paintingframe");
        this.setHardness(0.6F);
        this.frameOutlineSize = (1.0D / 16D) * 2.0D;
        this.frameBoxes = Collections.unmodifiableList(getFrameBoxes(this.frameOutlineSize));
    }
    
    @Override
    public Class<? extends ItemBlock> getItemClass() {
        return ItemPaintingFrame.class;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient() {
        super.initClient();
        this.specialRendererSide = -1;
    }
    
    @SideOnly(Side.CLIENT)
    public void setRendererSide(int sRS) {
        this.specialRendererSide = sRS;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
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
            float hitX,
            float hitY,
            float hitZ) {
        if (!entityPlayer.isSneaking()) {
            return false;
        }
        TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        if (tileP == null) {
            return false;
        }
        Picture picture = tileP.getPicture(side);
        if (picture == null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (entityPlayer != null) {
            rotatePicture(entityPlayer, picture, dir, false);
        }
        double ex = x + 0.5D + (dir.offsetX * 0.6);
        double ey = y + 0.5D + (dir.offsetY * 0.6);
        double ez = z + 0.5D + (dir.offsetZ * 0.6);
        ItemStack itemStack = ItemPainting.getPictureAsItem(picture);
        EntityItem entityItem = new EntityItem(world, ex, ey, ez, itemStack);
        entityItem.delayBeforeCanPickup = 3;
        world.spawnEntityInWorld(entityItem);
        tileP.clearPicture(side);
        tileP.markForUpdate();
        world.notifyBlockChange(x, y, z, this);
        return true;
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
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                int rot = getRotate(player, ForgeDirection.UP, false);
                tilePF.rotateY(rot);
            }
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame timePF = (TileEntityPaintingFrame)tile;
            ItemStack itemStack = ItemPaintingFrame.getFrameAsItem(timePF);
            this.dropBlockAsItem(world, x, y, z, itemStack);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            int x,
            int y,
            int z,
            EntityLivingBase entity,
            ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        if (!stack.hasTagCompound()) {
            return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
            for (ForgeDirection pside : ForgeDirection.VALID_DIRECTIONS) {
                ItemPaintingFrame.getPictureTag(stack, pside).ifPresent(tag-> {
                    Picture picture = tilePF.createPicture(pside.ordinal());
                    picture.readFromNBT(tag);
                });
            }
            int rot = getRotate(entity, ForgeDirection.UP, true);
            tilePF.rotateY(rot);
        }
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
        boolean contains = (tileFrame.getPicture(side) != null);
        if (this.specialRendererPhase == SpecialRendererPhase.PICTURE) {
            return contains && side == this.specialRendererSide;
        } else if (this.specialRendererPhase == SpecialRendererPhase.FRAME && contains) {
            return !GeometryUtils.isTouchingSide(side, this.specialRendererBox);
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
        if (tile.getPicture(side.ordinal()) != null) {
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
            if (tile.getPicture(i) != null) {
                AxisAlignedBB box = GeometryUtils.getBoundsBySide(i, this.getPaintingOutlineSize());
                facades.add(box);
            }
        }
        List<AxisAlignedBB> boxes = this.getFrameBoxes();
        Stream.concat(boxes.stream(), facades.build()).forEach(box-> {
            AxisAlignedBB absoluteBox = box.getOffsetBoundingBox(x, y, z);
            if (mask.intersectsWith(absoluteBox)) {
                list.add(absoluteBox);
            }
        });
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Core.instance.painting.blockPainting.getIcon(side, meta);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
        return ItemPaintingFrame.getFrameAsItem(tilePF);
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
