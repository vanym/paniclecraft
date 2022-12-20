package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
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
        final BlockPainting painting = Core.instance.painting.blockPainting;
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
                if (block != painting) {
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
            || !painting.canPlaceBlockAt(world, x, y, z)
            || !world.setBlock(x, y, z, painting, side, 3)) {
            return false;
        }
        painting.onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
        painting.onPostBlockPlaced(world, x, y, z, side);
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
                              painting.stepSound.func_150496_b(),
                              (painting.stepSound.getVolume() + 1.0F) / 2.0F,
                              painting.stepSound.getPitch() * 0.8F);
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
