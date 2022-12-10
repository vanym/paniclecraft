package ee_man.mod3.recipe;

import cpw.mods.fml.common.ICraftingHandler;
import ee_man.mod3.Core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeAntiEnchant implements IRecipe, ICraftingHandler{
	
	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world){
		byte wand = 0;
		byte enchantItem = 0;
		for(int i = 0; i < inventorycrafting.getSizeInventory(); ++i){
			ItemStack is = inventorycrafting.getStackInSlot(i);
			
			if(is != null){
				if(is.hasTagCompound()){
					NBTTagCompound nt = is.getTagCompound();
					if(nt.hasKey("ench") || nt.hasKey("StoredEnchantments")){
						enchantItem++;
					}
				}
				else
					if(is.itemID == Core.itemAntiEnchantWand.itemID){
						wand++;
					}
					else
						return false;
			}
		}
		return wand == 1 && enchantItem == 1;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting){
		ItemStack ris = null;
		for(int i = 0; i < inventorycrafting.getSizeInventory(); ++i){
			ItemStack is = inventorycrafting.getStackInSlot(i);
			if(is != null){
				if(is.hasTagCompound()){
					NBTTagCompound nt = is.getTagCompound();
					if(nt.hasKey("ench") || nt.hasKey("StoredEnchantments")){
						ris = is.copy();
						break;
					}
				}
			}
		}
		if(ris != null){
			NBTTagCompound nt = ris.getTagCompound();
			if(ris.itemID != Item.enchantedBook.itemID)
				nt.removeTag("ench");
			else{
				nt.removeTag("StoredEnchantments");
				ris.itemID = Item.book.itemID;
			}
			if(nt.hasNoTags())
				ris.setTagCompound(null);
		}
		return ris;
	}
	
	@Override
	public int getRecipeSize(){
		return 0;
	}
	
	@Override
	public ItemStack getRecipeOutput(){
		return null;
	}
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix){
		for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
			ItemStack is = craftMatrix.getStackInSlot(i);
			if(is != null){
				if(is.itemID == Core.itemAntiEnchantWand.itemID && this.matches((InventoryCrafting)craftMatrix, player.worldObj)){
					is.stackSize++;
					if(Core.itemAntiEnchantWand.getMaxDamage() > 0)
						is.damageItem(1, player);
				}
			}
		}
	}
	
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item){
	}
	
}
