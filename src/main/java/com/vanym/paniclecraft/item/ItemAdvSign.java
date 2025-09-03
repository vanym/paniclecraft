package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class ItemAdvSign extends Item {
    
    public ItemAdvSign() {
        super(Props.create().stacksTo(16).setTEISR(()->ItemRendererAdvSign::new));
        this.setRegistryName("advanced_sign");
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack stack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        boolean showFront = Screen.hasShiftDown();
        boolean showBack = Screen.hasControlDown();
        if (showFront || showBack) {
            getSide(stack, showFront).map(AdvSignText::getLines).ifPresent(lines-> {
                lines.stream()
                     .map(ITextComponent::getString)
                     .map(StringTextComponent::new)
                     .peek(line->line.withStyle(TextFormatting.GRAY))
                     .forEachOrdered(list::add);
            });
        } else if (getSide(stack, false).filter(t->!t.isEmpty()).isPresent()) {
            Stream.of(I18n.get(this.getDescriptionId() +
                ".showtext.both").split(System.lineSeparator()))
                  .map(StringTextComponent::new)
                  .peek(line->line.withStyle(TextFormatting.GRAY))
                  .forEachOrdered(list::add);
        } else if (getSide(stack, true).isPresent()) {
            Stream.of(I18n.get(this.getDescriptionId() +
                ".showtext.frontonly").split(System.lineSeparator()))
                  .map(StringTextComponent::new)
                  .peek(line->line.withStyle(TextFormatting.GRAY))
                  .forEachOrdered(list::add);
        }
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    public ActionResult<ItemStack> use(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getSign(stack).isPresent() && player.isSneaking()) {
            removeSign(stack);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
    }
    
    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        if (context.isSneaking()) {
            return ActionResultType.PASS;
        }
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        TileEntity tile = world.getBlockEntity(pos);
        CompoundNBT signTag = null;
        if (tile instanceof SignTileEntity) {
            SignTileEntity tileS = (SignTileEntity)tile;
            signTag = new CompoundNBT();
            AdvSignText text = new AdvSignText();
            List<ITextComponent> lines = text.getLines();
            lines.clear();
            Arrays.stream(tileS.messages)
                  .map(ITextComponent::getColoredString)
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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        if (!world.getBlockState(pos).getMaterial().isSolid()) {
            return ActionResultType.FAIL;
        }
        ItemStack stack = context.getItemInHand();
        pos = pos.relative(facing);
        Block block = Core.instance.advSign.blockAdvSign;
        PlayerEntity player = context.getPlayer();
        if (!player.mayUseItemAt(pos, facing, stack)
            || !world.setBlock(pos,
                                    block.getStateForPlacement(new BlockItemUseContext(context)),
                                    11)) {
            return ActionResultType.FAIL;
        }
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityAdvSign) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
            getSign(stack).filter(TileEntityAdvSign::isValidTag)
                          .ifPresent(signTag->tileAS.read(signTag, true));
            if (facing == Direction.UP) {
                tileAS.setForm(AdvSignForm.STICK_DOWN);
                double direction = Math.round(180.0D + player.yRot);
                tileAS.setDirection(direction);
            }
            if (facing == Direction.DOWN) {
                tileAS.setDirection(player.getDirection().toYRot());
            }
            tileAS.setEditor(player.getUUID());
        }
        stack.shrink(1);
        if (EffectiveSide.get().isClient()) {
            TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getBlockEntity(pos);
            Minecraft.getInstance().setScreen(new GuiEditAdvSign(tileAS));
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
        ItemUtils.getOrCreateBlockEntityTag(stack).merge(tag);
    }
    
    protected static void removeSign(ItemStack stack) {
        stack.removeTagKey(ItemUtils.BLOCK_ENTITY_TAG);
        ItemUtils.cleanTag(stack);
    }
    
    public static Optional<CompoundNBT> getSign(ItemStack stack) {
        return ItemUtils.getBlockEntityTag(stack);
    }
    
    protected static Optional<AdvSignText> getSide(ItemStack stack, boolean front) {
        String TAG_TEXT = front ? TileEntityAdvSign.TAG_FRONTTEXT : TileEntityAdvSign.TAG_BACKTEXT;
        return getSign(stack).filter(tag->tag.contains(TAG_TEXT, 10))
                             .map(tag->tag.getCompound(TAG_TEXT))
                             .map(AdvSignText::new);
    }
}
