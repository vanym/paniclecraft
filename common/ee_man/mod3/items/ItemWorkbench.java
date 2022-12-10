package ee_man.mod3.items;

import ee_man.mod3.Core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWorkbench extends ItemMod3{
	
	public ItemWorkbench(int par1, int damage){
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(damage);
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		if(!par2World.isRemote)
			par3.openGui(Core.instance, 4, par2World, (int)par3.posX, (int)par3.posY, (int)par3.posZ);
		return par1ItemStack;
	}
}
