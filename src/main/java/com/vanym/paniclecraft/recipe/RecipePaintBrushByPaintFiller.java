package com.vanym.paniclecraft.recipe;

import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.item.ItemPaintBrush;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipePaintBrushByPaintFiller implements IRecipe {
    
    @Override
    public boolean matches(InventoryCrafting par1InventoryCrafting, World world) {
        return this.matches((IInventory)par1InventoryCrafting, world);
    }
    
    public boolean matches(IInventory par1InventoryCrafting, World world) {
        int filler = 0;
        int brush = 0;
        for(int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(i);
            
            if (itemstack1 != null) {
                if (itemstack1.getItem() instanceof ItemPaintBrush) {
                    if (itemstack1.getItemDamage() == 2) {
                        filler++;
                    } else {
                        brush++;
                    }
                } else {
                    return false;
                }
            }
        }
        return filler == 1 && brush == 1;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
        ItemStack filler = null;
        ItemStack brush = null;
        for(int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(i);
            
            if (itemstack1 != null) {
                if (itemstack1.getItem() instanceof ItemPaintBrush) {
                    if (itemstack1.getItemDamage() == 2) {
                        filler = itemstack1;
                    } else {
                        brush = itemstack1.copy();
                    }
                }
            }
        }
        if (filler != null && brush != null) {
            ModItems.itemPaintBrush.setColor(brush, ModItems.itemPaintBrush.getColor(filler));
            return brush;
        }
        return null;
    }
    
    @Override
    public int getRecipeSize() {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
    
    @SubscribeEvent
    public void itemCraftedEvent(ItemCraftedEvent event) {
        if (this.matches(event.craftMatrix, event.player.worldObj)) {
            for(int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
                ItemStack itemstack1 = event.craftMatrix.getStackInSlot(i);
                if (itemstack1 != null) {
                    if (itemstack1.getItem() instanceof ItemPaintBrush) {
                        if (itemstack1.getItemDamage() == 2) {
                            itemstack1.stackSize++;
                            return;
                        }
                    }
                }
            }
        }
    }
}
