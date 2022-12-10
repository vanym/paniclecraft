package ee_man.mod3.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.entity.EntityRobot;
import ee_man.mod3.utils.Localization;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemRobotController extends ItemMod3{
	
	public ItemRobotController(int par1){
		super(par1);
		this.setMaxStackSize(1);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4){
		if(par1ItemStack.hasTagCompound()){
			NBTTagCompound nbttag = par1ItemStack.getTagCompound();
			if(nbttag.hasKey("lockedNick")){
				par3List.add(Localization.get("text.robotController.lockedTo").replaceAll("%nick", nbttag.getString("lockedNick")));
			}
		}
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		if(par2World.isRemote)
			return par1ItemStack;
		if(!par3EntityPlayer.isSneaking()){
			par3EntityPlayer.openGui(Core.instance, 7, par2World, 0, 0, 0);
		}
		else{
			if(!par1ItemStack.hasTagCompound())
				par1ItemStack.setTagCompound(new NBTTagCompound());
			NBTTagCompound nbttag = par1ItemStack.getTagCompound();
			if(!nbttag.hasKey("lockedNick")){
				nbttag.setString("lockedNick", par3EntityPlayer.username);
				par3EntityPlayer.addChatMessage(Localization.get("text.robotController.lock"));
			}
			else{
				nbttag.removeTag("lockedNick");
				par3EntityPlayer.addChatMessage(Localization.get("text.robotController.unlock"));
			}
		}
		return par1ItemStack;
	}
	
	public boolean func_111207_a(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity){
		if(entity.worldObj.isRemote)
			return entity instanceof EntityPlayer;
		if(entity instanceof EntityRobot){
			EntityRobot robot = (EntityRobot)entity;
			String nick = player.username;
			if(itemstack.hasTagCompound()){
				NBTTagCompound nbttag = itemstack.getTagCompound();
				if(nbttag.hasKey("lockedNick"))
					nick = nbttag.getString("lockedNick");
			}
			if(!robot.isLocked){
				if(robot.ownerNick != null && !robot.ownerNick.equalsIgnoreCase(nick)){
					robot.setOwner(nick);
					player.addChatMessage(Localization.get("text.robot.set").replaceAll("%nick", nick));
				}
				if(player.isSneaking()){
					robot.isLocked = true;
					player.addChatMessage(Localization.get("text.robot.lock").replaceAll("%nick", nick));
				}
			}
			else{
				if(robot.ownerNick != null && robot.ownerNick.equalsIgnoreCase(nick) && player.isSneaking()){
					robot.isLocked = false;
					player.addChatMessage(Localization.get("text.robot.unlock").replaceAll("%nick", nick));
				}
			}
			return true;
		}
		else
			return false;
	}
}
