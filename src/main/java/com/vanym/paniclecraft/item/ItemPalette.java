package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPalette extends ItemMod3 {
    
    public ItemPalette() {
        this.setUnlocalizedName("palette");
        this.setMaxStackSize(1);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.openGui(Core.instance, GUIs.PALETTE.ordinal(), world,
                           (int)player.posX, (int)player.posY, (int)player.posZ);
        }
        return stack;
    }
    
    public static boolean canBePalette(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemPalette && stack.stackSize > 0;
    }
}
