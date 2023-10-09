package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ItemMod3 extends Item {
    
    public ItemMod3() {
        this.setCreativeTab(Core.instance.tab);
    }
    
    @Override
    public String getUnlocalizedName() {
        return getUnlocalizedName(this.getRegistryName());
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public String getItemStackDisplayName(ItemStack stack) {
        return net.minecraft.util.text.translation.//
                I18n.translateToLocal(this.getUnlocalizedName(stack)).trim();
    }
    
    public static String getUnlocalizedName(ResourceLocation id) {
        return "item." + id.getResourceDomain() + "." + id.getResourcePath();
    }
    
    public static String getUnlocalizedName(String name) {
        return "item." + DEF.MOD_ID + "." + name;
    }
}
