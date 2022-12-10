package ee_man.mod3.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ee_man.mod3.container.ContainerRobotPanel.RobotController;

public class PlayerItemController implements RobotController{
	
	public EntityPlayer player;
	
	public ItemStack item;
	
	public PlayerItemController(EntityPlayer par1Player, ItemStack par2Item){
		player = par1Player;
		item = par2Item;
	}
	
	@Override
	public void setSelectRobot(int id){
		if(player.getHeldItem() != null){
			ItemStack item = player.getHeldItem();
			if(!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
			NBTTagCompound nbttag = item.getTagCompound();
			nbttag.setInteger("selectId", id);
		}
	}
	
	@Override
	public int getSelectRobot(){
		if(player.getHeldItem() != null){
			ItemStack item = player.getHeldItem();
			if(item.hasTagCompound()){
				NBTTagCompound nbttag = item.getTagCompound();
				if(nbttag.hasKey("selectId"))
					return nbttag.getInteger("selectId");
			}
		}
		return -1;
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		if(player.getHeldItem() != null)
			return player.getHeldItem().getDisplayName() == item.getDisplayName();
		else
			return false;
	}
}
