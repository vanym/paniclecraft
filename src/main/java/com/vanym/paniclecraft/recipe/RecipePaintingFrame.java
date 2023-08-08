package com.vanym.paniclecraft.recipe;

import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class RecipePaintingFrame extends RecipeRegister.ShapedOreRecipe {
    
    protected final EnumFacing side;
    
    public RecipePaintingFrame(Object... recipe) {
        this(ItemPaintingFrame.SideName.FRONT.getSide(), recipe);
    }
    
    protected RecipePaintingFrame(EnumFacing pside, Object... recipe) {
        super(ItemPaintingFrame.getItemWithEmptyPictures(pside), recipe);
        this.side = Objects.requireNonNull(pside);
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        RecipeUtils.addPainting(frame, painting, this.side);
        return frame;
    }
    
    @Override
    public boolean isDynamic() {
        return false; // we want to show this recipe
    }
}
