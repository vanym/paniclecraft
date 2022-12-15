package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;

import net.minecraft.item.ItemStack;

public interface IPaintingTool {
    public static enum PaintingToolType {
        NONE,
        BRUSH,
        FILLER,
    }
    
    public PaintingToolType getPaintingToolType(ItemStack itemStack);
    
    public Color getPaintingToolColor(ItemStack itemStack);
    
    public double getPaintingToolRadius(ItemStack itemStack, Picture picture);
}