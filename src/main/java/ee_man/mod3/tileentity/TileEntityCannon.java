package ee_man.mod3.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCannon extends TileEntity implements IInventory{
	
	public static double defMaxStrength = 10;
	
	public double maxStrength = defMaxStrength;
	
	public double direction = 0;
	
	public double height = 0;
	
	public double strength = 1;
	
	public ItemStack item;
	
	public Vec3 vector;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setDouble("direction", direction);
		par1NBTTagCompound.setDouble("height", height);
		par1NBTTagCompound.setDouble("strength", strength);
		par1NBTTagCompound.setDouble("maxStrength", maxStrength);
		NBTTagCompound nbttag = new NBTTagCompound();
		if(item != null)
			item.writeToNBT(nbttag);
		par1NBTTagCompound.setTag("item", nbttag);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		direction = par1NBTTagCompound.getDouble("direction");
		height = par1NBTTagCompound.getDouble("height");
		strength = par1NBTTagCompound.getDouble("strength");
		maxStrength = par1NBTTagCompound.getDouble("maxStrength");
		NBTTagCompound nbttag = par1NBTTagCompound.getCompoundTag("item");
		item = ItemStack.loadItemStackFromNBT(nbttag);
		vector = null;
	}
	
	public void updateEntity(){
		if(!this.worldObj.isRemote){
			this.shot(item);
			item = null;
		}
	}
	
	public Packet getDescriptionPacket(){
		NBTTagCompound dataTag = new NBTTagCompound();
		this.writeToNBT(dataTag);
		dataTag.removeTag("item");
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
	}
	
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet){
		NBTTagCompound nbtData = packet.func_148857_g();
		this.readFromNBT(nbtData);
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()){
			net.minecraft.client.gui.GuiScreen gui = net.minecraft.client.Minecraft.getMinecraft().currentScreen;
			if(gui != null)
				if(gui instanceof ee_man.mod3.client.gui.container.GuiCannon){
					ee_man.mod3.client.gui.container.GuiCannon guiCannon = (ee_man.mod3.client.gui.container.GuiCannon)gui;
					if(guiCannon.cannon.equals(this))
						guiCannon.checkHeight();
				}
		}
	}
	
	public void setDirection(double par1){
		direction = par1;
		vector = null;
	}
	
	public double getDirection(){
		return direction;
	}
	
	public void setHeight(double par1){
		height = par1;
		vector = null;
	}
	
	public double getHeight(){
		return height;
	}
	
	public void setStrength(double par1){
		strength = par1;
		//vector = null;
	}
	
	public double getStrength(){
		return strength;
	}
	
	public Vec3 getVector(){
		if(vector == null){
			double hc = Math.cos(Math.toRadians((double)this.height));
			double hs = Math.sin(Math.toRadians((double)this.height));
			int d = (int)this.direction;
			double rd = (Math.sin(Math.toRadians(d)));
			double ld = (Math.cos(Math.toRadians(d)));
			vector = Vec3.createVectorHelper(-hc * rd, hs, hc * ld);
		}
		return vector;
	}
	
	public void shot(ItemStack shotItem){
		if(shotItem == null)
			return;
		EntityItem entityitem = new EntityItem(this.worldObj, (double)this.xCoord + 0.5D, (double)this.yCoord + 0.4D, (double)this.zCoord + 0.5D, shotItem);
		//entityitem.lifespan = 72000;
		entityitem.delayBeforeCanPickup = 15;
		double s = this.strength;
		Vec3 m = this.getVector();
		entityitem.motionX = m.xCoord * s;
		entityitem.motionZ = m.zCoord * s;
		entityitem.motionY = m.yCoord * s;
		this.worldObj.spawnEntityInWorld(entityitem);
		shotItem = null;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox((double)this.xCoord - 0.5F, (double)this.yCoord + 0.0F, (double)this.zCoord - 0.5F, (double)this.xCoord + 1.5F, (double)this.yCoord + 1.5F, (double)this.zCoord + 1.5F);
	}
	
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared(){
		return 16384.0D;
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
				this.markDirty();
				return itemstack;
			}
			else{
				itemstack = item.splitStack(par2);
				
				if(item.stackSize == 0){
					item = null;
				}
				
				this.markDirty();
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
		
		this.markDirty();
	}
	
	@Override
	public String getInventoryName(){
		return "tile.cannon.inv";
	}
	
	@Override
	public int getInventoryStackLimit(){
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityplayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
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
	
	@Override
	public boolean hasCustomInventoryName(){
		return false;
	}
	
	@Override
	public void openInventory(){
	}
	
	@Override
	public void closeInventory(){
		
	}
}
