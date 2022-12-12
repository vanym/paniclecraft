package com.vanym.paniclecraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.DEF;

public abstract class ItemMod3 extends Item{
	public ItemMod3(){
		super();
	}
	
	@Override
	public String getUnlocalizedName(){
		// return String.format("item.%s%s", DEF.MOD_ID.toLowerCase() + ":",
		// getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
		return String.format("item.%s", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack){
		return getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		itemIcon = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}
	
	protected String getUnwrappedUnlocalizedName(String unlocalizedName){
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
