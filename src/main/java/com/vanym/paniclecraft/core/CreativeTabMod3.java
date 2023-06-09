package com.vanym.paniclecraft.core;

import java.util.List;
import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.IModComponent;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabMod3 extends CreativeTabs {
    
    public CreativeTabMod3(String tabid) {
        super(tabid);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return "itemgroup." + this.getTabLabel();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return Core.instance.getComponents()
                            .stream()
                            .filter(IModComponent::isEnabled)
                            .map(IModComponent::getItems)
                            .filter(Objects::nonNull)
                            .flatMap(List::stream)
                            .flatMap(item-> {
                                NonNullList<ItemStack> list = NonNullList.create();
                                item.getSubItems(this, list);
                                return list.stream();
                            })
                            .findFirst()
                            .orElse(ItemStack.EMPTY);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> stacks) {
        for (IModComponent component : Core.instance.getComponents()) {
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
