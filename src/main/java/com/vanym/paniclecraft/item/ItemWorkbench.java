package com.vanym.paniclecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;

public class ItemWorkbench extends ItemMod3{
	public ItemWorkbench(int damage){
		this.setMaxStackSize(1);
		this.setMaxDamage(damage);
		this.setUnlocalizedName("portableWorkbench");
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		if(!par2World.isRemote)
			par3.openGui(Core.instance, GUIs.PORTABLEWORKBENCH.ordinal(), par2World, (int)par3.posX, (int)par3.posY, (int)par3.posZ);
		return par1ItemStack;
	}
}
