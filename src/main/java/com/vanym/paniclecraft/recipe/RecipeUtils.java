package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class RecipeUtils {
    
    public static void addPainting(ItemStack frame, ItemStack painting, Direction pside) {
        CompoundNBT inputPictureTag = painting.getChildTag(ItemPainting.TAG_PICTURE);
        CompoundNBT pictureTag;
        if (inputPictureTag != null) {
            pictureTag = inputPictureTag.copy();
        } else {
            pictureTag = new CompoundNBT();
        }
        if (painting.hasDisplayName()) {
            pictureTag.putString(Picture.TAG_NAME, painting.getDisplayName().getFormattedText());
        }
        CompoundNBT outputItemTag = frame.getOrCreateTag();
        final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
        outputItemTag.put(TAG_PICTURE_I, pictureTag);
    }
}
