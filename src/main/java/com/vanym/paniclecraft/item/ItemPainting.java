package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPainting extends ItemMod3 {
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    public ItemPainting() {
        super(new Item.Properties().setTEISR(()->ItemRendererPainting::create));
        this.setRegistryName("painting");
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemStack = context.getItem();
        PlayerEntity entityPlayer = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
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
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if ((block != painting) || (side != state.get(BlockPainting.FACING))) {
                    break;
                }
                Direction stackdir = BlockPaintingContainer.getStackDirection(entityPlayer, side);
                if (stackdir == null) {
                    break;
                }
                pos = pos.offset(stackdir);
            }
        }
        if (i == 0) {
            pos = pos.offset(side);
        }
        if (!entityPlayer.canPlayerEdit(pos, side, itemStack)) {
            return ActionResultType.FAIL;
        }
        BlockState state = painting.getStateForPlacement(new BlockItemUseContext(context));
        if (!world.setBlockState(pos, state, 11)) {
            return ActionResultType.FAIL;
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
        return ActionResultType.SUCCESS;
    }
    
    public ActionResultType onItemUseOnFrame(
            ItemStack itemStack,
            PlayerEntity entityPlayer,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPicture(side) != null) {
            return ActionResultType.FAIL;
        }
        Picture picture = tilePF.createPicture(side);
        fillPicture(picture, itemStack);
        itemStack.shrink(1);
        if (entityPlayer != null) {
            Direction dir = Direction.byIndex(side);
            BlockPaintingContainer.rotatePicture(entityPlayer, picture, dir, true);
        }
        tilePF.markForUpdate();
        return ActionResultType.SUCCESS;
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        getPictureTag(itemStack).ifPresent(pictureTag-> {
            if (pictureTag.contains(Picture.TAG_EDITABLE) &&
                !pictureTag.getBoolean(Picture.TAG_EDITABLE)) {
                list.add(new TranslationTextComponent(
                        this.getTranslationKey() + ".uneditable"));
            }
            list.add(new StringTextComponent(pictureSizeInformation(pictureTag)));
        });
    }
    
    public static boolean fillPicture(Picture picture, ItemStack itemStack) {
        CompoundNBT pictureTag = getPictureTag(itemStack).orElse(null);
        if (pictureTag != null && !pictureTag.isEmpty()) {
            picture.deserializeNBT(pictureTag);
            if (itemStack.hasDisplayName()) {
                picture.setName(itemStack.getDisplayName().getFormattedText());
            }
            return true;
        }
        return false;
    }
    
    public static void putPictureTag(ItemStack stack, CompoundNBT pictureTag) {
        ItemPaintingFrame.removePictureTagName(pictureTag)
                         .map(StringTextComponent::new)
                         .ifPresent(stack::setDisplayName);
        ItemUtils.getOrCreateBlockEntityTag(stack).put(TAG_PICTURE, pictureTag);
    }
    
    public static Optional<CompoundNBT> getPictureTag(ItemStack stack) {
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.contains(TAG_PICTURE, 10))
                        .map(tag->tag.getCompound(TAG_PICTURE));
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
        CompoundNBT pictureTag = new CompoundNBT();
        CompoundNBT imageTag = new CompoundNBT();
        imageTag.putInt(Picture.TAG_IMAGE_WIDTH, width);
        imageTag.putInt(Picture.TAG_IMAGE_HEIGHT, height);
        pictureTag.put(Picture.TAG_IMAGE, imageTag);
        putPictureTag(stack, pictureTag);
        return stack;
    }
    
    @OnlyIn(Dist.CLIENT)
    public static String pictureSizeInformation(CompoundNBT pictureTag) {
        if (pictureTag.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        INBT imageTagBase = pictureTag.get(Picture.TAG_IMAGE);
        CompoundNBT imageTag;
        if (imageTagBase != null && imageTagBase instanceof CompoundNBT) {
            imageTag = (CompoundNBT)imageTagBase;
        } else {
            imageTag = new CompoundNBT();
        }
        sb.append(imageTag.getInt(Picture.TAG_IMAGE_WIDTH));
        sb.append("×");
        sb.append(imageTag.getInt(Picture.TAG_IMAGE_HEIGHT));
        return sb.toString();
    }
}
