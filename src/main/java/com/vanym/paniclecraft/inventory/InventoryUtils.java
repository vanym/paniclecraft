package com.vanym.paniclecraft.inventory;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    
    public static Stream<ItemStack> inventoryToStream(IInventory inv) {
        return inventoryToStream(inv, false);
    }
    
    public static Stream<ItemStack> inventoryToStream(IInventory inv, boolean onClosing) {
        return Stream.iterate(0, i->i + 1)
                     .limit(inv.getSizeInventory())
                     .map(onClosing ? inv::getStackInSlotOnClosing : inv::getStackInSlot);
    }
    
    public static void dropOnClosing(IInventory inv, EntityPlayer player) {
        InventoryUtils.inventoryToStream(inv, true)
                      .filter(s->s != null)
                      .forEach(s->player.dropPlayerItemWithRandomChoice(s, false));
    }
    
    public static ItemStack findItem(InventoryCrafting inv, Item item) {
        try {
            return inventoryToStream(inv).filter(s->s != null && item == s.getItem())
                                         .findFirst()
                                         .get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
