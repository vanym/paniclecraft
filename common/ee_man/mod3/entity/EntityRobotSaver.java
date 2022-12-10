package ee_man.mod3.entity;

import java.util.Map;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityRobotSaver extends EntityLivingBase{
	
	public EntityRobot robot;
	
	public NBTTagCompound nbt;
	
	public EntityRobotSaver(World par1World){
		super(par1World);
	}
	
	public EntityRobotSaver(World par1World, EntityRobot par2Robot){
		this(par1World);
		robot = par2Robot;
	}
	
	public void entityInit(){
	}
	
	public void readEntityFromNBT(NBTTagCompound nbttagcompound){
	}
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound){
	}
	
	public void onUpdate(){
		if(!this.worldObj.isRemote)
			if(robot == null && nbt == null)
				this.worldObj.removeEntity(this);
			else
				if(robot == null){
					if(!nbt.hasKey("botId"))
						robot = new EntityRobot(this.worldObj);
					else
						robot = new EntityRobot(this.worldObj, nbt.getInteger("botId"));
					robot.readFromNBT(nbt);
					nbt = null;
					this.worldObj.spawnEntityInWorld(robot);
					robot.mountEntity(ridingEntity);
					if(this.riddenByEntity != null)
						this.riddenByEntity.mountEntity(robot);
					this.worldObj.removeEntity(this);
				}
	}
	
	public boolean addEntityID(NBTTagCompound par1NBTTagCompound){
		if(nbt == null)
			return false;
		else
			return super.addEntityID(par1NBTTagCompound);
	}
	
	public boolean addNotRiddenEntityID(NBTTagCompound par1NBTTagCompound){
		if(nbt == null)
			return false;
		else
			return super.addNotRiddenEntityID(par1NBTTagCompound);
	}
	
	public void travelToDimension(int par1){
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		if(nbt != null){
			nbt.setString("id", par1NBTTagCompound.getString("id"));
			@SuppressWarnings("rawtypes")
			Map map = ReflectionHelper.getPrivateValue(NBTTagCompound.class, nbt, 0);
			ReflectionHelper.setPrivateValue(NBTTagCompound.class, par1NBTTagCompound, map, 0);
		}
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		nbt = par1NBTTagCompound;
		super.readFromNBT(par1NBTTagCompound);
	}
	
	@Override
	public ItemStack getHeldItem(){
		return null;
	}
	
	@Override
	public ItemStack getCurrentItemOrArmor(int i){
		return null;
	}
	
	@Override
	public void setCurrentItemOrArmor(int i, ItemStack itemstack){
	}
	
	@Override
	public ItemStack[] getLastActiveItems(){
		return null;
	}
}