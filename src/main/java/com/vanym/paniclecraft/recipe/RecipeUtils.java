package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class RecipeUtils {
    
    public static ItemStack findItem(InventoryCrafting inv, Item item) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() == item) {
                return itemStack;
            }
        }
        return null;
    }
    
    public static void addPainting(ItemStack frame, ItemStack painting, ForgeDirection pside) {
        if (!frame.hasTagCompound()) {
            frame.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound inputItemTag = painting.getTagCompound();
        NBTBase pictureTag;
        if (painting.hasTagCompound() && inputItemTag.hasKey(ItemPainting.TAG_PICTURE)) {
            NBTTagCompound inputPictureTag = inputItemTag.getCompoundTag(ItemPainting.TAG_PICTURE);
            pictureTag = inputPictureTag.copy();
        } else {
            pictureTag = new NBTTagCompound();
        }
        NBTTagCompound outputItemTag = frame.getTagCompound();
        final String TAG_PICTURE_I = ItemPaintingFrame.getPictureTag(pside);
        outputItemTag.setTag(TAG_PICTURE_I, pictureTag);
    }
}
