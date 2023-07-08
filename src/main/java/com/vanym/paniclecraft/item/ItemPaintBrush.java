package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPaintBrush extends ItemPaintingTool implements IColorizeable {
    
    public static final String TAG_COLOR = "Color";
    
    public static enum Type {
        BRUSH("paintingtool_brush"),
        SMALLBRUSH("paintingtool_brush_small"),
        FILLER("paintingtool_filler"),
        COLORPICKER("paintingtool_colorpicker");
        
        public final String id;
        
        Type(String id) {
            this.id = id;
        }
    }
    
    protected final Type type;
    
    public ItemPaintBrush(Type type) {
        super(new Item.Properties().maxStackSize(1));
        this.type = type;
        this.setRegistryName(type.id);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        if (Screen.hasShiftDown()) {
            Color color = new Color(this.getColor(itemStack));
            list.add(new StringTextComponent("R: ").appendSibling(new StringTextComponent(
                    Integer.toString(color.getRed())).applyTextStyle(TextFormatting.RED)));
            list.add(new StringTextComponent("G: ").appendSibling(new StringTextComponent(
                    Integer.toString(color.getGreen())).applyTextStyle(TextFormatting.GREEN)));
            list.add(new StringTextComponent("B: ").appendSibling(new StringTextComponent(
                    Integer.toString(color.getBlue())).applyTextStyle(TextFormatting.BLUE)));
        }
    }
    
    @Override
    public int getColor(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            CompoundNBT itemTag = itemStack.getTag();
            if (itemTag.contains(TAG_COLOR)) {
                return itemTag.getInt(TAG_COLOR);
            }
        }
        return ColorUtils.getAlphaless(Core.instance.painting.DEFAULT_COLOR);
    }
    
    @Override
    public void clearColor(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            CompoundNBT itemTag = itemStack.getTag();
            if (itemTag.contains(TAG_COLOR)) {
                itemTag.remove(TAG_COLOR);
            }
        }
    }
    
    @Override
    public boolean hasCustomColor(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains(TAG_COLOR);
    }
    
    @Override
    public void setColor(ItemStack itemStack, int color) {
        CompoundNBT itemTag = itemStack.getOrCreateTag();
        itemTag.putInt(TAG_COLOR, color);
    }
    
    @Override
    public PaintingToolType getPaintingToolType(ItemStack itemStack) {
        switch (this.type) {
            case BRUSH:
            case SMALLBRUSH:
                return PaintingToolType.BRUSH;
            case FILLER:
                return PaintingToolType.FILLER;
            case COLORPICKER:
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
        switch (this.type) {
            case BRUSH:
                return getRadius(Core.instance.painting.config.brushRadiuses, picture);
            case SMALLBRUSH:
                return getRadius(Core.instance.painting.config.smallBrushRadiuses, picture);
            default:
                return 0.1D;
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public IItemColor color() {
        return new ItemColor();
    }
    
    @OnlyIn(Dist.CLIENT)
    protected class ItemColor implements IItemColor {
        @Override
        public int getColor(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) {
                return ItemPaintBrush.this.getColor(stack);
            } else {
                return -1;
            }
        }
    }
}
