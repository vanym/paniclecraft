package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemPaintBrush extends ItemMod3 implements IPaintingTool, IColorizeable {
    
    public static final String TAG_RADIUS = "Radius";
    public static final String TAG_COLOR = "Color";
    
    protected static final double MAX_RADIUS = 256.0D;
    protected static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");
    
    protected static final int DAMAGE_BRUSH = 0;
    protected static final int DAMAGE_SMALLBRUSH = 1;
    protected static final int DAMAGE_FILLER = 4;
    
    @SideOnly(Side.CLIENT)
    public IIcon iconBrushHead;
    @SideOnly(Side.CLIENT)
    public IIcon iconBrushBody;
    @SideOnly(Side.CLIENT)
    public IIcon iconSmallBrushHead;
    @SideOnly(Side.CLIENT)
    public IIcon iconSmallBrushBody;
    @SideOnly(Side.CLIENT)
    public IIcon iconFillerHead;
    @SideOnly(Side.CLIENT)
    public IIcon iconFillerBody;
    
    @SideOnly(Side.CLIENT)
    protected Set<MessagePaintBrushUse> brushUseMessages = new HashSet<>();
    
    public ItemPaintBrush() {
        super();
        this.setUnlocalizedName("paintBrush");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public ItemStack getBrush() {
        return new ItemStack(this, 1, DAMAGE_BRUSH);
    }
    
    public ItemStack getSmallBrush() {
        return new ItemStack(this, 1, DAMAGE_SMALLBRUSH);
    }
    
    public ItemStack getFiller() {
        return new ItemStack(this, 1, DAMAGE_FILLER);
    }
    
    @Override
    public boolean isItemTool(ItemStack stack) {
        return true;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        MessagePaintBrushUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
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
        if (itemStack != null && itemStack.getItem() instanceof ItemPaintBrush) {
            MessagePaintBrushUse mes = makeBrushUseMessage(mc.theWorld, mc.objectMouseOver);
            if (mes != null) {
                this.brushUseMessages.add(mes);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void flashBrushUseMessages() {
        for (MessagePaintBrushUse mes : this.brushUseMessages) {
            Core.instance.network.sendToServer(mes);
        }
        this.brushUseMessages.clear();
    }
    
    @SideOnly(Side.CLIENT)
    public static MessagePaintBrushUse makeBrushUseMessage(
            IBlockAccess world,
            MovingObjectPosition target) {
        Picture picture = BlockPaintingContainer.getPicture(world, target);
        if (picture == null) {
            return null;
        }
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        PaintingSide pside = PaintingSide.getSize(target.sideHit);
        Vec3 inBlock = MainUtils.getInBlockVec(target);
        Vec3 inPainting = pside.toPaintingCoords(inBlock);
        int px = (int)(inPainting.xCoord * picture.getWidth());
        int py = (int)(inPainting.yCoord * picture.getHeight());
        MessagePaintBrushUse message =
                new MessagePaintBrushUse(x, y, z, px, py, (byte)pside.ordinal());
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
            float ibx,
            float iby,
            float ibz) {
        Picture picture = BlockPaintingContainer.getPicture(world, x, y, z, side);
        if (picture != null) {
            entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
            if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                this.brushUseMessages.clear();
            }
        }
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
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
                sb.append(StatCollector.translateToLocal(this.getUnlocalizedName() + ".radius"));
                sb.append(": ");
                sb.append(NUMBER_FORMATTER.format(radius));
                list.add(sb.toString());
            }
        }
        if (GuiScreen.isShiftKeyDown()) {
            Color rgbColor = MainUtils.getColorFromInt(this.getColor(itemStack));
            list.add("R: \u00a7c" + rgbColor.getRed());
            list.add("G: \u00a7a" + rgbColor.getGreen());
            list.add("B: \u00a79" + rgbColor.getBlue());
        }
    }
    
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 0) {
            return this.getColor(stack);
        } else {
            return 0xffffff;
        }
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName() + itemStack.getItemDamage();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        switch (damage) {
            default:
            case DAMAGE_BRUSH:
                return (pass == 0 ? this.iconBrushHead : this.iconBrushBody);
            case DAMAGE_SMALLBRUSH:
                return (pass == 0 ? this.iconSmallBrushHead : this.iconSmallBrushBody);
            case DAMAGE_FILLER:
                return (pass == 0 ? this.iconFillerHead : this.iconFillerBody);
        }
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativetabs, List list) {
        if (!(item instanceof ItemPaintBrush)) {
            return;
        }
        ItemPaintBrush brush = (ItemPaintBrush)item;
        list.add(brush.getBrush());
        list.add(brush.getSmallBrush());
        list.add(brush.getFiller());
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        final String PREFIX = DEF.MOD_ID + ":" + this.getName();
        this.iconBrushHead = iconRegister.registerIcon(PREFIX + "_brush_head");
        this.iconBrushBody = iconRegister.registerIcon(PREFIX + "_brush_body");
        this.iconSmallBrushHead = iconRegister.registerIcon(PREFIX + "_smallbrush_head");
        this.iconSmallBrushBody = iconRegister.registerIcon(PREFIX + "_smallbrush_body");
        this.iconFillerHead = iconRegister.registerIcon(PREFIX + "_filler_head");
        this.iconFillerBody = iconRegister.registerIcon(PREFIX + "_filler_body");
    }
    
    @Override
    public boolean isFull3D() {
        return true;
    }
    
    @Override
    public int getColor(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_COLOR)) {
                return itemTag.getInteger(TAG_COLOR);
            }
        }
        return MainUtils.getAlphaless(Core.instance.painting.DEFAULT_COLOR);
    }
    
    @Override
    public void clearColor(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_COLOR)) {
                itemTag.removeTag(TAG_COLOR);
            }
        }
    }
    
    @Override
    public boolean hasCustomColor(ItemStack itemStack) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_COLOR);
    }
    
    @Override
    public void setColor(ItemStack itemStack, int color) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound itemTag = itemStack.getTagCompound();
        itemTag.setInteger(TAG_COLOR, color);
    }
    
    @Override
    public PaintingToolType getPaintingToolType(ItemStack itemStack) {
        switch (itemStack.getItemDamage()) {
            case DAMAGE_BRUSH:
            case DAMAGE_SMALLBRUSH:
                return PaintingToolType.BRUSH;
            case DAMAGE_FILLER:
                return PaintingToolType.FILLER;
        }
        return PaintingToolType.NONE;
    }
    
    @Override
    public Color getPaintingToolColor(ItemStack itemStack) {
        return new Color(this.getColor(itemStack));
    }
    
    @Override
    public double getPaintingToolRadius(ItemStack itemStack, Picture picture) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_RADIUS)) {
                double radius = itemTag.getDouble(TAG_RADIUS);
                return Math.min(MAX_RADIUS, radius);
            }
        }
        int row;
        if (picture != null) {
            row = Math.min(picture.getWidth(), picture.getHeight());
        } else {
            row = 0;
        }
        switch (itemStack.getItemDamage()) {
            case 0:
                return Core.instance.painting.config.getBrushRadius(row);
            case 1:
                return Core.instance.painting.config.getSmallBrushRadius(row);
            default:
                return 0.1D;
        }
    }
}
