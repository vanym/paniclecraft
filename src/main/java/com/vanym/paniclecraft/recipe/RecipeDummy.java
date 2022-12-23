package com.vanym.paniclecraft.recipe;

import java.util.Arrays;
import java.util.List;

import com.vanym.paniclecraft.Core;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeDummy {
    
    public static List<IRecipe> getColorizeByDyeDummies() {
        ItemStack brush = Core.instance.painting.itemPaintBrush.getBrush();
        ItemStack smallBrush = Core.instance.painting.itemPaintBrush.getSmallBrush();
        ItemStack filler = Core.instance.painting.itemPaintBrush.getFiller();
        return Arrays.asList(new Shapeless(brush, brush, RecipeColorizeByDye.DYE),
                             new Shapeless(smallBrush, smallBrush, RecipeColorizeByDye.DYE),
                             new Shapeless(filler, filler, RecipeColorizeByDye.DYE));
    }
    
    public static List<IRecipe> getColorizeByFillerDummies() {
        ItemStack brush = Core.instance.painting.itemPaintBrush.getBrush();
        ItemStack smallBrush = Core.instance.painting.itemPaintBrush.getSmallBrush();
        ItemStack filler = Core.instance.painting.itemPaintBrush.getFiller();
        return Arrays.asList(new Shapeless(brush, brush, filler),
                             new Shapeless(smallBrush, smallBrush, filler));
    }
    
    public static class Shaped extends ShapedOreRecipe {
        
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
    
    public static class Shapeless extends ShapelessOreRecipe {
        
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
