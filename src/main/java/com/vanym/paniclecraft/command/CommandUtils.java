package com.vanym.paniclecraft.command;

import net.minecraft.event.HoverEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class CommandUtils {
    
    public static String makeGiveCommand(String player, ItemStack stack) {
        return String.format("/give %s %s %d %d %s",
                             player, Item.itemRegistry.getNameForObject(stack.getItem()),
                             stack.stackSize, stack.getItemDamage(),
                             !stack.hasTagCompound() ? "" : stack.getTagCompound()
                                                                 .toString())
                     .trim();
    }
    
    public static HoverEvent makeItemHover(ItemStack stack) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new ChatComponentText(stack.writeToNBT(new NBTTagCompound()).toString()));
    }
}
