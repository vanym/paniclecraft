package com.vanym.paniclecraft.utils;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {
    
    public static Optional<NBTTagCompound> getTag(ItemStack stack) {
        if (stack == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(stack.getTagCompound());
    }
    
    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
    
    public static void cleanTag(ItemStack stack) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasNoTags()) {
                stack.setTagCompound(null);
            }
        }
    }
    
    public static Optional<NBTTagCompound> getBlockEntityTag(ItemStack stack) {
        return getTag(stack);
    }
    
    public static NBTTagCompound getOrCreateBlockEntityTag(ItemStack stack) {
        return getOrCreateTag(stack);
    }
    
    public static void cleanBlockEntityTag(ItemStack stack) {
        cleanTag(stack);
    }
}
