package ee_man.mod3.utils;

import java.util.ArrayList;
import java.util.HashMap;

import ee_man.mod3.entity.EntityRobot;

public class RobotMap{
	public HashMap<String, ArrayList<EntityRobot>> map = new HashMap<String, ArrayList<EntityRobot>>();
	
	public void addRobot(String nick, EntityRobot robot){
		if(!map.containsKey(nick)){
			map.put(nick, new ArrayList<EntityRobot>());
		}
		map.get(nick).add(robot);
	}
	
	public void remove(String nick, EntityRobot robot){
		if(map.containsKey(nick)){
			map.get(nick).remove(robot);
		}
	}
	
	public boolean hasRobot(String nick, EntityRobot robot){
		return map.containsKey(nick) ? map.get(nick).contains(robot) : false;
	}
	
	public ArrayList<EntityRobot> getRobotArrayList(String nick){
		if(!map.containsKey(nick)){
			map.put(nick, new ArrayList<EntityRobot>());
		}
		return map.get(nick);
	}
}
