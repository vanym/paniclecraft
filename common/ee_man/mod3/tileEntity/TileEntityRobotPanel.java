package ee_man.mod3.tileEntity;

import ee_man.mod3.container.ContainerRobotPanel.RobotController;
import ee_man.mod3.entity.EntityRobot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRobotPanel extends TileEntity implements RobotController{
	
	public int selectedRobotId = -1;
	
	public String ownerNick = null;
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("selectedRobotId", selectedRobotId);
		if(ownerNick != null)
			par1NBTTagCompound.setString("ownerNick", ownerNick);
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		selectedRobotId = par1NBTTagCompound.getInteger("selectedRobotId");
		if(par1NBTTagCompound.hasKey("ownerNick"))
			ownerNick = par1NBTTagCompound.getString("ownerNick");
	}
	
	@Override
	public void setSelectRobot(int par1){
		selectedRobotId = par1;
	}
	
	@Override
	public int getSelectRobot(){
		return selectedRobotId;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityplayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	
	public void handle(int a, int b){
		EntityRobot robot = this.getSelectedRobot();
		if(robot != null)
			robot.handle(a, b);
	}
	
	public Object[] getInfo(int a, int b){
		EntityRobot robot = this.getSelectedRobot();
		if(robot != null)
			return robot.getInfo(a, b);
		else
			return null;
	}
	
	public int[] getRobotsIds(){
		int[] ret = new int[ownerNick == null ? 0 : EntityRobot.robotLists.getRobotArrayList(ownerNick).size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = EntityRobot.robotLists.getRobotArrayList(ownerNick).get(i).id;
		}
		return ret;
	}
	
	public EntityRobot getRobotById(int id){
		if(ownerNick != null)
			for(EntityRobot robot : EntityRobot.robotLists.getRobotArrayList(ownerNick)){
				if(robot.id == id){
					return robot;
				}
			}
		return null;
	}
	
	public EntityRobot getSelectedRobot(){
		return getRobotById(selectedRobotId);
	}
	
}
