package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWorkbench extends ItemMod3 {
    
    public ItemWorkbench(int damage) {
        this.setUnlocalizedName("portableWorkbench");
        this.setMaxDamage(damage);
        this.setMaxStackSize(1);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.openGui(Core.instance, GUIs.PORTABLEWORKBENCH.ordinal(), world,
                           (int)player.posX, (int)player.posY, (int)player.posZ);
        }
        return stack;
    }
    
    public static boolean canBeWorkbench(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWorkbench && stack.stackSize > 0;
    }
}
