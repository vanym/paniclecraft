package com.vanym.paniclecraft.core.component.painting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IColorizeable {
    
    public boolean hasCustomColor(ItemStack itemStack);
    
    public int getColor(ItemStack itemStack);
    
    public void clearColor(ItemStack itemStack);
    
    public void setColor(ItemStack itemStack, int color);
    
    public static IColorizeable getColorizeable(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        Item item = stack.getItem();
        if (!(item instanceof IColorizeable)) {
            return null;
        }
        return (IColorizeable)item;
    }
}
