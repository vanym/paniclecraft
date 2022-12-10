package ee_man.mod3.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ee_man.mod3.Core;
import ee_man.mod3.core.GUIs;

public class ItemPalette extends ItemMod3{
	
	public ItemPalette(){
		super();
		this.setMaxStackSize(1);
		this.setUnlocalizedName("palette");
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		if(!par2World.isRemote)
			par3.openGui(Core.instance, GUIs.PALETTE.ordinal(), par2World, (int)par3.posX, (int)par3.posY, (int)par3.posZ);
		return par1ItemStack;
	}
}
