package ee_man.mod3.plugins.computercraft.peripheralTileEntity;

import java.util.HashMap;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;
import ee_man.mod3.tileEntity.TileEntityRobotPanel;

public class TileEntityRobotPanelPeripheral extends TileEntityRobotPanel implements IPeripheral{
	
	@Override
	public String getType(){
		return "robotPanel";
	}
	
	@Override
	public String[] getMethodNames(){
		return new String[]{"action", "getInfo", "setRobot", "getRobot", "getRobotList"};
	}
	
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception{
		switch(method){
			case 0:{
				if(arguments.length >= 2 && arguments[0] instanceof Double && arguments[1] instanceof Double){
					this.handle(((Double)arguments[0]).intValue(), ((Double)arguments[1]).intValue());
				}
				else
					throw new Exception("not correct arguments(num , num)");
			}
			break;
			case 1:{
				if(arguments.length >= 2 && arguments[0] instanceof Double && arguments[1] instanceof Double){
					return this.getInfo(((Double)arguments[0]).intValue(), ((Double)arguments[1]).intValue());
				}
				else
					throw new Exception("not correct arguments(num , num)");
			}
			case 2:{
				if(arguments.length >= 1 && arguments[0] instanceof Double)
					this.selectedRobotId = ((Double)arguments[0]).intValue();
				else
					throw new Exception("not correct arguments(num)");
			}
			break;
			case 3:{
				return new Object[]{this.selectedRobotId};
			}
			case 4:{
				HashMap<Integer, Integer> robotsIdsHashMap = new HashMap<Integer, Integer>();
				int[] robotsIdsArray = this.getRobotsIds();
				for(int i = 0; i < robotsIdsArray.length; i++)
					robotsIdsHashMap.put(i + 1, robotsIdsArray[i]);
				return new Object[]{robotsIdsHashMap};
			}
		}
		return null;
	}
	
	@Override
	public boolean canAttachToSide(int side){
		return true;
	}
	
	@Override
	public void attach(IComputerAccess computer){
	}
	
	@Override
	public void detach(IComputerAccess computer){
	}
	
}
