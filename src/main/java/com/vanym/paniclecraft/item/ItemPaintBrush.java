package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.container.GuiPalette;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.utils.ColorUtils;

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

public class ItemPaintBrush extends ItemPaintingTool implements IColorizeable {
    
    public static final String TAG_COLOR = "Color";
    
    protected static final int DAMAGE_BRUSH = 0;
    protected static final int DAMAGE_SMALLBRUSH = 1;
    protected static final int DAMAGE_FILLER = 4;
    protected static final int DAMAGE_COLORPICKER = 6;
    
    protected static final Map<Integer, String> SUBTYPES;
    static {
        Map<Integer, String> subtypes = new HashMap<>();
        subtypes.put(DAMAGE_BRUSH, "paintingtool_brush");
        subtypes.put(DAMAGE_SMALLBRUSH, "paintingtool_brush_small");
        subtypes.put(DAMAGE_FILLER, "paintingtool_filler");
        subtypes.put(DAMAGE_COLORPICKER, "paintingtool_colorpicker");
        SUBTYPES = Collections.unmodifiableMap(subtypes);
    }
    
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
    public IIcon iconColorPickerHead;
    @SideOnly(Side.CLIENT)
    public IIcon iconColorPickerBody;
    
    public ItemPaintBrush() {
        this.setRegistryName("paintbrush");
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
    
    public ItemStack getColorPicker() {
        return new ItemStack(this, 1, DAMAGE_COLORPICKER);
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
            ItemStack stack,
            EntityPlayer player,
            List list,
            boolean advancedItemTooltips) {
        super.addInformation(stack, player, list, advancedItemTooltips);
        boolean shift = GuiScreen.isShiftKeyDown();
        boolean palette = Minecraft.getMinecraft().currentScreen instanceof GuiPalette;
        String format =
                palette ? (shift ? Core.instance.painting.clientConfig.paintBrushPaletteShiftTooltipFormat
                                 : Core.instance.painting.clientConfig.paintBrushPaletteTooltipFormat)
                        : (shift ? Core.instance.painting.clientConfig.paintBrushShiftTooltipFormat
                                 : Core.instance.painting.clientConfig.paintBrushTooltipFormat);
        if (!format.isEmpty()) {
            Color color = new Color(this.getColor(stack));
            String formatted = String.format(Locale.ROOT, format,
                                             color.getRed(),
                                             color.getGreen(),
                                             color.getBlue(),
                                             ColorUtils.getAlphaless(color));
            list.addAll(Arrays.asList(formatted.split("\n")));
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
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        String name = SUBTYPES.get(damage);
        if (name == null) {
            return this.getUnlocalizedName() + damage;
        }
        return IMod3Item.getUnlocalizedName(name);
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
            case DAMAGE_COLORPICKER:
                return (pass == 0 ? this.iconColorPickerHead : this.iconColorPickerBody);
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
        // red, green, blue
        IntStream.of(0x993333, 0x667F33, 0x334CB2).mapToObj(color-> {
            ItemStack stack = brush.getBrush();
            this.setColor(stack, color);
            return stack;
        }).forEachOrdered(list::add);
        list.add(brush.getSmallBrush());
        // black
        IntStream.of(0x191919).mapToObj(color-> {
            ItemStack stack = brush.getSmallBrush();
            this.setColor(stack, color);
            return stack;
        }).forEachOrdered(list::add);
        list.add(brush.getFiller());
        list.add(brush.getColorPicker());
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        final String PREFIX = DEF.MOD_ID + ":paintingtool";
        this.iconBrushHead = iconRegister.registerIcon(PREFIX + "_brush_head");
        this.iconBrushBody = iconRegister.registerIcon(PREFIX + "_brush_body");
        this.iconSmallBrushHead = iconRegister.registerIcon(PREFIX + "_brush_small_head");
        this.iconSmallBrushBody = iconRegister.registerIcon(PREFIX + "_brush_small_body");
        this.iconFillerHead = iconRegister.registerIcon(PREFIX + "_filler_head");
        this.iconFillerBody = iconRegister.registerIcon(PREFIX + "_filler_body");
        this.iconColorPickerHead = iconRegister.registerIcon(PREFIX + "_colorpicker_head");
        this.iconColorPickerBody = iconRegister.registerIcon(PREFIX + "_colorpicker_body");
    }
    
    @Override
    public int getColor(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound itemTag = itemStack.getTagCompound();
            if (itemTag.hasKey(TAG_COLOR)) {
                return itemTag.getInteger(TAG_COLOR);
            }
        }
        return ColorUtils.getAlphaless(Core.instance.painting.DEFAULT_COLOR);
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
            case DAMAGE_COLORPICKER:
                return PaintingToolType.COLORPICKER;
            
        }
        return PaintingToolType.NONE;
    }
    
    @Override
    public Color getPaintingToolColor(ItemStack itemStack) {
        return new Color(this.getColor(itemStack));
    }
    
    @Override
    public double getPaintingToolRadius(ItemStack itemStack, IPictureSize picture) {
        Double tagRadius = getTagRadius(itemStack);
        if (tagRadius != null) {
            return tagRadius;
        }
        switch (itemStack.getItemDamage()) {
            case DAMAGE_BRUSH:
                return getRadius(Core.instance.painting.config.brushRadiuses, picture);
            case DAMAGE_SMALLBRUSH:
                return getRadius(Core.instance.painting.config.smallBrushRadiuses, picture);
            default:
                return 0.1D;
        }
    }
}
