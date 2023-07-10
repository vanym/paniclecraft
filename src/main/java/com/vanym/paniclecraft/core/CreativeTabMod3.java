package com.vanym.paniclecraft.core;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.IModComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabMod3 extends CreativeTabs {
    
    public Item iconitem;
    
    public CreativeTabMod3(String tabid) {
        super(tabid);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return "itemgroup." + this.getTabLabel();
    }
    
    @Override
    public Item getTabIconItem() {
        return this.iconitem;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void displayAllReleventItems(List stacks) {
        for (IModComponent component : Core.instance.getComponents()) {
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
