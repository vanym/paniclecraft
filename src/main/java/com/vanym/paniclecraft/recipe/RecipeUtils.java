package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class RecipeUtils {
    
    public static void addPainting(ItemStack frame, ItemStack painting, EnumFacing pside) {
        NBTTagCompound pictureTag = ItemPainting.getPictureTag(painting)
                                                .map(NBTTagCompound::copy)
                                                .map(NBTTagCompound.class::cast)
                                                .orElseGet(NBTTagCompound::new);
        if (painting.hasDisplayName()) {
            ItemPaintingFrame.putPictureTagName(pictureTag, painting.getDisplayName());
        }
        ItemPaintingFrame.putPictureTag(frame, pside, pictureTag);
    }
}
