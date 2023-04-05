package com.vanym.paniclecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeRegister {
    
    public static void register(IRecipe recipe) {
        recipe.setRegistryName(recipe.getRecipeOutput().getItem().getRegistryName());
        ForgeRegistries.RECIPES.register(recipe);
    }
    
    // @formatter:off
    public static class ShapedOreRecipe extends net.minecraftforge.oredict.ShapedOreRecipe {
        public ShapedOreRecipe(Item result, Object... recipe) { super(null, result, recipe); }
        public ShapedOreRecipe(Block result, Object... recipe) { super(null, result, recipe); }
        public ShapedOreRecipe(ItemStack result, Object... recipe) { super(null, result, recipe); }
    }
    public static class ShapelessOreRecipe extends net.minecraftforge.oredict.ShapelessOreRecipe {
        public ShapelessOreRecipe(Item result, Object... recipe) { super(null, result, recipe); }
        public ShapelessOreRecipe(Block result, Object... recipe) { super(null, result, recipe); }
        public ShapelessOreRecipe(ItemStack result, Object... recipe) { super(null, result, recipe); }
    }
    // @formatter:on
}
