package com.vanym.paniclecraft.recipe;

import java.util.Optional;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeColorizeByDye extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int items = 0;
        int dyes = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot == null) {
                continue;
            }
            if (slot.getItem() instanceof IColorizeable) {
                ++items;
            } else if (DyeUtils.isDye(slot)) {
                ++dyes;
            }
        }
        return items == 1 && dyes >= 1;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = null;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        IColorizeable itemcolored = null;
        int k;
        int l;
        float f;
        float f1;
        int l1;
        
        for (k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack slot = inv.getStackInSlot(k);
            
            if (slot == null) {
                continue;
            }
            
            if (slot.getItem() instanceof IColorizeable) {
                itemcolored = (IColorizeable)slot.getItem();
                
                if (itemstack != null) {
                    return null;
                }
                
                itemstack = slot.copy();
                itemstack.setCount(1);
                
                if (itemcolored.hasCustomColor(slot)) {
                    l = itemcolored.getColor(itemstack);
                    f = (float)(l >> 16 & 255) / 255.0F;
                    f1 = (float)(l >> 8 & 255) / 255.0F;
                    float f2 = (float)(l & 255) / 255.0F;
                    i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                    aint[0] = (int)((float)aint[0] + f * 255.0F);
                    aint[1] = (int)((float)aint[1] + f1 * 255.0F);
                    aint[2] = (int)((float)aint[2] + f2 * 255.0F);
                    ++j;
                }
            } else {
                Optional<EnumDyeColor> color = DyeUtils.colorFromStack(slot);
                if (!color.isPresent()) {
                    return ItemStack.EMPTY;
                }
                float[] afloat = color.get().getColorComponentValues();
                int j1 = (int)(afloat[0] * 255.0F);
                int k1 = (int)(afloat[1] * 255.0F);
                l1 = (int)(afloat[2] * 255.0F);
                i += Math.max(j1, Math.max(k1, l1));
                aint[0] += j1;
                aint[1] += k1;
                aint[2] += l1;
                ++j;
            }
        }
        
        if (itemcolored == null) {
            return null;
        } else {
            k = aint[0] / j;
            int i1 = aint[1] / j;
            l = aint[2] / j;
            f = (float)i / (float)j;
            f1 = (float)Math.max(k, Math.max(i1, l));
            k = (int)((float)k * f / f1);
            i1 = (int)((float)i1 * f / f1);
            l = (int)((float)l * f / f1);
            l1 = (k << 8) + i1;
            l1 = (l1 << 8) + l;
            itemcolored.setColor(itemstack, l1);
            return itemstack;
        }
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
