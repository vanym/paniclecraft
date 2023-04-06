package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintBrush extends ItemPaintingTool implements IWithSubtypes, IColorizeable {
    
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
    
    public ItemPaintBrush() {
        super();
        this.setUnlocalizedName("paintbrush");
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
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        if (GuiScreen.isShiftKeyDown()) {
            Color color = new Color(this.getColor(itemStack));
            list.add("R: \u00a7c" + color.getRed());
            list.add("G: \u00a7a" + color.getGreen());
            list.add("B: \u00a79" + color.getBlue());
        }
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        String name = SUBTYPES.get(damage);
        if (name == null) {
            return this.getUnlocalizedName() + damage;
        }
        return "item." + name;
    }
    
    @Override
    public Map<Integer, String> getSubtypes() {
        return SUBTYPES;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs creativetab, NonNullList<ItemStack> list) {
        if (!this.isInCreativeTab(creativetab)) {
            return;
        }
        ItemPaintBrush brush = (ItemPaintBrush)this;
        list.add(brush.getBrush());
        list.add(brush.getSmallBrush());
        list.add(brush.getFiller());
        list.add(brush.getColorPicker());
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
    
    @SideOnly(Side.CLIENT)
    public IItemColor color() {
        return new ItemColor();
    }
    
    @SideOnly(Side.CLIENT)
    protected class ItemColor implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) {
                return ItemPaintBrush.this.getColor(stack);
            } else {
                return -1;
            }
        }
    }
}
