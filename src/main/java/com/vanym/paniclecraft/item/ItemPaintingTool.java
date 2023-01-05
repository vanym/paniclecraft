package com.vanym.paniclecraft.item;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class ItemPaintingTool extends ItemMod3 implements IPaintingTool {
    
    public static final String TAG_RADIUS = "Radius";
    
    protected static final double MAX_RADIUS = 256.0D;
    
    protected static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");
    
    @SideOnly(Side.CLIENT)
    protected Set<MessagePaintingToolUse> brushUseMessages = new HashSet<>();
    
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        MessagePaintingToolUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
        if (mes != null) {
            this.brushUseMessages.add(mes);
        }
        this.flashBrushUseMessages();
    }
    
    @Override
    public void onPlayerStoppedUsing(
            ItemStack itemStack,
            World world,
            EntityPlayer player,
            int count) {
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        this.flashBrushUseMessages();
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderWorldLast(net.minecraftforge.client.event.RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemStack = mc.thePlayer.getItemInUse();
        if (itemStack != null && itemStack.getItem() == this) {
            MessagePaintingToolUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
            if (mes != null) {
                this.brushUseMessages.add(mes);
            }
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
            MovingObjectPosition target) {
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        int side = target.sideHit;
        boolean tile = true;
        IPictureSize picture = BlockPaintingContainer.getPicture(world, x, y, z, side);
        if (picture == null) {
            tile = false;
            if (Core.instance.painting.config.allowPaintOnBlock) {
                picture = EntityPaintOnBlock.getExistingPicture(world, x, y, z, side);
                if (picture == null && EntityPaintOnBlock.isValidBlockSide(world, x, y, z, side)) {
                    picture = Core.instance.painting.config.paintOnBlockDefaultSize;
                }
            }
        }
        if (picture == null) {
            return null;
        }
        PaintingSide pside = PaintingSide.getSize(side);
        Vec3 inBlock = MainUtils.getInBlockVec(target);
        Vec3 inPainting = pside.toPaintingCoords(inBlock);
        int px = (int)(inPainting.xCoord * picture.getWidth());
        int py = (int)(inPainting.yCoord * picture.getHeight());
        MessagePaintingToolUse message =
                new MessagePaintingToolUse(x, y, z, px, py, (byte)pside.ordinal(), tile);
        return message;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 72000;
    }
    
    @Override
    public boolean onItemUse(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (BlockPaintingContainer.getPicture(world, x, y, z, side) != null
            || (Core.instance.painting.config.allowPaintOnBlock
                && (EntityPaintOnBlock.getExistingPicture(world, x, y, z, side) != null
                    || EntityPaintOnBlock.isValidBlockSide(world, x, y, z, side)))) {
            entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                this.brushUseMessages.clear();
            }
        }
        return false;
    }
    
    @Override
    public boolean isItemTool(ItemStack stack) {
        return true;
    }
    
    @Override
    public boolean isFull3D() {
        return true;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
                double radius = this.getPaintingToolRadius(itemStack, null);
                StringBuilder sb = new StringBuilder();
                sb.append(StatCollector.translateToLocal("text.paintingtool.radius"));
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
}
