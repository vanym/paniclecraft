package ee_man.mod3.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import ee_man.mod3.Core;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class ItemBroom extends ItemMod3{
	
	protected double defDis = 6;
	
	public ItemBroom(int par1, int par2, double par3){
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(par2);
		defDis = par3;
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		if(!par2World.isRemote){
			this.collectItems(par1ItemStack, par2World, par3);
		}
		return par1ItemStack;
	}
	
	public int getItemEnchantability(){
		return Core.enchantmentRange == null ? 0 : 1;
	}
	
	public boolean isFull3D(){
		return true;
	}
	
	public boolean isItemTool(ItemStack par1ItemStack){
		return true;
	}
	
	public void collectItems(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		float ent = 0.125F;
		int lvl = 0;
		if(Core.enchantmentRange != null)
			lvl = EnchantmentHelper.getEnchantmentLevel(Core.enchantmentRange.effectId, par1ItemStack);
		double dis = defDis * (1 + ent * lvl);
		@SuppressWarnings("rawtypes")
		List list = par2World.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(par3.posX - (dis + 2), par3.posY - (dis + 2), par3.posZ - (dis + 2), par3.posX + (dis + 2), par3.posY + (dis + 2), par3.posZ + (dis + 2)));
		for(int g = 0; g < list.size(); g++){
			EntityItem itemEntity = (EntityItem)list.get(g);
			if(par3.getDistance(itemEntity.posX, itemEntity.posY, itemEntity.posZ) <= dis && par3.canEntityBeSeen(itemEntity)){
				EntityItemPickupEvent event = new EntityItemPickupEvent(par3, itemEntity);
				ItemStack itemStack = itemEntity.getEntityItem();
				int itemStackSize = itemStack.stackSize;
				if(itemEntity.delayBeforeCanPickup <= 0 && (event.getResult() == Result.ALLOW || itemStackSize <= 0 || par3.inventory.addItemStackToInventory(itemStack))){
					if(itemStack.itemID == Block.wood.blockID)
						par3.triggerAchievement(AchievementList.mineWood);
					
					if(itemStack.itemID == Item.leather.itemID)
						par3.triggerAchievement(AchievementList.killCow);
					
					if(itemStack.itemID == Item.diamond.itemID)
						par3.triggerAchievement(AchievementList.diamonds);
					
					if(itemStack.itemID == Item.blazeRod.itemID)
						par3.triggerAchievement(AchievementList.blazeRod);
					
					GameRegistry.onPickupNotification(par3, itemEntity);
					itemEntity.playSound("random.pop", 0.2F, (1.0F * 0.7F + 1.0F) * 2.0F);
					par3.onItemPickup(itemEntity, itemStackSize);
					par1ItemStack.damageItem(itemStackSize, par3);
					if(itemStack.stackSize <= 0){
						itemEntity.setDead();
					}
				}
			}
		}
		return;
	}
}
