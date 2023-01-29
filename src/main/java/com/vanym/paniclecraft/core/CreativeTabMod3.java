package com.vanym.paniclecraft.core;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.ModComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

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
    public Item getTabIconItem() {
        return this.iconitem;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void displayAllReleventItems(List stacks) {
        for (ModComponent component : Core.instance.getComponents()) {
            if (!component.isEnabled()) {
                continue;
            }
            List<Item> items = component.getItems();
            if (items == null) {
                continue;
            }
            items.forEach(item->item.getSubItems(item, this, stacks));
        }
    }
}
