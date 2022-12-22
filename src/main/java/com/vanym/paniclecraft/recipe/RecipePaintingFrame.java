package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipePaintingFrame extends ShapedOreRecipe {
    
    public RecipePaintingFrame(Object... recipe) {
        super(Core.instance.painting.blockPaintingFrame.getItemWithEmptyFrontPicture(), recipe);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack painting = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() == Core.instance.painting.itemPainting) {
                painting = itemStack;
                break;
            }
        }
        if (painting != null && painting.hasTagCompound() && frame.hasTagCompound()) {
            NBTTagCompound inputItemTag = painting.getTagCompound();
            NBTTagCompound pictureTag = inputItemTag.getCompoundTag(ItemPainting.TAG_PICTURE);
            NBTTagCompound outputItemTag = frame.getTagCompound();
            final String TAG_PICTURE_I = String.format(BlockPaintingFrame.TAG_PICTURE_N,
                                                       BlockPaintingFrame.FRONT_SIDE.ordinal());
            outputItemTag.setTag(TAG_PICTURE_I, pictureTag);
        }
        return frame;
    }
}
