package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class RecipePaintingFrame extends RecipeRegister.ShapedOreRecipe {
    
    public RecipePaintingFrame(Object... recipe) {
        super(ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.SideName.FRONT.getSide()),
              recipe);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        RecipeUtils.addPainting(frame, painting, ItemPaintingFrame.SideName.FRONT.getSide());
        return frame;
    }
    
    @Override
    public boolean isDynamic() {
        return false; // we want to show this recipe
    }
}
