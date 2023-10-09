package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Optional;

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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemPainting extends ItemMod3 {
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    protected final Block block;
    
    public ItemPainting(Block block) {
        this.setRegistryName("painting");
        this.block = block;
    }
    
    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
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
        if (!player.isSneaking()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                return this.onItemUseOnFrame(stack, player, world, tilePF, side);
            }
            for (; i < Core.instance.painting.config.paintingPlaceStack; i++) {
                Block block = world.getBlock(x, y, z);
                if (block != this.block) {
                    break;
                }
                int meta = world.getBlockMetadata(x, y, z);
                if (meta != side) {
                    break;
                }
                ForgeDirection stackdir =
                        BlockPaintingContainer.getStackDirection(player, dir);
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
        if (!player.canPlayerEdit(x, y, z, side, stack)
            || !this.block.canPlaceBlockAt(world, x, y, z)
            || !world.setBlock(x, y, z, this.block, side, 3)) {
            return false;
        }
        this.block.onBlockPlacedBy(world, x, y, z, player, stack);
        this.block.onPostBlockPlaced(world, x, y, z, side);
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
                              this.block.stepSound.func_150496_b(),
                              (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                              this.block.stepSound.getPitch() * 0.8F);
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(x, y, z);
        SideUtils.runSync(!world.isRemote, tileP, ()-> {
            Picture picture = tileP.getPicture();
            fillPicture(picture, stack);
            if (player != null) {
                BlockPaintingContainer.rotatePicture(player, picture, dir, true);
            }
        });
        --stack.stackSize;
        return true;
    }
    
    public boolean onItemUseOnFrame(
            ItemStack stack,
            EntityPlayer player,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPicture(side) != null) {
            return false;
        }
        SideUtils.runSync(!world.isRemote, tilePF, ()-> {
            Picture picture = tilePF.createPicture(side, stack);
            if (player != null) {
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                BlockPaintingContainer.rotatePicture(player, picture, dir, true);
            }
        });
        --stack.stackSize;
        tilePF.markForUpdate();
        world.notifyBlockChange(tilePF.xCoord, tilePF.yCoord, tilePF.zCoord, tilePF.getBlockType());
        JUtils.runIf(world.isRemote, ()->Core.instance.shooter.once(this::showRemoveTooltip));
        return true;
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
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
        GuiUtils.showFloatingTooltip(new ChatComponentTranslation(
                this.getUnlocalizedName() + ".remove_tooltip",
                GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode()),
                GameSettings.getKeyDisplayString(settings.keyBindUseItem.getKeyCode())));
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
