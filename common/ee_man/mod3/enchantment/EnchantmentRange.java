package ee_man.mod3.enchantment;

import ee_man.mod3.items.ItemBroom;
import ee_man.mod3.utils.Localization;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class EnchantmentRange extends Enchantment{
	
	public int maxLevel;
	
	public EnchantmentRange(int par1, int par2, int par3){
		super(par1, par2, EnumEnchantmentType.all);
		maxLevel = par3;
		this.setName("range");
	}
	
	public int getMaxLevel(){
		return maxLevel;
	}
	
	public int getMinEnchantability(int par1){
		return 1 + (par1 - 1) * 9;
	}
	
	public int getMaxEnchantability(int par1){
		return this.getMinEnchantability(par1) + 8;
	}
	
	public String getTranslatedName(int par1){
		return Localization.get(this.getName()) + " " + StatCollector.translateToLocal("enchantment.level." + par1);
	}
	
	public boolean canApply(ItemStack stack){
		return stack.getItem() instanceof ItemBroom;
	}
	
	public boolean canApplyAtEnchantingTable(ItemStack stack){
		return this.canApply(stack);
	}
}
