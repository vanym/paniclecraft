package com.vanym.paniclecraft.recipe;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool.PaintingToolType;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeColorizeByFiller extends ShapelessOreRecipe {
    
    public RecipeColorizeByFiller() {
        super(Core.instance.painting.itemPaintBrush.getBrush(),
              Core.instance.painting.itemPaintBrush.getBrush(),
              Core.instance.painting.itemPaintBrush.getFiller());
    }
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean filler = false;
        boolean colorizeable = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot == null) {
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
            if (slot == null) {
                continue;
            }
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
            colorizeable.setColor(colorizeableStack, MainUtils.getAlphaless(color));
            return colorizeableStack;
        }
        return null;
    }
    
    @SubscribeEvent
    public void itemCraftedEvent(ItemCraftedEvent event) {
        if (event.player == null) {
            return;
        }
        World world = event.player.getEntityWorld();
        if (!(event.craftMatrix instanceof InventoryCrafting)) {
            return;
        }
        InventoryCrafting inv = (InventoryCrafting)event.craftMatrix;
        if (!this.matches(inv, world)) {
            return;
        }
        
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot == null) {
                continue;
            }
            Item item = slot.getItem();
            if (item instanceof IPaintingTool) {
                IPaintingTool tool = (IPaintingTool)item;
                if (tool.getPaintingToolType(slot) == PaintingToolType.FILLER) {
                    ++slot.stackSize;
                    return;
                }
            }
        }
    }
}
