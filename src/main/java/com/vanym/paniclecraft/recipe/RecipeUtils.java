package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class RecipeUtils {
    
    public static ItemStack findItem(InventoryCrafting inv, Block block) {
        return findItem(inv, Item.getItemFromBlock(block));
    }
    
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
        if (painting == null || !painting.hasTagCompound() || !frame.hasTagCompound()) {
            return;
        }
        NBTTagCompound inputItemTag = painting.getTagCompound();
        if (!inputItemTag.hasKey(ItemPainting.TAG_PICTURE)) {
            return;
        }
        NBTTagCompound pictureTag = inputItemTag.getCompoundTag(ItemPainting.TAG_PICTURE);
        NBTTagCompound outputItemTag = frame.getTagCompound();
        final String TAG_PICTURE_I = BlockPaintingFrame.getPictureTag(pside);
        outputItemTag.setTag(TAG_PICTURE_I, pictureTag.copy());
    }
}
