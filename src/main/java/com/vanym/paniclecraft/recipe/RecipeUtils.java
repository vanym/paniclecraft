package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class RecipeUtils {
    
    public static void addPainting(ItemStack frame, ItemStack painting, ForgeDirection pside) {
        if (!frame.hasTagCompound()) {
            frame.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound inputItemTag = painting.getTagCompound();
        NBTTagCompound pictureTag;
        if (painting.hasTagCompound() && inputItemTag.hasKey(ItemPainting.TAG_PICTURE)) {
            NBTTagCompound inputPictureTag = inputItemTag.getCompoundTag(ItemPainting.TAG_PICTURE);
            pictureTag = (NBTTagCompound)inputPictureTag.copy();
        } else {
            pictureTag = new NBTTagCompound();
        }
        if (painting.hasDisplayName()) {
            pictureTag.setString(Picture.TAG_NAME, painting.getDisplayName());
        }
        NBTTagCompound outputItemTag = frame.getTagCompound();
        final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
        outputItemTag.setTag(TAG_PICTURE_I, pictureTag);
    }
}
