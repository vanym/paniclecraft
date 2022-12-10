package ee_man.mod3.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.item.ItemWorkbench;

public class SlotPortableCrafting extends SlotCrafting{
	
	public SlotPortableCrafting(EntityPlayer par1EntityPlayer, IInventory par2iInventory, IInventory par3iInventory, int par4, int par5, int par6){
		super(par1EntityPlayer, par2iInventory, par3iInventory, par4, par5, par6);
	}
	
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack){
		ItemStack heldItem = par1EntityPlayer.getHeldItem();
		if(heldItem != null && heldItem.getItem() instanceof ItemWorkbench && ModItems.itemWorkbench.getMaxDamage() > 0){
			heldItem.damageItem(1, par1EntityPlayer);
		}
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
	}
}
