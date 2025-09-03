package com.vanym.paniclecraft.recipe;

import java.util.stream.IntStream;

import com.google.gson.JsonObject;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.MatrixUtils;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPaintingFrame;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipePaintingFrameAddPainting extends ShapedRecipe {
    
    protected final Direction side;
    
    public RecipePaintingFrameAddPainting(ResourceLocation id,
            Direction side,
            int offsetX,
            int offsetY) {
        this(getRecipe(id, offsetX, offsetY), side);
    }
    
    protected RecipePaintingFrameAddPainting(ShapedRecipe recipe, Direction side) {
        this(recipe.getId(), recipe.getGroup(),
             recipe.getWidth(), recipe.getHeight(),
             recipe.getIngredients(), side);
    }
    
    protected RecipePaintingFrameAddPainting(ResourceLocation id,
            String group,
            int recipeWidth,
            int recipeHeight,
            NonNullList<Ingredient> recipeItems,
            Direction side) {
        super(id, group, recipeWidth, recipeHeight, recipeItems,
              ItemPaintingFrame.getItemWithEmptyPictures(side));
        this.side = side;
    }
    
    @Override
    protected boolean matches(CraftingInventory inv, int x, int y, boolean mirror) {
        if (mirror) {
            return false;
        }
        return super.matches(inv, x, y, mirror);
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }
        ItemStack frame = InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        return !ItemPaintingFrame.getPictureTag(frame, this.side).isPresent();
    }
    
    @Override
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack frame = super.assemble(inv);
        ItemStack inputFrame =
                InventoryUtils.findItem(inv, Core.instance.painting.itemPaintingFrame);
        ItemStack painting = InventoryUtils.findItem(inv, Core.instance.painting.itemPainting);
        if (inputFrame.hasTag()) {
            frame.setTag(inputFrame.getTag().copy());
        }
        RecipeUtils.addPainting(frame, painting, this.side);
        return frame;
    }
    
    @Override
    public boolean isSpecial() {
        return false; // we want to show this recipe
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypePaintingFrameAdd;
    }
    
    protected static ShapedRecipe getRecipe(ResourceLocation id, int offsetX, int offsetY) {
        int sizeX = Math.abs(offsetX) + 1;
        int sizeY = Math.abs(offsetY) + 1;
        byte[] input = new byte[sizeX * sizeY];
        input[input.length - 1] = 'p';
        input[0] = 'f';
        if (offsetX < 0) {
            MatrixUtils.flipH(input, sizeX, 1);
        }
        if (offsetY < 0) {
            MatrixUtils.flipV(input, sizeX, 1);
        }
        final ItemStack p = new ItemStack(Core.instance.painting.itemPainting);
        final ItemStack f = new ItemStack(Core.instance.painting.itemPaintingFrame);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        IntStream.range(0, input.length)
                 .mapToObj(i->input[i])
                 .map(b->b == 'p' ? p : b == 'f' ? f : ItemStack.EMPTY)
                 .map(Ingredient::of)
                 .forEachOrdered(ingredients::add);
        return new ShapedRecipe(id, "", sizeX, sizeY, ingredients, f);
    }
    
    public static class Serializer extends ShapedRecipe.Serializer {
        
        @Override
        public RecipePaintingFrameAddPainting fromJson(ResourceLocation recipeId, JsonObject json) {
            Direction side = RecipeUtils.getSide(json, "side");
            int offsetX = JSONUtils.getAsInt(json, "offsetX");
            int offsetY = JSONUtils.getAsInt(json, "offsetY");
            return new RecipePaintingFrameAddPainting(recipeId, side, offsetX, offsetY);
        }
        
        @Override
        public RecipePaintingFrameAddPainting fromNetwork(ResourceLocation recipeId, PacketBuffer buf) {
            Direction side = Direction.from3DDataValue(buf.readVarInt());
            ShapedRecipe recipe = super.fromNetwork(recipeId, buf);
            return new RecipePaintingFrameAddPainting(recipe, side);
        }
        
        @Override
        public void toNetwork(PacketBuffer buf, ShapedRecipe recipeUncasted) {
            final RecipePaintingFrameAddPainting recipe =
                    (RecipePaintingFrameAddPainting)recipeUncasted;
            buf.writeVarInt(recipe.side.get3DDataValue());
            super.toNetwork(buf, recipe);
        }
    }
}
