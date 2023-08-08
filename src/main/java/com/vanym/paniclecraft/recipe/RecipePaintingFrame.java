package com.vanym.paniclecraft.recipe;

import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipePaintingFrame extends ShapedOreRecipe {
    
    protected final ForgeDirection side;
    
    public RecipePaintingFrame(Object... recipe) {
        this(ItemPaintingFrame.SideName.FRONT.getSide(), recipe);
    }
    
    protected RecipePaintingFrame(ForgeDirection pside, Object... recipe) {
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
}
