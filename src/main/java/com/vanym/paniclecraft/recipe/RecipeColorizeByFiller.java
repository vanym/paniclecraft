package com.vanym.paniclecraft.recipe;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RecipeColorizeByFiller extends SpecialRecipe {
    
    public RecipeColorizeByFiller(ResourceLocation id) {
        super(id);
    }
    
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        boolean filler = false;
        boolean colorizeable = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot.isEmpty()) {
                continue;
            }
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack fillerStack = ItemStack.EMPTY;
        ItemStack colorizeableStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            Item item = slot.getItem();
            if (item instanceof IPaintingTool) {
                IPaintingTool tool = (IPaintingTool)item;
                if (tool.getPaintingToolType(slot) == PaintingToolType.FILLER) {
                    if (!fillerStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    fillerStack = slot;
                    continue;
                }
            }
            if (item instanceof IColorizeable) {
                if (!colorizeableStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                colorizeableStack = slot.copy();
            }
        }
        if (!fillerStack.isEmpty() && !colorizeableStack.isEmpty()) {
            Item fillerItem = fillerStack.getItem();
            Item colorizeableItem = colorizeableStack.getItem();
            IPaintingTool tool = (IPaintingTool)fillerItem;
            IColorizeable colorizeable = (IColorizeable)colorizeableItem;
            Color color = tool.getPaintingToolColor(fillerStack);
            colorizeable.setColor(colorizeableStack, ColorUtils.getAlphaless(color));
            return colorizeableStack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
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
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Core.instance.painting.recipeTypeColorizeByFiller;
    }
}
