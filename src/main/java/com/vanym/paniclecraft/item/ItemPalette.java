package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPalette extends ItemMod3 {
    
    public ItemPalette() {
        super();
        this.setMaxStackSize(1);
        this.setUnlocalizedName("palette");
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3) {
        if (!par2World.isRemote) {
            par3.openGui(Core.instance, GUIs.PALETTE.ordinal(), par2World, (int)par3.posX,
                         (int)par3.posY, (int)par3.posZ);
        }
        return par1ItemStack;
    }
}
