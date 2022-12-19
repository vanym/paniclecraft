package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemPainting extends ItemMod3 {
    
    public static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    public static int paintingPlaceStack = 2;
    
    public ItemPainting() {
        super();
        this.setUnlocalizedName("painting");
    }
    
    @Override
    public boolean onItemUse(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        int i = 0;
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!entityPlayer.isSneaking()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                return this.onItemUseOnFrame(itemStack, entityPlayer, world, tilePF, side);
            }
            for (; i < paintingPlaceStack; i++) {
                Block block = world.getBlock(x, y, z);
                if (block != Core.instance.painting.blockPainting) {
                    break;
                }
                int meta = world.getBlockMetadata(x, y, z);
                if (meta != side) {
                    break;
                }
                ForgeDirection stackdir =
                        BlockPaintingContainer.getStackDirection(entityPlayer, dir);
                if (stackdir == ForgeDirection.UNKNOWN) {
                    break;
                }
                x += stackdir.offsetX;
                y += stackdir.offsetY;
                z += stackdir.offsetZ;
            }
        }
        if (i == 0) {
            x += dir.offsetX;
            y += dir.offsetY;
            z += dir.offsetZ;
        }
        if (!entityPlayer.canPlayerEdit(x, y, z, side, itemStack)
            || !Core.instance.painting.blockPainting.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }
        world.setBlock(x, y, z, Core.instance.painting.blockPainting, side, 3);
        --itemStack.stackSize;
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(x, y, z);
        Picture picture = tileP.getPainting(side);
        this.fillPicture(picture, itemStack);
        if (entityPlayer != null) {
            BlockPaintingContainer.rotatePicture(entityPlayer, picture, dir, true);
        }
        return true;
    }
    
    public boolean onItemUseOnFrame(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPainting(side) != null) {
            return false;
        }
        Picture picture = tilePF.createPicture(side);
        --itemStack.stackSize;
        this.fillPicture(picture, itemStack);
        if (entityPlayer != null) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            BlockPaintingContainer.rotatePicture(entityPlayer, picture, dir, true);
        }
        tilePF.markForUpdate();
        world.notifyBlockChange(tilePF.xCoord, tilePF.yCoord, tilePF.zCoord, tilePF.getBlockType());
        return true;
    }
    
    public boolean fillPicture(Picture picture, ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_PICTURE)) {
                NBTTagCompound pictureTag = itemTag.getCompoundTag(TAG_PICTURE);
                if (!pictureTag.hasNoTags()) {
                    picture.readFromNBT(pictureTag);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            NBTBase pictureTagBase = itemTag.getTag(TAG_PICTURE);
            if (pictureTagBase != null && pictureTagBase instanceof NBTTagCompound) {
                NBTTagCompound pictureTag = (NBTTagCompound)pictureTagBase;
                NBTBase imageTagBase = pictureTag.getTag(Picture.TAG_IMAGE);
                if (imageTagBase != null && imageTagBase instanceof NBTTagCompound) {
                    NBTTagCompound imageTag = (NBTTagCompound)imageTagBase;
                    StringBuilder sb = new StringBuilder();
                    sb.append(imageTag.getInteger(Picture.TAG_IMAGE_WIDTH));
                    sb.append("×");
                    sb.append(imageTag.getInteger(Picture.TAG_IMAGE_HEIGHT));
                    list.add(sb.toString());
                }
            }
        }
    }
}
