package ee_man.mod3.items;

import ee_man.mod3.entity.EntityRobot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRobot extends ItemMod3{
	
	public ItemRobot(int par1){
		super(par1);
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(par3World.isRemote)
			return true;
		switch(par7){
			case 0:
				par5--;
			break;
			case 1:
				par5++;
			break;
			case 2:
				par6--;
			break;
			case 3:
				par6++;
			break;
			case 4:
				par4--;
			break;
			case 5:
				par4++;
			break;
		}
		EntityRobot robot = new EntityRobot(par3World);
		robot.setPosition(par4 + 0.5D, par5, par6 + 0.5D);
		robot.fallDistance = 0.0F;
		robot.setOwner(par2EntityPlayer.username);
		par3World.spawnEntityInWorld(robot);
		if(!par2EntityPlayer.capabilities.isCreativeMode)
			par1ItemStack.stackSize--;
		return true;
	}
}
