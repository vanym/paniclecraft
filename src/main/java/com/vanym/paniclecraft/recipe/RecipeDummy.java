package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDummy {
    
    public static List<IRecipe> getColorizeByDyeDummies() {
        ItemStack brush = Core.instance.painting.itemPaintBrush.getBrush();
        ItemStack smallBrush = Core.instance.painting.itemPaintBrush.getSmallBrush();
        ItemStack filler = Core.instance.painting.itemPaintBrush.getFiller();
        Shapeless brushRecipe = new Shapeless(brush, brush, "dye");
        brushRecipe.setRegistryName(DEF.MOD_ID, "paintBrushByDyeDummy");
        Shapeless smallBrushRecipe = new Shapeless(smallBrush, smallBrush, "dye");
        smallBrushRecipe.setRegistryName(DEF.MOD_ID, "smallPaintBrushByDyeDummy");
        Shapeless fillerRecipe = new Shapeless(filler, filler, "dye");
        fillerRecipe.setRegistryName(DEF.MOD_ID, "paintFillerByDyeDummy");
        return Arrays.asList(brushRecipe, smallBrushRecipe, fillerRecipe);
    }
    
    public static List<IRecipe> getColorizeByFillerDummies() {
        ItemStack brush = Core.instance.painting.itemPaintBrush.getBrush();
        ItemStack smallBrush = Core.instance.painting.itemPaintBrush.getSmallBrush();
        ItemStack filler = Core.instance.painting.itemPaintBrush.getFiller();
        Shapeless brushRecipe = new Shapeless(brush, brush, filler);
        brushRecipe.setRegistryName(DEF.MOD_ID, "paintBrushByFillerDummy");
        Shapeless smallBrushRecipe = new Shapeless(smallBrush, smallBrush, filler);
        smallBrushRecipe.setRegistryName(DEF.MOD_ID, "smallPaintBrushByFillerDummy");
        return Arrays.asList(brushRecipe, smallBrushRecipe);
    }
    
    public static class Shaped extends RecipeRegister.ShapedOreRecipe {
        
        public Shaped(Block result, Object... recipe) {
            super(result, recipe);
        }
        
        public Shaped(Item result, Object... recipe) {
            super(result, recipe);
        }
        
        public Shaped(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
        
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            return false;
        }
        
        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return null;
        }
    }
    
    public static class Shapeless extends RecipeRegister.ShapelessOreRecipe {
        
        public Shapeless(Block result, Object... recipe) {
            super(result, recipe);
        }
        
        public Shapeless(Item result, Object... recipe) {
            super(result, recipe);
        }
        
        public Shapeless(ItemStack result, Object... recipe) {
            super(result, recipe);
        }
        
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            return false;
        }
        
        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return null;
        }
    }
}
