package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWorkbench extends ItemMod3 {
    
    public ItemWorkbench(int damage) {
        this.setUnlocalizedName("portable_workbench");
        this.setMaxDamage(damage);
        this.setMaxStackSize(1);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            EntityPlayer player,
            EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(Core.instance, GUIs.PORTABLEWORKBENCH.ordinal(), world,
                           (int)player.posX, (int)player.posY, (int)player.posZ);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    public static boolean canBeWorkbench(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWorkbench && !stack.isEmpty();
    }
}
