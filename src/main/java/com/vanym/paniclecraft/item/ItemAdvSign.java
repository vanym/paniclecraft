package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererAdvSign;
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
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
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
        getLines(stack).ifPresent(lines-> {
            if (Screen.hasShiftDown()) {
                IntStream.range(0, lines.size())
                         .mapToObj(lines::getString)
                         .map(StringTextComponent::new)
                         .peek(line->line.applyTextStyle(TextFormatting.GRAY))
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
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        if (!context.isPlacerSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                CompoundNBT signTag = null;
                if (tile instanceof SignTileEntity) {
                    SignTileEntity tileS = (SignTileEntity)tile;
                    signTag = new CompoundNBT();
                    ListNBT linesTag = new ListNBT();
                    Arrays.stream(tileS.signText)
                          .map(ITextComponent::getFormattedText)
                          .map(StringNBT::new)
                          .forEachOrdered(linesTag::add);
                    signTag.put(TileEntityAdvSign.TAG_LINES, linesTag);
                } else if (tile instanceof TileEntityAdvSign) {
                    TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                    signTag = new CompoundNBT();
                    tileAS.write(signTag, true);
                }
                if (signTag != null) {
                    putSign(stack, signTag);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        if (!world.getBlockState(pos).getMaterial().isSolid()) {
            return ActionResultType.FAIL;
        }
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
            getSign(stack).ifPresent(signTag->tileAS.read(signTag, true));
            if (facing == Direction.UP) {
                tileAS.setStick(true);
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
        if (tileAS == null || tileAS.lines.stream().allMatch(String::isEmpty)) {
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
        CompoundNBT tag = stack.getTag();
        tag.remove(TAG_SIGN);
        ItemUtils.cleanTag(stack);
    }
    
    public static Optional<CompoundNBT> getSign(ItemStack stack) {
        return ItemUtils.getTag(stack)
                        .filter(tag->tag.contains(TAG_SIGN, 10))
                        .map(tag->tag.getCompound(TAG_SIGN));
    }
    
    protected static Optional<ListNBT> getLines(ItemStack stack) {
        return getSign(stack).filter(tag->tag.contains(TileEntityAdvSign.TAG_LINES, 9))
                             .map(tag->tag.getList(TileEntityAdvSign.TAG_LINES, 8));
    }
}
