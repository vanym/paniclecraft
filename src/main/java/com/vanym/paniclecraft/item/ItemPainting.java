package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPainting extends ItemMod3 {
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    public ItemPainting() {
        this.setRegistryName("painting");
    }
    
    @Override
    public EnumActionResult onItemUse(
            EntityPlayer entityPlayer,
            World world,
            BlockPos pos,
            EnumHand hand,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ) {
        ItemStack itemStack = entityPlayer.getHeldItem(hand);
        final BlockPainting painting = Core.instance.painting.blockPainting;
        int i = 0;
        if (!entityPlayer.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                return this.onItemUseOnFrame(itemStack, entityPlayer, world, tilePF,
                                             side.getIndex());
            }
            for (; i < Core.instance.painting.config.paintingPlaceStack; i++) {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (block != painting) {
                    break;
                }
                int meta = block.getMetaFromState(state);
                if (meta != side.getIndex()) {
                    break;
                }
                EnumFacing stackdir = BlockPaintingContainer.getStackDirection(entityPlayer, side);
                if (stackdir == null) {
                    break;
                }
                pos = pos.offset(stackdir);
            }
        }
        if (i == 0) {
            pos = pos.offset(side);
        }
        if (!entityPlayer.canPlayerEdit(pos, side, itemStack)
            || !painting.canPlaceBlockAt(world, pos)) {
            return EnumActionResult.FAIL;
        }
        IBlockState state = painting.getStateForPlacement(world, pos, side,
                                                          hitX, hitY, hitZ,
                                                          0, entityPlayer, hand);
        if (!world.setBlockState(pos, state, 11)) {
            return EnumActionResult.FAIL;
        }
        painting.onBlockPlacedBy(world, pos, state, entityPlayer, itemStack);
        state = world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, world, pos, entityPlayer);
        world.playSound(entityPlayer, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(pos);
        Picture picture = tileP.getPicture(side.getIndex());
        fillPicture(picture, itemStack);
        itemStack.shrink(1);
        if (entityPlayer != null) {
            BlockPaintingContainer.rotatePicture(entityPlayer, picture, side, true);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public EnumActionResult onItemUseOnFrame(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPicture(side) != null) {
            return EnumActionResult.FAIL;
        }
        Picture picture = tilePF.createPicture(side);
        fillPicture(picture, itemStack);
        itemStack.shrink(1);
        if (entityPlayer != null) {
            EnumFacing dir = EnumFacing.getFront(side);
            BlockPaintingContainer.rotatePicture(entityPlayer, picture, dir, true);
        }
        tilePF.markForUpdate();
        return EnumActionResult.SUCCESS;
    }
    
    @Override
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        getPictureTag(itemStack).ifPresent(pictureTag-> {
            if (pictureTag.hasKey(Picture.TAG_EDITABLE) &&
                !pictureTag.getBoolean(Picture.TAG_EDITABLE)) {
                list.add(I18n.format(this.getUnlocalizedName() +
                    ".uneditable"));
            }
            list.add(pictureSizeInformation(pictureTag));
        });
    }
    
    public static boolean fillPicture(Picture picture, ItemStack itemStack) {
        NBTTagCompound pictureTag = getPictureTag(itemStack).orElse(null);
        if (pictureTag != null && !pictureTag.hasNoTags()) {
            picture.deserializeNBT(pictureTag);
            if (itemStack.hasDisplayName()) {
                picture.setName(itemStack.getDisplayName());
            }
            return true;
        }
        return false;
    }
    
    public static void setPictureTag(ItemStack stack, NBTTagCompound pictureTag) {
        ItemPaintingFrame.removePictureTagName(pictureTag).ifPresent(stack::setStackDisplayName);
        ItemUtils.getOrCreateBlockEntityTag(stack).setTag(TAG_PICTURE, pictureTag);
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack) {
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.hasKey(TAG_PICTURE, 10))
                        .map(tag->tag.getCompoundTag(TAG_PICTURE));
    }
    
    public static ItemStack getPictureAsItem(Picture picture) {
        ItemStack stack = new ItemStack(Core.instance.painting.itemPainting);
        if (picture == null) {
            return stack;
        }
        setPictureTag(stack, picture.serializeNBT());
        return stack;
    }
    
    public static ItemStack getSizedItem(IPictureSize size) {
        return getSizedItem(size.getWidth(), size.getHeight());
    }
    
    public static ItemStack getSizedItem(int width, int height) {
        ItemStack stack = new ItemStack(Core.instance.painting.itemPainting);
        NBTTagCompound pictureTag = new NBTTagCompound();
        NBTTagCompound imageTag = new NBTTagCompound();
        imageTag.setInteger(Picture.TAG_IMAGE_WIDTH, width);
        imageTag.setInteger(Picture.TAG_IMAGE_HEIGHT, height);
        pictureTag.setTag(Picture.TAG_IMAGE, imageTag);
        setPictureTag(stack, pictureTag);
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    public static String pictureSizeInformation(NBTTagCompound pictureTag) {
        if (pictureTag.hasNoTags()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        NBTBase imageTagBase = pictureTag.getTag(Picture.TAG_IMAGE);
        NBTTagCompound imageTag;
        if (imageTagBase != null && imageTagBase instanceof NBTTagCompound) {
            imageTag = (NBTTagCompound)imageTagBase;
        } else {
            imageTag = new NBTTagCompound();
        }
        sb.append(imageTag.getInteger(Picture.TAG_IMAGE_WIDTH));
        sb.append("×");
        sb.append(imageTag.getInteger(Picture.TAG_IMAGE_HEIGHT));
        return sb.toString();
    }
}
