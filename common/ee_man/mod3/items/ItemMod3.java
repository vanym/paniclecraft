package ee_man.mod3.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.utils.Localization;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMod3 extends Item{
	
	public ItemMod3(int par1){
		super(par1);
		this.setCreativeTab(Core.creativeTab);
	}
	
	public String getItemDisplayName(ItemStack itemstack){
		return Localization.get(getUnlocalizedName(itemstack) + (this.hasSubtypes ? itemstack.getItemDamage() : ""));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		itemIcon = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName());
	}
}
