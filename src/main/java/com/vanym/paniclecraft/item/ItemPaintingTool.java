package com.vanym.paniclecraft.item;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemPaintingTool extends ItemMod3 implements IPaintingTool {
    
    public static final String TAG_RADIUS = "Radius";
    
    protected static final double MAX_RADIUS = 256.0D;
    
    protected static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");
    
    @SideOnly(Side.CLIENT)
    protected Set<MessagePaintingToolUse> brushUseMessages;
    
    protected ItemPaintingTool() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.brushUseMessages = new HashSet<>();
        }
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.onUsingTickClient(stack, player, count);
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected void onUsingTickClient(ItemStack stack, EntityLivingBase player, int count) {
        Minecraft mc = Minecraft.getMinecraft();
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
            EntityLivingBase player,
            int count) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.flashBrushUseMessages();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void flashBrushUseMessages() {
        for (MessagePaintingToolUse mes : this.brushUseMessages) {
            Core.instance.network.sendToServer(mes);
        }
        this.brushUseMessages.clear();
    }
    
    @SideOnly(Side.CLIENT)
    public static MessagePaintingToolUse makeBrushUseMessage(
            World world,
            RayTraceResult target) {
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        BlockPos pos = target.getBlockPos();
        int side = target.sideHit.getIndex();
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
    public int getMaxItemUseDuration(ItemStack itemstack) {
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
            EntityPlayer player,
            EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
    
    @Override
    public EnumActionResult onItemUse(
            EntityPlayer entityPlayer,
            World world,
            BlockPos pos,
            EnumHand hand,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ) {
        int side = facing.getIndex();
        if (WorldPictureProvider.ANYTILE.getPicture(world, pos, side) != null
            || (Core.instance.painting.config.allowPaintOnBlock
                && (EntityPaintOnBlock.getExistingPicture(world, pos, side) != null
                    || EntityPaintOnBlock.isValidBlockSide(world, pos, side)))) {
            entityPlayer.setActiveHand(hand);
            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                this.brushUseMessages.clear();
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
    
    @Override
    public boolean isFull3D() {
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
                double radius = this.getPaintingToolRadius(itemStack, null);
                StringBuilder sb = new StringBuilder();
                sb.append(I18n.format(getUnlocalizedName("paintingtool") +
                    ".radius"));
                sb.append(": ");
                sb.append(NUMBER_FORMATTER.format(radius));
                list.add(sb.toString());
            }
        }
    }
    
    public static Double getTagRadius(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
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
    
    @SideOnly(Side.CLIENT)
    public static class PerFrameEventHandler {
        
        @SubscribeEvent
        public void renderWorldLast(RenderWorldLastEvent event) {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack stack = mc.player.getActiveItemStack();
            if (stack.getItem() instanceof ItemPaintingTool) {
                ItemPaintingTool item = (ItemPaintingTool)stack.getItem();
                MessagePaintingToolUse mes = makeBrushUseMessage(mc.world, mc.objectMouseOver);
                if (mes != null) {
                    item.brushUseMessages.add(mes);
                }
            }
        }
    }
}
