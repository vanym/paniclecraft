package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererAdvSign;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class ItemAdvSign extends Item {
    
    public static final String TAG_SIGN = "Sign";
    
    public ItemAdvSign() {
        super(Props.create().maxStackSize(16).setTEISR(()->ItemRendererAdvSign::new));
        this.setRegistryName("advanced_sign");
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack stack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        getSide(stack, !Screen.hasControlDown()).map(AdvSignText::getLines).ifPresent(lines-> {
            if (Screen.hasShiftDown()) {
                lines.stream()
                     .map(ITextComponent::getString)
                     .map(StringTextComponent::new)
                     .forEachOrdered(list::add);
            } else {
                list.add(new TranslationTextComponent(this.getTranslationKey() + ".showtext"));
            }
        });
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getSign(stack).isPresent() && player.isSneaking()) {
            removeSign(stack);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
    }
    
    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        if (context.isPlacerSneaking()) {
            return ActionResultType.PASS;
        }
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        TileEntity tile = world.getTileEntity(pos);
        CompoundNBT signTag = null;
        if (tile instanceof SignTileEntity) {
            SignTileEntity tileS = (SignTileEntity)tile;
            signTag = new CompoundNBT();
            AdvSignText text = new AdvSignText();
            List<ITextComponent> lines = text.getLines();
            lines.clear();
            Arrays.stream(tileS.signText)
                  .map(ITextComponent::getFormattedText)
                  .map(FormattingUtils::parseLine)
                  .forEachOrdered(lines::add);
            signTag.put(TileEntityAdvSign.TAG_FRONTTEXT, text.serializeNBT());
            signTag.put(TileEntityAdvSign.TAG_BACKTEXT, new AdvSignText(4).serializeNBT());
            signTag.putInt(TileEntityAdvSign.TAG_STANDCOLOR, Color.WHITE.getRGB());
        } else if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            signTag = new CompoundNBT();
            tileAS.write(signTag, true);
        }
        if (signTag != null) {
            if (TileEntityAdvSign.isValidTag(signTag)) {
                putSign(stack, signTag);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        if (!world.getBlockState(pos).getMaterial().isSolid()) {
            return ActionResultType.FAIL;
        }
        ItemStack stack = context.getItem();
        pos = pos.offset(facing);
        Block block = Core.instance.advSign.blockAdvSign;
        PlayerEntity player = context.getPlayer();
        if (!player.canPlayerEdit(pos, facing, stack)
            || !world.setBlockState(pos,
                                    block.getStateForPlacement(new BlockItemUseContext(context)),
                                    11)) {
            return ActionResultType.FAIL;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            getSign(stack).filter(TileEntityAdvSign::isValidTag)
                          .ifPresent(signTag->tileAS.read(signTag, true));
            if (facing == Direction.UP) {
                tileAS.setForm(AdvSignForm.STICK_DOWN);
                double direction = Math.round(180.0D + player.rotationYaw);
                tileAS.setDirection(direction);
            }
            if (facing == Direction.DOWN) {
                tileAS.setDirection(player.getHorizontalFacing().getHorizontalAngle());
            }
            tileAS.setEditor(player);
        }
        stack.shrink(1);
        if (EffectiveSide.get().isClient()) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getTileEntity(pos);
            Minecraft.getInstance().displayGuiScreen(new GuiEditAdvSign(tileAS));
        }
        return ActionResultType.SUCCESS;
    }
    
    public static ItemStack getSavedSign(TileEntityAdvSign tileAS) {
        ItemStack stack = new ItemStack(Core.instance.advSign.itemAdvSign);
        if (tileAS == null || (tileAS.getFront().isEmpty() && tileAS.getBack().isEmpty())) {
            return stack;
        }
        CompoundNBT signTag = new CompoundNBT();
        tileAS.write(signTag, true);
        putSign(stack, signTag);
        return stack;
    }
    
    protected static void putSign(ItemStack stack, CompoundNBT tag) {
        ItemUtils.getOrCreateTag(stack).put(TAG_SIGN, tag);
    }
    
    protected static void removeSign(ItemStack stack) {
        ItemUtils.getTag(stack).ifPresent(tag->tag.remove(TAG_SIGN));
        ItemUtils.cleanTag(stack);
    }
    
    public static Optional<CompoundNBT> getSign(ItemStack stack) {
        return ItemUtils.getTag(stack)
                        .filter(tag->tag.contains(TAG_SIGN, 10))
                        .map(tag->tag.getCompound(TAG_SIGN));
    }
    
    protected static Optional<AdvSignText> getSide(ItemStack stack, boolean front) {
        String TAG_TEXT = front ? TileEntityAdvSign.TAG_FRONTTEXT : TileEntityAdvSign.TAG_BACKTEXT;
        return getSign(stack).filter(tag->tag.contains(TAG_TEXT, 10))
                             .map(tag->tag.getCompound(TAG_TEXT))
                             .map(AdvSignText::new);
    }
}
