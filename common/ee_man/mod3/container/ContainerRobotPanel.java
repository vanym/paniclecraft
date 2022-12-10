package ee_man.mod3.container;

import java.util.ArrayList;

import ee_man.mod3.entity.EntityRobot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerRobotPanel extends Container{
	
	public ArrayList<EntityRobot> robotList;
	
	public RobotController controller;
	
	public ContainerRobotPanel(ArrayList<EntityRobot> par1RobotList, RobotController par2Controller){
		robotList = par1RobotList;
		controller = par2Controller;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer){
		return controller.canInteractWith(entityplayer);
	}
	
	public EntityRobot getRobotById(int id){
		for(EntityRobot robot : this.robotList){
			if(robot.id == id){
				return robot;
			}
		}
		return null;
	}
	
	public EntityRobot getSelectedRobot(){
		return getRobotById(controller.getSelectRobot());
	}
	
	public int[] getRobotsIds(){
		int[] ret = new int[this.robotList.size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = this.robotList.get(i).id;
		}
		return ret;
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
	
	public static interface RobotController{
		void setSelectRobot(int id);
		
		int getSelectRobot();
		
		boolean canInteractWith(EntityPlayer entityplayer);
	}
}
