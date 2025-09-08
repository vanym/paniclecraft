package com.vanym.paniclecraft.item;

import java.text.DecimalFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.utils.ClientUtils;
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
    
    protected ItemPaintingTool(Item.Properties properties) {
        super(properties);
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()->this.brushUseMessages =
                Core.instance.painting.paintingToolUseSet);
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (EffectiveSide.get().isClient()) {
            this.onUsingTickClient(stack, player, count);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void onUsingTickClient(ItemStack stack, LivingEntity player, int count) {
        if (!ClientUtils.isMe(player)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        MessagePaintingToolUse mes = makeBrushUseMessage(mc.level, mc.hitResult);
        if (mes != null) {
            this.brushUseMessages.add(mes);
        }
        this.flashBrushUseMessages();
    }
    
    @Override
    public void releaseUsing(
            ItemStack stack,
            World world,
            LivingEntity player,
            int count) {
        if (EffectiveSide.get().isClient() && ClientUtils.isMe(player)) {
            this.flashBrushUseMessages();
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
        BlockPos pos = target.getBlockPos();
        int side = target.getDirection().get3DDataValue();
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
    public ActionResult<ItemStack> use(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return new ActionResult<>(ActionResultType.FAIL, stack);
    }
    
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity entityPlayer = context.getPlayer();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Hand hand = context.getHand();
        int side = context.getClickedFace().get3DDataValue();
        if (WorldPictureProvider.ANYTILE.getPicture(world, pos, side) != null
            || (Core.instance.painting.config.allowPaintOnBlock
                && (EntityPaintOnBlock.getExistingPicture(world, pos, side) != null
                    || EntityPaintOnBlock.isValidBlockSide(world, pos, side)))) {
            entityPlayer.startUsingItem(hand);
            if (EffectiveSide.get().isClient() && ClientUtils.isMe(entityPlayer)) {
                this.brushUseMessages.clear();
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack itemStack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        if (itemStack.hasTag()) {
            CompoundNBT itemTag = itemStack.getTag();
            if (itemTag.contains(TAG_RADIUS)) {
                double radius = this.getPaintingToolRadius(itemStack, null);
                list.add(new TranslationTextComponent(
                        "item." + DEF.MOD_ID + ".paintingtool.radius").append(": ")
                                                                      .append(NUMBER_FORMATTER.format(radius))
                                                                      .withStyle(TextFormatting.GRAY));
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
    
    @OnlyIn(Dist.CLIENT)
    public static class PerFrameEventHandler {
        
        @SubscribeEvent
        public void renderWorldLast(RenderWorldLastEvent event) {
            Minecraft mc = Minecraft.getInstance();
            ItemStack stack = mc.player.getUseItem();
            if (stack.getItem() instanceof ItemPaintingTool) {
                ItemPaintingTool item = (ItemPaintingTool)stack.getItem();
                MessagePaintingToolUse mes = makeBrushUseMessage(mc.level, mc.hitResult);
                if (mes != null) {
                    item.brushUseMessages.add(mes);
                }
            }
        }
    }
}
