package com.vanym.paniclecraft.inventory;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
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
        return IntStream.range(0, inv.getSizeInventory())
                        .mapToObj(onClosing ? inv::getStackInSlotOnClosing : inv::getStackInSlot);
    }
    
    public static void dropOnClosing(IInventory inv, EntityPlayer player) {
        InventoryUtils.inventoryToStream(inv, true)
                      .filter(s->s != null)
                      .forEach(s->player.dropPlayerItemWithRandomChoice(s, false));
    }
    
    public static ItemStack findItem(InventoryCrafting inv, Item item) {
        return inventoryToStream(inv).filter(s->s != null && item == s.getItem())
                                     .findFirst()
                                     .orElse(null);
    }
    
    @SideOnly(Side.CLIENT)
    public static String getTranslatedName(IInventory inv, Object... params) {
        if (inv.hasCustomInventoryName()) {
            return inv.getInventoryName();
        } else {
            return I18n.format(inv.getInventoryName(), params);
        }
    }
}
