package com.vanym.paniclecraft.recipe;

import java.util.Objects;

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
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class RecipePaintingFrame extends ShapedRecipe {
    
    protected final Direction side;
    
    public RecipePaintingFrame(ResourceLocation id,
            String group,
            int recipeWidth,
            int recipeHeight,
            NonNullList<Ingredient> recipeItems,
            Direction pside) {
        super(id, group, recipeWidth, recipeHeight, recipeItems,
              ItemPaintingFrame.getItemWithEmptyPictures(pside));
        this.side = Objects.requireNonNull(pside);
    }
    
    protected RecipePaintingFrame(Direction pside, ShapedRecipe recipe) {
        this(recipe.getId(), recipe.getGroup(),
             recipe.getWidth(), recipe.getHeight(),
             recipe.getIngredients(), pside);
    }
    
    @Override
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack frame = super.assemble(inv);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        RecipeUtils.addPainting(frame, painting, this.side);
        return frame;
    }
    
    @Override
    public boolean isSpecial() {
        return false; // we want to show this recipe
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypePaintingFrame;
    }
    
    public static class Serializer extends ShapedRecipe.Serializer {
        
        @Override
        public RecipePaintingFrame fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject stack = new JsonObject();
            String itemId = Core.instance.painting.itemPaintingFrame.getRegistryName().toString();
            stack.addProperty("item", itemId);
            json.add("result", stack);
            Direction side = RecipeUtils.getSide(json, "side");
            ShapedRecipe recipe = super.fromJson(recipeId, json);
            return new RecipePaintingFrame(side, recipe);
        }
        
        @Override
        public RecipePaintingFrame fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            Direction side = Direction.from3DDataValue(buffer.readVarInt());
            ShapedRecipe recipe = super.fromNetwork(recipeId, buffer);
            return new RecipePaintingFrame(side, recipe);
        }
        
        @Override
        public void toNetwork(PacketBuffer buf, ShapedRecipe recipeUncasted) {
            final RecipePaintingFrame recipe = (RecipePaintingFrame)recipeUncasted;
            buf.writeVarInt(recipe.side.get3DDataValue());
            super.toNetwork(buf, recipe);
        }
    }
}
