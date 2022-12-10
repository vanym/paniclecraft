package ee_man.mod3.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ee_man.mod3.Core;

public class ItemPalette extends ItemMod3{
	
	public ItemPalette(int par1){
		super(par1);
		this.setMaxStackSize(1);
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		if(!par2World.isRemote)
			par3.openGui(Core.instance, 5, par2World, (int)par3.posX, (int)par3.posY, (int)par3.posZ);
		return par1ItemStack;
	}
}
