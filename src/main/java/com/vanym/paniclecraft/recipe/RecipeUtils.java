package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class RecipeUtils {
    
    public static void addPainting(ItemStack frame, ItemStack painting, Direction pside) {
        CompoundNBT pictureTag = ItemPainting.getPictureTag(painting)
                                             .map(CompoundNBT::copy)
                                             .orElseGet(CompoundNBT::new);
        if (painting.hasDisplayName()) {
            ItemPaintingFrame.putPictureTagName(pictureTag,
                                                painting.getDisplayName().getFormattedText());
        }
        ItemPaintingFrame.putPictureTag(frame, pside, pictureTag);
    }
}
