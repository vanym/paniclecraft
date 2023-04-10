package com.vanym.paniclecraft.recipe;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;

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
        return Stream.of(brush, smallBrush, filler)
                     .map(item->new Shapeless(item, item, "dye"))
                     .peek(RecipeRegister.namer("%s_colorize_by_dye_dummy"))
                     .collect(Collectors.toList());
    }
    
    public static List<IRecipe> getColorizeByFillerDummies() {
        ItemStack brush = Core.instance.painting.itemPaintBrush.getBrush();
        ItemStack smallBrush = Core.instance.painting.itemPaintBrush.getSmallBrush();
        ItemStack filler = Core.instance.painting.itemPaintBrush.getFiller();
        return Stream.of(brush, smallBrush)
                     .map(item->new Shapeless(item, item, filler))
                     .peek(RecipeRegister.namer("%s_colorize_by_filler_dummy"))
                     .collect(Collectors.toList());
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
            return ItemStack.EMPTY;
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
            return ItemStack.EMPTY;
        }
    }
}
