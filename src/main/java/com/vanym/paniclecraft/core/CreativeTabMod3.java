package com.vanym.paniclecraft.core;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.ModComponent;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabMod3 extends CreativeTabs {
    
    public Item iconitem;
    
    public CreativeTabMod3(String modid) {
        super(modid);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return DEF.MOD_NAME;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(this.iconitem);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> stacks) {
        for (ModComponent component : Core.instance.getComponents()) {
            if (!component.isEnabled()) {
                continue;
            }
            List<Item> items = component.getItems();
            if (items == null) {
                continue;
            }
            items.forEach(item->item.getSubItems(this, stacks));
        }
    }
}
