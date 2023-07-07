package com.vanym.paniclecraft.recipe;

import com.google.gson.JsonObject;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipePaintingFrame extends ShapedRecipe {
    
    public RecipePaintingFrame(ResourceLocation id,
            String group,
            int recipeWidth,
            int recipeHeight,
            NonNullList<Ingredient> recipeItems) {
        super(id, group, recipeWidth, recipeHeight, recipeItems,
              ItemPaintingFrame.getItemWithEmptyPictures(ItemPaintingFrame.FRONT));
    }
    
    protected RecipePaintingFrame(ShapedRecipe recipe) {
        this(recipe.getId(), recipe.getGroup(),
             recipe.getWidth(), recipe.getHeight(),
             recipe.getIngredients());
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack frame = super.getCraftingResult(inv);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        RecipeUtils.addPainting(frame, painting, ItemPaintingFrame.FRONT);
        return frame;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypePaintingFrame;
    }
    
    public static class Serializer extends ShapedRecipe.Serializer {
        
        @Override
        public RecipePaintingFrame read(ResourceLocation recipeId, JsonObject json) {
            JsonObject stack = new JsonObject();
            String itemId = Core.instance.painting.itemPaintingFrame.getRegistryName().toString();
            stack.addProperty("item", itemId);
            json.add("result", stack);
            ShapedRecipe recipe = super.read(recipeId, json);
            return new RecipePaintingFrame(recipe);
        }
        
        @Override
        public RecipePaintingFrame read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new RecipePaintingFrame(super.read(recipeId, buffer));
        }
    }
}
