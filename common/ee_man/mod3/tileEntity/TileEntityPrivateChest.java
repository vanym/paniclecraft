package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.inventory.InventoryPrivateChest;
import ee_man.mod3.inventory.InventoryUpgradesPrivateChest;
import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPrivateChest extends TileEntity implements IInventory{
	
	public NBTTagCompound upgradesDataSendable;
	public NBTTagCompound upgradesDataNotSendable;
	
	public InventoryPrivateChest inventoryItems;
	public InventoryUpgradesPrivateChest inventoryUpgrades;
	
	public byte select = -1;
	
	public TileEntityPrivateChest(){
		inventoryItems = new InventoryPrivateChest(64, 64);
		inventoryUpgrades = new InventoryUpgradesPrivateChest(this);
		upgradesDataSendable = new NBTTagCompound();
		upgradesDataNotSendable = new NBTTagCompound();
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		
		this.select = par1NBTTagCompound.getByte("select");
		
		NBTTagList nbttaglistItems = par1NBTTagCompound.getTagList("Items");
		
		for(int i = 0; i < nbttaglistItems.tagCount(); ++i){
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglistItems.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			
			if(j >= 0 && j < this.inventoryItems.getSizeInventory()){
				this.inventoryItems.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
			}
		}
		NBTTagList nbttaglistUpgrades = par1NBTTagCompound.getTagList("Upgrades");
		
		for(int i = 0; i < nbttaglistUpgrades.tagCount(); ++i){
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglistUpgrades.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			
			if(j >= 0 && j < this.inventoryUpgrades.getSizeInventory()){
				this.inventoryUpgrades.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
			}
		}
		
		this.upgradesDataSendable = par1NBTTagCompound.getCompoundTag("upgradesDataSendable");
		this.upgradesDataNotSendable = par1NBTTagCompound.getCompoundTag("upgradesDataNotSendable");
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByte("select", this.select);
		NBTTagList nbttaglistItems = new NBTTagList();
		
		for(int i = 0; i < this.inventoryItems.getSizeInventory(); ++i){
			if(this.inventoryItems.getStackInSlot(i) != null){
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				this.inventoryItems.getStackInSlot(i).writeToNBT(nbttagcompound1);
				nbttaglistItems.appendTag(nbttagcompound1);
			}
		}
		
		NBTTagList nbttaglistUpgrades = new NBTTagList();
		
		for(int i = 0; i < this.inventoryUpgrades.getSizeInventory(); ++i){
			if(this.inventoryUpgrades.getStackInSlot(i) != null){
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				this.inventoryUpgrades.getStackInSlot(i).writeToNBT(nbttagcompound1);
				nbttaglistUpgrades.appendTag(nbttagcompound1);
			}
		}
		
		par1NBTTagCompound.setTag("Items", nbttaglistItems);
		par1NBTTagCompound.setTag("Upgrades", nbttaglistUpgrades);
		par1NBTTagCompound.setTag("upgradesDataSendable", this.upgradesDataSendable);
		par1NBTTagCompound.setTag("upgradesDataNotSendable", this.upgradesDataNotSendable);
	}
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(6);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.writeByte(select);
			for(int i = 0; i < this.inventoryUpgrades.getSizeInventory(); i++){
				Packet.writeItemStack(this.inventoryUpgrades.getStackInSlot(i), data);
			}
			NBTTagCompound.writeNamedTag(upgradesDataSendable, data);
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
	
	public ItemStack getSelectedUpgrade(){
		if(this.select >= 0)
			return this.inventoryUpgrades.getStackInSlot(select);
		else
			return null;
	}
	
	public void updateEntity(){
		if(!this.worldObj.isRemote){
			for(int i = 0; i < inventoryUpgrades.getSizeInventory(); i++){
				ItemStack is = inventoryUpgrades.getStackInSlot(i);
				if(is != null)
					if(is.getItem() instanceof IUpgradeForPrivateChest){
						((IUpgradeForPrivateChest)is.getItem()).onChestUpdate(this, is);
					}
					else{
						inventoryUpgrades.setInventorySlotContents(i, null);
						EntityItem em = new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, is);
						this.worldObj.spawnEntityInWorld(em);
					}
			}
		}
	}
	
	public boolean canPlayerOpen(EntityPlayer player, int par6, float par7, float par8, float par9){
		boolean result = true;
		ArrayList<ItemStack> list = this.inventoryUpgrades.getNotNullUpgrades();
		@SuppressWarnings("rawtypes")
		Iterator ir = list.iterator();
		while(ir.hasNext()){
			ItemStack is = (ItemStack)ir.next();
			if(is.getItem() instanceof IUpgradeForPrivateChest){
				if(!((IUpgradeForPrivateChest)is.getItem()).canPlayerOpenChest(this, is, player, par6, par7, par8, par9)){
					result = false;
				}
			}
		}
		
		@SuppressWarnings("rawtypes")
		Iterator ir2 = list.iterator();
		while(ir2.hasNext()){
			ItemStack is = (ItemStack)ir2.next();
			if(is.getItem() instanceof IUpgradeForPrivateChest){
				if(result){
					((IUpgradeForPrivateChest)is.getItem()).onPlayerTryOpenChestAndHeCan(this, is, player, par6, par7, par8, par9);
				}
				else{
					((IUpgradeForPrivateChest)is.getItem()).onPlayerTryOpenChestAndHeCannot(this, is, player, par6, par7, par8, par9);
				}
			}
		}
		return result;
	}
	
	public boolean canBeBroken(){
		boolean result = true;
		ArrayList<ItemStack> list = this.inventoryUpgrades.getNotNullUpgrades();
		@SuppressWarnings("rawtypes")
		Iterator ir = list.iterator();
		while(ir.hasNext()){
			ItemStack is = (ItemStack)ir.next();
			if(is.getItem() instanceof IUpgradeForPrivateChest){
				if(!((IUpgradeForPrivateChest)is.getItem()).canChestBeBroken(this, is)){
					result = false;
				}
			}
		}
		return result;
	}
	
	public boolean openInventoryForWorld(){
		boolean result = true;
		ArrayList<ItemStack> list = this.inventoryUpgrades.getNotNullUpgrades();
		@SuppressWarnings("rawtypes")
		Iterator ir = list.iterator();
		while(ir.hasNext()){
			ItemStack is = (ItemStack)ir.next();
			if(is.getItem() instanceof IUpgradeForPrivateChest){
				if(!((IUpgradeForPrivateChest)is.getItem()).openInventoryForWorld(this, is)){
					result = false;
				}
			}
		}
		return result;
	}
	
	@Override
	public int getSizeInventory(){
		return(!this.openInventoryForWorld() ? 0 : this.inventoryItems.getSizeInventory());
	}
	
	@Override
	public ItemStack getStackInSlot(int i){
		return(!this.openInventoryForWorld() ? null : this.inventoryItems.getStackInSlot(i));
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j){
		return(!this.openInventoryForWorld() ? null : this.inventoryItems.decrStackSize(i, j));
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i){
		return(!this.openInventoryForWorld() ? null : this.inventoryItems.getStackInSlotOnClosing(i));
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack){
		if(this.openInventoryForWorld())
			this.inventoryItems.setInventorySlotContents(i, itemstack);
		else{
			EntityItem em = new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, itemstack);
			this.worldObj.spawnEntityInWorld(em);
		}
	}
	
	@Override
	public String getInvName(){
		return "PrivateChest";
	}
	
	@Override
	public boolean isInvNameLocalized(){
		return false;
	}
	
	@Override
	public int getInventoryStackLimit(){
		return(!this.openInventoryForWorld() ? 0 : this.inventoryItems.getInventoryStackLimit());
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return(!this.openInventoryForWorld() ? false : this.inventoryItems.isUseableByPlayer(entityplayer));
	}
	
	@Override
	public void openChest(){
	}
	
	@Override
	public void closeChest(){
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return(!this.openInventoryForWorld() ? false : this.inventoryItems.isItemValidForSlot(i, itemstack));
	}
	
	@SideOnly(Side.CLIENT)
	public boolean specialRender(){
		boolean toReturn = false;
		ArrayList<ItemStack> list = this.inventoryUpgrades.getNotNullUpgrades();
		@SuppressWarnings("rawtypes")
		Iterator ir = list.iterator();
		while(ir.hasNext()){
			ItemStack is = (ItemStack)ir.next();
			if(is.getItem() instanceof IUpgradeForPrivateChest){
				if(((IUpgradeForPrivateChest)is.getItem()).specialRender(this, is)){
					toReturn = true;
				}
			}
		}
		return toReturn;
	}
}
