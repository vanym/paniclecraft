package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;
import com.vanym.paniclecraft.utils.JUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPainting extends BlockItem {
    
    protected static final String TAG_PICTURE = TileEntityPainting.TAG_PICTURE;
    
    public ItemPainting(Block block) {
        super(block, Props.create().setTEISR(()->ItemRendererPainting::create));
        this.setRegistryName("painting");
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isSneaking()) {
            World world = context.getWorld();
            BlockPos pos = context.getPos();
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityPaintingFrame) {
                TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
                ItemStack stack = context.getItem();
                Direction side = context.getFace();
                return this.onItemUseOnFrame(stack, player, world, tilePF,
                                             side.getIndex());
            }
        }
        return super.onItemUse(context);
    }
    
    @Override
    @Nullable
    public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null || player.isSneaking()) {
            return context;
        }
        World world = context.getWorld();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(context.getPos());
        Direction side = context.getFace();
        Block self = this.getBlock();
        for (int i = 0; i < Core.instance.painting.config.paintingPlaceStack; i++) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != self || side != state.get(BlockPainting.FACING)) {
                break;
            }
            Direction stackdir = BlockPaintingContainer.getStackDirection(player, side);
            if (stackdir == null) {
                break;
            }
            pos.move(stackdir);
        }
        context = BlockItemUseContext.func_221536_a(context, pos, side);
        return context.replacingClickedOnBlock()
            && world.getBlockState(pos).getBlock() != self ? context : null;
    }
    
    @Override
    protected boolean onBlockPlaced(
            BlockPos pos,
            World world,
            @Nullable PlayerEntity player,
            ItemStack stack,
            BlockState state) {
        super.onBlockPlaced(pos, world, player, stack, state);
        TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(pos);
        SideUtils.runSync(!world.isRemote, tileP, ()-> {
            Picture picture = tileP.getPicture();
            fillPicture(picture, stack);
            if (player != null) {
                Direction side = state.get(BlockPainting.FACING);
                BlockPaintingContainer.rotatePicture(player, picture, side, true);
            }
        });
        return true;
    }
    
    public ActionResultType onItemUseOnFrame(
            ItemStack stack,
            PlayerEntity player,
            World world,
            TileEntityPaintingFrame tilePF,
            int side) {
        if (tilePF.getPicture(side) != null) {
            return ActionResultType.FAIL;
        }
        SideUtils.runSync(!world.isRemote, tilePF, ()-> {
            Picture picture = tilePF.createPicture(side, stack);
            if (player != null) {
                Direction dir = Direction.byIndex(side);
                BlockPaintingContainer.rotatePicture(player, picture, dir, true);
            }
        });
        stack.shrink(1);
        tilePF.markForUpdate();
        JUtils.runIf(world.isRemote, ()->Core.instance.shooter.once(this::showRemoveTooltip));
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
            Stream.Builder<ITextComponent> lines = Stream.builder();
            if (pictureTag.contains(Picture.TAG_EDITABLE) &&
                !pictureTag.getBoolean(Picture.TAG_EDITABLE)) {
                lines.add(new TranslationTextComponent(
                        this.getTranslationKey() + ".uneditable"));
            }
            lines.add(new StringTextComponent(pictureSizeInformation(pictureTag)));
            lines.build()
                 .peek(line->line.applyTextStyle(TextFormatting.GRAY))
                 .forEachOrdered(list::add);
        });
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void showRemoveTooltip() {
        Minecraft mc = Minecraft.getInstance();
        GameSettings settings = mc.gameSettings;
        GuiUtils.showFloatingTooltip(new TranslationTextComponent(
                this.getTranslationKey() + ".remove_tooltip",
                settings.keyBindSneak.getLocalizedName(),
                settings.keyBindUseItem.getLocalizedName()));
    }
    
    public static boolean fillPicture(Picture picture, ItemStack itemStack) {
        CompoundNBT pictureTag = getPictureTag(itemStack).orElse(null);
        if (pictureTag != null && !pictureTag.isEmpty()) {
            picture.deserializeNBT(pictureTag);
            if (itemStack.hasDisplayName()) {
                picture.setName(itemStack.getDisplayName());
            }
            return true;
        }
        return false;
    }
    
    public static void putPictureTag(ItemStack stack, CompoundNBT pictureTag) {
        ItemPaintingFrame.removePictureTagName(pictureTag).ifPresent(stack::setDisplayName);
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
