package com.vanym.paniclecraft.inventory;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    
    public static Stream<ItemStack> stream(IInventory inv) {
        return stream(inv, false);
    }
    
    public static Stream<ItemStack> stream(IInventory inv, boolean onClosing) {
        return IntStream.range(0, inv.getSizeInventory())
                        .mapToObj(onClosing ? inv::removeStackFromSlot : inv::getStackInSlot);
    }
    
    public static ItemStack findItem(CraftingInventory inv, Item item) {
        return stream(inv).filter(s->item == s.getItem())
                          .findFirst()
                          .orElse(ItemStack.EMPTY);
    }
}
