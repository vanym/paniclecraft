package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipePaintingFrame extends ShapedOreRecipe {
    
    public RecipePaintingFrame(Object... recipe) {
        super(ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.FRONT), recipe);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        RecipeUtils.addPainting(frame, painting, ItemPaintingFrame.FRONT);
        return frame;
    }
}
