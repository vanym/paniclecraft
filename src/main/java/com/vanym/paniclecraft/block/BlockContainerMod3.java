package com.vanym.paniclecraft.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.init.ModItems;

public abstract class BlockContainerMod3 extends BlockContainer{
	
	public BlockContainerMod3(Material material){
		super(material);
		this.setCreativeTab(ModItems.tab);
	}
	
	@Override
	public String getUnlocalizedName(){
		// return String.format("tile.%s%s", DEF.MOD_ID.toLowerCase() + ":",
		// getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
		return String.format("tile.%s", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon(String.format(DEF.MOD_ID + ":%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
	}
	
	protected String getUnwrappedUnlocalizedName(String unlocalizedName){
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
