package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWorkbench extends ItemMod3 implements IFuelHandler {
    
    public ItemWorkbench(int damage) {
        this.setUnlocalizedName("portable_workbench");
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
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel.getItem() instanceof ItemWorkbench) {
            return 200;
        }
        return 0;
    }
}
