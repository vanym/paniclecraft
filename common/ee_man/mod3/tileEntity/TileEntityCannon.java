package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.utils.Localization;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityCannon extends TileEntity implements IInventory{
	
	public static byte defMaxStrength = 20;
	
	public byte maxStrength = defMaxStrength;
	
	public short direction = 0;
	
	public byte height = 0;
	
	public byte strength = 0;
	
	public ItemStack item;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("direction", direction);
		par1NBTTagCompound.setByte("height", height);
		par1NBTTagCompound.setByte("strength", strength);
		par1NBTTagCompound.setByte("maxStrength", maxStrength);
		NBTTagCompound nbttag = new NBTTagCompound();
		if(item != null)
			item.writeToNBT(nbttag);
		par1NBTTagCompound.setTag("item", nbttag);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		direction = par1NBTTagCompound.getShort("direction");
		height = par1NBTTagCompound.getByte("height");
		strength = par1NBTTagCompound.getByte("strength");
		maxStrength = par1NBTTagCompound.getByte("maxStrength");
		NBTTagCompound nbttag = par1NBTTagCompound.getCompoundTag("item");
		item = ItemStack.loadItemStackFromNBT(nbttag);
	}
	
	public void updateEntity(){
		this.shot();
	}
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(4);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.writeShort(direction);
			data.writeByte(height);
			data.writeByte(strength);
			data.writeByte(maxStrength);
		} catch(IOException e){
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = DefaultProperties.MOD_ID;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;
		packet.isChunkDataPacket = false;
		return packet;
	}
	
	public void shot(){
		if(item == null || this.worldObj.isRemote)
			return;
		EntityItem entityitem = new EntityItem(this.worldObj, (double)this.xCoord + 0.5D, (double)this.yCoord + 0.4D, (double)this.zCoord + 0.5D, item);
		entityitem.delayBeforeCanPickup = 15;
		double s = 1.0D + 0.1D * this.strength;
		double us = s * (double)(90 - this.height) / 90.0D;
		int d = (int)this.direction;
		while(d >= 90)
			d -= 90;
		double r = us * ((double)(d) / 90.0D);
		double l = us * ((double)(90 - d) / 90.0D);
		switch(this.direction / 90){
			case 0:
				entityitem.motionX = -r;
				entityitem.motionZ = l;
			break;
			case 1:
				entityitem.motionX = -l;
				entityitem.motionZ = -r;
			break;
			case 2:
				entityitem.motionX = r;
				entityitem.motionZ = -l;
			break;
			case 3:
				entityitem.motionX = l;
				entityitem.motionZ = r;
			break;
		}
		entityitem.motionY = s * ((double)this.height / 90.0D);
		this.worldObj.spawnEntityInWorld(entityitem);
		this.item = null;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord - 0.5F, (double)this.yCoord + 0.0F, (double)this.zCoord - 0.5F, (double)this.xCoord + 1.5F, (double)this.yCoord + 1.5F, (double)this.zCoord + 1.5F);
	}
	
	@Override
	public int getSizeInventory(){
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int i){
		if(i == 0)
			return item;
		return null;
	}
	
	@Override
	public ItemStack decrStackSize(int par1, int par2){
		if(item != null){
			ItemStack itemstack;
			
			if(item.stackSize <= par2){
				itemstack = item;
				item = null;
				this.onInventoryChanged();
				return itemstack;
			}
			else{
				itemstack = item.splitStack(par2);
				
				if(item.stackSize == 0){
					item = null;
				}
				
				this.onInventoryChanged();
				return itemstack;
			}
		}
		else{
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int par1){
		if(item != null){
			ItemStack itemstack = item;
			item = null;
			return itemstack;
		}
		else{
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack){
		item = par2ItemStack;
		
		if(par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()){
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
		
		this.onInventoryChanged();
	}
	
	@Override
	public String getInvName(){
		return Localization.get("tile.cannon.inv.name");
	}
	
	@Override
	public int getInventoryStackLimit(){
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityplayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	
	public void openChest(){
	}
	
	public void closeChest(){
	}
	
	public boolean isInvNameLocalized(){
		return false;
	}
	
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return true;
	}
}
