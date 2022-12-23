package com.vanym.paniclecraft.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;

import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeColorizeByDye implements IRecipe {
    
    public static final String DYE = "dye";
    protected static final String[] DYES_SUFFIX = // from OreDictionary
            {"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray",
             "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"};
    
    protected final List<List<ItemStack>> dyesByColor;
    
    public RecipeColorizeByDye() {
        ArrayList<List<ItemStack>> list = new ArrayList<>();
        for (String suffix : DYES_SUFFIX) {
            List<ItemStack> dyeColor = OreDictionary.getOres(DYE + suffix);
            list.add(dyeColor);
        }
        this.dyesByColor = Collections.unmodifiableList(list);
    }
    
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
            } else if (this.isAnyDye(slot)) {
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
                itemstack.stackSize = 1;
                
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
                int index = this.getDyeIndex(slot);
                if (index < 0) {
                    return null;
                }
                
                float[] afloat = EntitySheep.fleeceColorTable[BlockColored.func_150032_b(index)];
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
    
    protected int getDyeIndex(ItemStack slot) {
        int i = 0;
        for (List<ItemStack> color : this.dyesByColor) {
            if (listMatches(slot, color)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    protected boolean isAnyDye(ItemStack slot) {
        return this.getDyeIndex(slot) >= 0;
    }
    
    protected static boolean listMatches(ItemStack slot, List<ItemStack> list) {
        // NOTE: OreDictionary.UnmodifiableArrayList (1.7.10-10.13.4.1614) does not support stream()
        for (ItemStack itemStack : list) {
            if (OreDictionary.itemMatches(itemStack, slot, false)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getRecipeSize() {
        return 2;
    }
    
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
