package com.vanym.paniclecraft.item;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public abstract class ItemPaintingTool extends Item implements IPaintingTool {
    
    public static final String TAG_RADIUS = "Radius";
    
    protected static final double MAX_RADIUS = 256.0D;
    
    protected static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");
    
    @OnlyIn(Dist.CLIENT)
    protected Set<MessagePaintingToolUse> brushUseMessages;
    
    public ItemPaintingTool(Item.Properties properties) {
        super(properties);
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()->this.brushUseMessages = new HashSet<>());
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (EffectiveSide.get().isClient()) {
            this.onUsingTickClient(stack, player, count);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void onUsingTickClient(ItemStack stack, LivingEntity player, int count) {
        Minecraft mc = Minecraft.getInstance();
        MessagePaintingToolUse mes = makeBrushUseMessage(mc.world, mc.objectMouseOver);
        if (mes != null) {
            this.brushUseMessages.add(mes);
        }
        this.flashBrushUseMessages();
    }
    
    @Override
    public void onPlayerStoppedUsing(
            ItemStack stack,
            World world,
            LivingEntity player,
            int count) {
        if (EffectiveSide.get().isClient()) {
            this.flashBrushUseMessages();
        }
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void renderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack itemStack = mc.player.getActiveItemStack();
        if (itemStack != null && itemStack.getItem() == this) {
            MessagePaintingToolUse mes = makeBrushUseMessage(mc.world, mc.objectMouseOver);
            if (mes != null) {
                this.brushUseMessages.add(mes);
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void flashBrushUseMessages() {
        for (MessagePaintingToolUse mes : this.brushUseMessages) {
            Core.instance.network.sendToServer(mes);
        }
        this.brushUseMessages.clear();
    }
    
    @OnlyIn(Dist.CLIENT)
    public static MessagePaintingToolUse makeBrushUseMessage(
            World world,
            RayTraceResult atarget) {
        if (!BlockRayTraceResult.class.isInstance(atarget)
            || atarget.getType() != RayTraceResult.Type.BLOCK) {
            return null;
        }
        BlockRayTraceResult target = (BlockRayTraceResult)atarget;
        BlockPos pos = target.getPos();
        int side = target.getFace().getIndex();
        boolean tile = true;
        IPictureSize picture = WorldPictureProvider.ANYTILE.getPicture(world, pos, side);
        if (picture == null) {
            tile = false;
            if (Core.instance.painting.config.allowPaintOnBlock) {
                picture = EntityPaintOnBlock.getExistingPicture(world, pos, side);
                if (picture == null && EntityPaintOnBlock.isValidBlockSide(world, pos, side)) {
                    picture = Core.instance.painting.config.paintOnBlockDefaultSize;
                }
            }
        }
        if (picture == null) {
            return null;
        }
        PaintingSide pside = PaintingSide.getSide(side);
        Vec3d inBlock = GeometryUtils.getInBlockVec(target);
        Vec3d inPainting = pside.axes.toSideCoords(inBlock);
        int px = (int)(inPainting.x * picture.getWidth());
        int py = (int)(inPainting.y * picture.getHeight());
        MessagePaintingToolUse message =
                new MessagePaintingToolUse(pos, px, py, (byte)pside.ordinal(), tile);
        return message;
    }
    
    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }
    
    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() instanceof ItemPaintingTool
            && newStack.getItem() instanceof ItemPaintingTool;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(ActionResultType.FAIL, stack);
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity entityPlayer = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Hand hand = context.getHand();
        int side = context.getFace().getIndex();
        if (WorldPictureProvider.ANYTILE.getPicture(world, pos, side) != null
            || (Core.instance.painting.config.allowPaintOnBlock
                && (EntityPaintOnBlock.getExistingPicture(world, pos, side) != null
                    || EntityPaintOnBlock.isValidBlockSide(world, pos, side)))) {
            entityPlayer.setActiveHand(hand);
            if (EffectiveSide.get().isClient()) {
                this.brushUseMessages.clear();
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        if (itemStack.hasTag()) {
            CompoundNBT itemTag = itemStack.getTag();
            if (itemTag.contains(TAG_RADIUS)) {
                double radius = this.getPaintingToolRadius(itemStack, null);
                list.add(new TranslationTextComponent(
                        "item." + DEF.MOD_ID + ".paintingtool.radius").appendText(": ")
                                                                      .appendText(NUMBER_FORMATTER.format(radius))
                                                                      .applyTextStyle(TextFormatting.GRAY));
            }
        }
    }
    
    public static Double getTagRadius(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            CompoundNBT itemTag = itemStack.getTag();
            if (itemTag.contains(TAG_RADIUS)) {
                double radius = itemTag.getDouble(TAG_RADIUS);
                return Math.min(MAX_RADIUS, radius);
            }
        }
        return null;
    }
    
    protected static double getRadius(SortedMap<Integer, Double> radiuses, IPictureSize picture) {
        int row;
        if (picture != null) {
            row = Math.min(picture.getWidth(), picture.getHeight());
        } else {
            row = 0;
        }
        return getRadius(radiuses, row);
    }
    
    protected static double getRadius(SortedMap<Integer, Double> radiuses, int row) {
        try {
            int key = radiuses.headMap(row + 1).lastKey();
            return radiuses.get(key);
        } catch (NoSuchElementException e) {
            return 0.0D;
        }
    }
}
