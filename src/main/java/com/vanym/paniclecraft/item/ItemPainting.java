package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;
import com.vanym.paniclecraft.utils.JUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPainting extends ItemMod3 {
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    protected final Block block;
    
    public ItemPainting(Block block) {
        this.setRegistryName("painting");
        this.block = block;
    }
    
    @Override
    public EnumActionResult onItemUse(
            EntityPlayer player,
            World world,
            BlockPos pos,
            EnumHand hand,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        int i = 0;
        if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                return this.onItemUseOnFrame(stack, player, world, tilePF,
                                             side.getIndex());
            }
            for (; i < Core.instance.painting.config.paintingPlaceStack; i++) {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (block != this.block) {
                    break;
                }
                int meta = block.getMetaFromState(state);
                if (meta != side.getIndex()) {
                    break;
                }
                EnumFacing stackdir = BlockPaintingContainer.getStackDirection(player, side);
                if (stackdir == null) {
                    break;
                }
                pos = pos.offset(stackdir);
            }
        }
        if (i == 0) {
            pos = pos.offset(side);
        }
        if (!player.canPlayerEdit(pos, side, stack)
            || !this.block.canPlaceBlockAt(world, pos)) {
            return EnumActionResult.FAIL;
        }
        IBlockState state = this.block.getStateForPlacement(world, pos, side,
                                                            hitX, hitY, hitZ,
                                                            0, player, hand);
        if (!world.setBlockState(pos, state, 11)) {
            return EnumActionResult.FAIL;
        }
        this.block.onBlockPlacedBy(world, pos, state, player, stack);
        state = world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
        world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(pos);
        SideUtils.runSync(!world.isRemote, tileP, ()-> {
            Picture picture = tileP.getPicture();
            fillPicture(picture, stack);
            if (player != null) {
                BlockPaintingContainer.rotatePicture(player, picture, side, true);
            }
        });
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
    
    public EnumActionResult onItemUseOnFrame(
            ItemStack stack,
            EntityPlayer player,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPicture(side) != null) {
            return EnumActionResult.FAIL;
        }
        SideUtils.runSync(!world.isRemote, tilePF, ()-> {
            Picture picture = tilePF.createPicture(side, stack);
            if (player != null) {
                EnumFacing dir = EnumFacing.getFront(side);
                BlockPaintingContainer.rotatePicture(player, picture, dir, true);
            }
        });
        stack.shrink(1);
        tilePF.markForUpdate();
        JUtils.runIf(world.isRemote, ()->Core.instance.shooter.once(this::showRemoveTooltip));
        return EnumActionResult.SUCCESS;
    }
    
    @Override
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }
    
    @Override
    public String getUnlocalizedName() {
        return this.block.getUnlocalizedName();
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.block.getLocalizedName();
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
                list.add(I18n.format(this.getUnlocalizedName() + ".uneditable"));
            }
            list.add(pictureSizeInformation(pictureTag));
        });
    }
    
    @SideOnly(Side.CLIENT)
    protected void showRemoveTooltip() {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        GuiUtils.showFloatingTooltip(new TextComponentTranslation(
                this.getUnlocalizedName() + ".remove_tooltip",
                settings.keyBindSneak.getDisplayName(),
                settings.keyBindUseItem.getDisplayName()));
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
    
    public static void putPictureTag(ItemStack stack, NBTTagCompound pictureTag) {
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
        putPictureTag(stack, picture.serializeNBT());
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
        putPictureTag(stack, pictureTag);
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
