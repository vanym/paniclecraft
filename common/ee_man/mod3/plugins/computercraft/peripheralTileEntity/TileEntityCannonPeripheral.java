package ee_man.mod3.plugins.computercraft.peripheralTileEntity;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;
import ee_man.mod3.tileEntity.TileEntityCannon;

public class TileEntityCannonPeripheral extends TileEntityCannon implements IPeripheral{
	public String getType(){
		return "cannon";
	}
	
	public String[] getMethodNames(){
		return new String[]{"setDirection", "setHeight", "setStrength", "getDirection", "getHeight", "getStrength", "getMaxStrength"};
	}
	
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception{
		switch(method){
			case 0:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						int i = d.intValue();
						if(i >= 0 && i < 360){
							short s = (short)i;
							if(this.direction != s){
								this.direction = s;
								this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
							}
							return null;
						}
						else
							throw new Exception("number must be from 0 to 359");
					}
					else
						throw new Exception("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new Exception("too few arguments");
					else
						throw new Exception("too many arguments");
				}
			case 1:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						int i = d.intValue();
						if(i >= 0 && i <= 90){
							byte b = (byte)i;
							if(this.height != b){
								this.height = b;
								this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
							}
							return null;
						}
						else
							throw new Exception("number must be from 0 to 90");
					}
					else
						throw new Exception("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new Exception("too few arguments");
					else
						throw new Exception("too many arguments");
				}
			case 2:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						int i = d.intValue();
						if(i >= 0 && i <= maxStrength){
							byte b = (byte)i;
							if(this.strength != b){
								this.strength = b;
								this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
							}
							return null;
						}
						else
							throw new Exception("number must be from 0 to maxStrength");
					}
					else
						throw new Exception("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new Exception("too few arguments");
					else
						throw new Exception("too many arguments");
				}
			case 3:
				return new Object[]{this.direction};
			case 4:
				return new Object[]{this.height};
			case 5:
				return new Object[]{this.strength};
			case 6:
				return new Object[]{this.maxStrength};
		}
		return null;
	}
	
	public boolean canAttachToSide(int side){
		return true;
	}
	
	public void attach(IComputerAccess computer){
	}
	
	public void detach(IComputerAccess computer){
	}
}
