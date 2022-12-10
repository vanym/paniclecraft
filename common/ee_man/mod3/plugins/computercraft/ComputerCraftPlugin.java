package ee_man.mod3.plugins.computercraft;

import ee_man.mod3.Core;

public class ComputerCraftPlugin{
	public static void registerTurtles(){
		try{
			if(Core.itemPaintBrush != null && Core.config.get("ComputerCraft", "TurtlePaintBrush", true).getBoolean(true))
				dan200.turtle.api.TurtleAPI.registerUpgrade(new ee_man.mod3.plugins.computercraft.turtle.TurtlePaintBrush());
			if(Core.itemSignEditTool != null && Core.config.get("ComputerCraft", "TurtleSignEdit", true).getBoolean(true))
				dan200.turtle.api.TurtleAPI.registerUpgrade(new ee_man.mod3.plugins.computercraft.turtle.TurtleSignEdit());
		} catch(Exception e){
			Core.log.warning("ComputerCraft problem");
		}
	}
}
