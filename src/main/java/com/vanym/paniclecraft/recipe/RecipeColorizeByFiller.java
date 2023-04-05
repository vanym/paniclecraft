package com.vanym.paniclecraft.recipe;

import java.awt.Color;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeColorizeByFiller extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean filler = false;
        boolean colorizeable = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            Item item = slot.getItem();
            if (item instanceof IPaintingTool) {
                IPaintingTool tool = (IPaintingTool)item;
                if (tool.getPaintingToolType(slot) == PaintingToolType.FILLER) {
                    if (filler) {
                        return false;
                    }
                    filler = true;
                    continue;
                }
            }
            if (item instanceof IColorizeable) {
                if (colorizeable) {
                    return false;
                }
                colorizeable = true;
            } else {
                return false;
            }
        }
        return filler && colorizeable;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack fillerStack = null;
        ItemStack colorizeableStack = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            Item item = slot.getItem();
            if (item instanceof IPaintingTool) {
                IPaintingTool tool = (IPaintingTool)item;
                if (tool.getPaintingToolType(slot) == PaintingToolType.FILLER) {
                    if (fillerStack != null) {
                        return null;
                    }
                    fillerStack = slot;
                    continue;
                }
            }
            if (item instanceof IColorizeable) {
                if (colorizeableStack != null) {
                    return null;
                }
                colorizeableStack = slot.copy();
            }
        }
        if (fillerStack != null && colorizeableStack != null) {
            Item fillerItem = fillerStack.getItem();
            Item colorizeableItem = colorizeableStack.getItem();
            IPaintingTool tool = (IPaintingTool)fillerItem;
            IColorizeable colorizeable = (IColorizeable)colorizeableItem;
            Color color = tool.getPaintingToolColor(fillerStack);
            colorizeable.setColor(colorizeableStack, ColorUtils.getAlphaless(color));
            return colorizeableStack;
        }
        return null;
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> list =
                NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            Item item = slot.getItem();
            if (item instanceof IPaintingTool) {
                IPaintingTool tool = (IPaintingTool)item;
                if (tool.getPaintingToolType(slot) == PaintingToolType.FILLER) {
                    ItemStack stack = slot.copy();
                    stack.setCount(1);
                    list.set(i, stack);
                    continue;
                }
            }
            list.set(i, ForgeHooks.getContainerItem(slot));
        }
        return list;
    }
    
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean isDynamic() {
        return true;
    }
    
    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
