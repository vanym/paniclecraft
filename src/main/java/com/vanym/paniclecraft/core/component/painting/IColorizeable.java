package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.item.ItemStack;

public interface IColorizeable {
    
    public boolean hasCustomColor(ItemStack itemStack);
    
    public int getColor(ItemStack itemStack);
    
    public void clearColor(ItemStack itemStack);
    
    public void setColor(ItemStack itemStack, int color);
}
