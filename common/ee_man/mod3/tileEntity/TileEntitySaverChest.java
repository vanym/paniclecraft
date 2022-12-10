package ee_man.mod3.tileEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DefaultProperties;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class TileEntitySaverChest extends TileEntity{
	// @SideOnly(Side.CLIENT)
	public static final double itemNoRenderDis = 0.0D;
	// @SideOnly(Side.CLIENT)
	public static final double itemRenderDis = 1.0D;
	// @SideOnly(Side.CLIENT)
	public byte rotation = 0;
	@SuppressWarnings("rawtypes")
	public List entityList;
	
	public boolean open = false;
	
	public void updateEntity(){
		final float f = 0.0625F;
		if(entityList == null)
			entityList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(this.xCoord + f + (f / 2), this.yCoord + (f / 2), this.zCoord + f + (f / 2), this.xCoord + 1.0F - f - (f / 2), this.yCoord + 1.0F - f * 2 - (f / 2), this.zCoord + 1.0F - f - (f / 2)));
		for(int g = 0; g < entityList.size(); g++){
			EntityItem itemEntity = (EntityItem)entityList.get(g);
			itemEntity.setPosition(this.xCoord + 0.5, this.yCoord + 0.3, this.zCoord + 0.5);
			if(FMLCommonHandler.instance().getEffectiveSide().isClient()){
				if(this.rotation == 0 && !this.isInvalid())
					itemEntity.renderDistanceWeight = itemNoRenderDis;
				else
					itemEntity.renderDistanceWeight = itemRenderDis;
			}
		}
		entityList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(this.xCoord + f + (f / 2), this.yCoord + (f / 2), this.zCoord + f + (f / 2), this.xCoord + 1.0F - f - (f / 2), this.yCoord + 1.0F - f * 2 - (f / 2), this.zCoord + 1.0F - f - (f / 2)));
		if(this.worldObj.isRemote)
			return;
		for(int g = 0; g < entityList.size(); g++){
			EntityItem itemEntity = (EntityItem)entityList.get(g);
			if(itemEntity.delayBeforeCanPickup < 5)
				itemEntity.delayBeforeCanPickup = 5;
			itemEntity.age = 0;
		}
		@SuppressWarnings("rawtypes")
		List playerList = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord + f + (f / 2), this.yCoord + (f / 2), this.zCoord + f + (f / 2), this.xCoord + 1.0F - f - (f / 2), this.yCoord + 1.0F - f * 2 - (f / 2), this.zCoord + 1.0F - f - (f / 2)));
		for(int g = 0; g < playerList.size(); g++){
			if(entityList.size() > 0){
				EntityItem itemEntity = (EntityItem)entityList.get(0);
				EntityPlayer player = (EntityPlayer)playerList.get(g);
				EntityItemPickupEvent event = new EntityItemPickupEvent(player, itemEntity);
				ItemStack itemStack = itemEntity.getEntityItem();
				int itemStackSize = itemStack.stackSize;
				if(itemEntity.delayBeforeCanPickup <= 5 && (event.getResult() == Result.ALLOW || itemStackSize <= 0 || player.inventory.addItemStackToInventory(itemStack))){
					if(itemStack.itemID == Block.wood.blockID)
						player.triggerAchievement(AchievementList.mineWood);
					
					if(itemStack.itemID == Item.leather.itemID)
						player.triggerAchievement(AchievementList.killCow);
					
					if(itemStack.itemID == Item.diamond.itemID)
						player.triggerAchievement(AchievementList.diamonds);
					
					if(itemStack.itemID == Item.blazeRod.itemID)
						player.triggerAchievement(AchievementList.blazeRod);
					
					GameRegistry.onPickupNotification(player, itemEntity);
					itemEntity.playSound("random.pop", 0.2F, (1.0F * 0.7F + 1.0F) * 2.0F);
					player.onItemPickup(itemEntity, itemStackSize);
					if(itemStack.stackSize <= 0){
						itemEntity.setDead();
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord - 0.0F, (double)this.yCoord - 0.0F, (double)this.zCoord - 0.0F, (double)this.xCoord + 1.0F, (double)this.yCoord + (this.rotation > 0 ? 2.0F : 1.0F), (double)this.zCoord + 1.0F);
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setBoolean("open", open);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		this.open = par1NBTTagCompound.getBoolean("open");
	}
	
	public boolean canUpdate(){
		return true;
	}
	
	public Packet getDescriptionPacket(){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try{
			data.writeByte(5);
			data.writeInt(this.xCoord);
			data.writeInt(this.yCoord);
			data.writeInt(this.zCoord);
			data.writeBoolean(open);
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
	
	public void invalidate(){
		super.invalidate();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			for(int g = 0; g < entityList.size(); g++){
				if(entityList.get(g) != null && entityList.get(g) instanceof EntityItem){
					EntityItem itemEntity = (EntityItem)entityList.get(g);
					itemEntity.renderDistanceWeight = itemRenderDis;
				}
			}
	}
}
