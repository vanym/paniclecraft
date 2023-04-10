package com.vanym.paniclecraft.inventory;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryUtils {
    
    public static Stream<ItemStack> inventoryToStream(IInventory inv) {
        return inventoryToStream(inv, false);
    }
    
    public static Stream<ItemStack> inventoryToStream(IInventory inv, boolean onClosing) {
        return IntStream.range(0, inv.getSizeInventory())
                        .mapToObj(onClosing ? inv::removeStackFromSlot : inv::getStackInSlot);
    }
    
    public static ItemStack findItem(InventoryCrafting inv, Item item) {
        return inventoryToStream(inv).filter(s->item == s.getItem())
                                     .findFirst()
                                     .orElse(ItemStack.EMPTY);
    }
    
    @SideOnly(Side.CLIENT)
    public static String getTranslatedName(IWorldNameable inv, Object... params) {
        if (inv.hasCustomName()) {
            return inv.getName();
        } else {
            return I18n.format(inv.getName(), params);
        }
    }
}
