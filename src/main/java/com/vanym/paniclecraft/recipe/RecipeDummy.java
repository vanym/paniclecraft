package com.vanym.paniclecraft.recipe;

import com.google.gson.JsonObject;
import com.vanym.paniclecraft.DEF;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeDummy {
    
    public static final DeferredRegister<IRecipeSerializer<?>> REGISTER;
    
    protected static final IRecipeSerializer<?> DUMMY_SHAPED_SERIALIZER =
            new ShapedRecipe.Serializer();
    
    protected static final IRecipeSerializer<?> DUMMY_SHAPELESS_SERIALIZER =
            new ShapelessRecipe.Serializer();
    
    static {
        REGISTER = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, DEF.MOD_ID);
        REGISTER.register("dummy_shaped", ()->DUMMY_SHAPED_SERIALIZER);
        REGISTER.register("dummy_shapeless", ()->DUMMY_SHAPELESS_SERIALIZER);
    }
    
    public static class Shaped extends ShapedRecipe {
        
        public Shaped(ResourceLocation id,
                String group,
                int recipeWidth,
                int recipeHeight,
                NonNullList<Ingredient> recipeItems,
                ItemStack recipeOutput) {
            super(id, group, recipeWidth, recipeHeight, recipeItems, recipeOutput);
        }
        
        protected Shaped(ShapedRecipe recipe) {
            this(recipe.getId(), recipe.getGroup(),
                 recipe.getWidth(), recipe.getHeight(),
                 recipe.getIngredients(), recipe.getRecipeOutput());
        }
        
        @Override
        public boolean matches(CraftingInventory inv, World world) {
            return false;
        }
        
        @Override
        public ItemStack getCraftingResult(CraftingInventory inv) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return DUMMY_SHAPED_SERIALIZER;
        }
        
        public static class Serializer extends ShapedRecipe.Serializer {
            
            @Override
            public Shaped read(ResourceLocation recipeId, JsonObject json) {
                return new Shaped(super.read(recipeId, json));
            }
            
            @Override
            public Shaped read(ResourceLocation recipeId, PacketBuffer buf) {
                return new Shaped(super.read(recipeId, buf));
            }
        }
    }
    
    public static class Shapeless extends ShapelessRecipe {
        
        public Shapeless(ResourceLocation id,
                String group,
                ItemStack recipeOutput,
                NonNullList<Ingredient> recipeItems) {
            super(id, group, recipeOutput, recipeItems);
        }
        
        protected Shapeless(ShapelessRecipe recipe) {
            this(recipe.getId(), recipe.getGroup(),
                 recipe.getRecipeOutput(), recipe.getIngredients());
        }
        
        @Override
        public boolean matches(CraftingInventory inv, World world) {
            return false;
        }
        
        @Override
        public ItemStack getCraftingResult(CraftingInventory inv) {
            return ItemStack.EMPTY;
        }
        
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return DUMMY_SHAPELESS_SERIALIZER;
        }
        
        public static class Serializer extends ShapelessRecipe.Serializer {
            
            @Override
            public Shapeless read(ResourceLocation recipeId, JsonObject json) {
                return new Shapeless(super.read(recipeId, json));
            }
            
            @Override
            public Shapeless read(ResourceLocation recipeId, PacketBuffer buf) {
                return new Shapeless(super.read(recipeId, buf));
            }
        }
    }
}
