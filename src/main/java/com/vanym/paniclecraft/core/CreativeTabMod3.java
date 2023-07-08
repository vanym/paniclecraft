package com.vanym.paniclecraft.core;

import java.util.List;
import java.util.Objects;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.IModComponent;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CreativeTabMod3 extends ItemGroup {
    
    public CreativeTabMod3(String modid) {
        super(modid);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
        return Core.instance.getComponents()
                            .stream()
                            .filter(IModComponent::isEnabled)
                            .map(IModComponent::getItems)
                            .filter(Objects::nonNull)
                            .flatMap(List::stream)
                            .flatMap(item-> {
                                NonNullList<ItemStack> list = NonNullList.create();
                                item.fillItemGroup(this, list);
                                return list.stream();
                            })
                            .findFirst()
                            .orElse(ItemStack.EMPTY);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> stacks) {
        for (IModComponent component : Core.instance.getComponents()) {
            if (!component.isEnabled()) {
                continue;
            }
            List<Item> items = component.getItems();
            if (items == null) {
                continue;
            }
            items.forEach(item->item.fillItemGroup(this, stacks));
        }
    }
}
