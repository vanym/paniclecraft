package ee_man.mod3.items;

import ee_man.mod3.utils.Localization;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMod3 extends ItemBlock{
	
	public ItemBlockMod3(int par1, boolean par2){
		super(par1);
		this.setHasSubtypes(par2);
	}
	
	public String getItemDisplayName(ItemStack itemstack){
		if(!this.hasSubtypes)
			return Localization.get(getUnlocalizedName(itemstack));
		else
			return Localization.get(getUnlocalizedName(itemstack) + itemstack.getItemDamage());
	}
}
