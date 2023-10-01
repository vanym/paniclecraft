package com.vanym.paniclecraft.utils;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {
    
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    
    public static Optional<NBTTagCompound> getTag(ItemStack stack) {
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
        return Optional.ofNullable(stack.getSubCompound(BLOCK_ENTITY_TAG));
    }
    
    public static NBTTagCompound getOrCreateBlockEntityTag(ItemStack stack) {
        return stack.getOrCreateSubCompound(BLOCK_ENTITY_TAG);
    }
    
    public static void cleanBlockEntityTag(ItemStack stack) {
        getTag(stack).filter(t->t.hasKey(BLOCK_ENTITY_TAG, 10))
                     .filter(t->t.getCompoundTag(BLOCK_ENTITY_TAG).hasNoTags())
                     .ifPresent(t->t.removeTag(BLOCK_ENTITY_TAG));
        cleanTag(stack);
    }
}
