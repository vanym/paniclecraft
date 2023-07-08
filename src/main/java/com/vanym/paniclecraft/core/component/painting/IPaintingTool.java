package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;

import net.minecraft.item.ItemStack;

public interface IPaintingTool {
    public static enum PaintingToolType {
        NONE(false),
        BRUSH(true),
        FILLER(false),
        COLORPICKER(true),
        REMOVER(true);
        
        protected boolean pixelSelector;
        
        PaintingToolType(boolean pixelSelector) {
            this.pixelSelector = pixelSelector;
        }
        
        public boolean isPixelSelector() {
            return this.pixelSelector;
        }
    }
    
    public PaintingToolType getPaintingToolType(ItemStack itemStack);
    
    public Color getPaintingToolColor(ItemStack itemStack);
    
    public double getPaintingToolRadius(ItemStack itemStack, IPictureSize picture);
}
