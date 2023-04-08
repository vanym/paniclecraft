package com.vanym.paniclecraft.recipe;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

public class RecipeRegister {
    
    public static <T extends IRecipe> T flowRegistryName(T recipe) {
        return flowRegistryName(recipe, "%s");
    }
    
    public static <T extends IRecipe> T flowRegistryName(T recipe, String format) {
        ResourceLocation registry = recipe.getRecipeOutput().getItem().getRegistryName();
        String domain = registry.getResourceDomain();
        String path = registry.getResourcePath();
        ResourceLocation id = new ResourceLocation(domain, String.format(format, path));
        recipe.setRegistryName(id);
        return recipe;
    }
    
    public static String getName(ItemStack stack) {
        String name = stack.getUnlocalizedName();
        if (name.startsWith("item.")) {
            name = name.substring(5);
        }
        return name;
    }
    
    public static <T extends IRecipe> T useName(T recipe) {
        return useName(recipe, "%s");
    }
    
    public static <T extends IRecipe> T useName(T recipe, String format) {
        ItemStack stack = recipe.getRecipeOutput();
        String name = getName(stack);
        String domain = stack.getItem().getRegistryName().getResourceDomain();
        ResourceLocation id = new ResourceLocation(domain, String.format(format, name));
        recipe.setRegistryName(id);
        return recipe;
    }
    
    public static Consumer<IRecipe> namer(String format) {
        return recipe->useName(recipe, format);
    }
    
    // @formatter:off
    public static class ShapedOreRecipe extends net.minecraftforge.oredict.ShapedOreRecipe {
        public ShapedOreRecipe(Item result, Object... recipe) { super(null, result, recipe); }
        public ShapedOreRecipe(Block result, Object... recipe) { super(null, result, recipe); }
        public ShapedOreRecipe(ItemStack result, Object... recipe) { super(null, result, recipe); }
        public ShapedOreRecipe flow() { return flowRegistryName(this); }
        public ShapedOreRecipe name() { return useName(this); }
    }
    public static class ShapelessOreRecipe extends net.minecraftforge.oredict.ShapelessOreRecipe {
        public ShapelessOreRecipe(Item result, Object... recipe) { super(null, result, recipe); }
        public ShapelessOreRecipe(Block result, Object... recipe) { super(null, result, recipe); }
        public ShapelessOreRecipe(ItemStack result, Object... recipe) { super(null, result, recipe); }
        public ShapelessOreRecipe flow() { return flowRegistryName(this); }
        public ShapelessOreRecipe name() { return useName(this); }
    }
    // @formatter:on
}
