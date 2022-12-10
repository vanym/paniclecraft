package ee_man.mod3.tileEntity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPlayerInv extends TileEntity implements IInventory{
	
	public EntityPlayer player;
	
	public String playerName;
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		playerName = par1NBTTagCompound.getString("playerName");
	}
	
	public void checkPlayer(){
		if(!this.worldObj.isRemote){
			@SuppressWarnings("unchecked")
			ArrayList<EntityPlayerMP> playerList = (ArrayList<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			if(!playerList.contains(player) || !player.username.equalsIgnoreCase(playerName))
				player = null;
			if(player == null && playerName != null){
				player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		if(this.playerName != null)
			par1NBTTagCompound.setString("playerName", playerName);
	}
	
	@Override
	public int getSizeInventory(){
		this.checkPlayer();
		return player == null ? 0 : player.inventory.getSizeInventory();
	}
	
	@Override
	public ItemStack getStackInSlot(int i){
		this.checkPlayer();
		return player == null ? null : player.inventory.getStackInSlot(i);
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j){
		this.checkPlayer();
		return player == null ? null : player.inventory.decrStackSize(i, j);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i){
		this.checkPlayer();
		return player == null ? null : player.inventory.getStackInSlotOnClosing(i);
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack){
		this.checkPlayer();
		if(player != null)
			player.inventory.setInventorySlotContents(i, itemstack);
	}
	
	@Override
	public String getInvName(){
		this.checkPlayer();
		return player == null ? null : player.inventory.getInvName();
	}
	
	@Override
	public boolean isInvNameLocalized(){
		this.checkPlayer();
		return player == null ? false : player.inventory.isInvNameLocalized();
	}
	
	@Override
	public int getInventoryStackLimit(){
		this.checkPlayer();
		return player == null ? 0 : player.inventory.getInventoryStackLimit();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		this.checkPlayer();
		return player == null ? false : !player.isDead;
	}
	
	@Override
	public void openChest(){
		this.checkPlayer();
		if(player != null)
			player.inventory.openChest();
	}
	
	@Override
	public void closeChest(){
		this.checkPlayer();
		if(player != null)
			player.inventory.closeChest();
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		this.checkPlayer();
		if(player != null){
			return player.inventory.isItemValidForSlot(i, itemstack);
		}
		else
			return false;
	}
}
