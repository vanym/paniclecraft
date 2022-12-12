package com.vanym.paniclecraft.plugins.computercraft.pte;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

public class TileEntityCannonPeripheral implements IPeripheral{
	
	public final TileEntityCannon tileCannon;
	
	public TileEntityCannonPeripheral(TileEntityCannon cannon){
		tileCannon = cannon;
	}
	
	@Override
	public String getType(){
		return "cannon";
	}
	
	@Override
	public String[] getMethodNames(){
		return new String[]{"setDirection", "setHeight", "setStrength", "getDirection", "getHeight", "getStrength", "getMaxStrength"};
	}
	
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException{
		switch(method){
			case 0:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						double i = d.doubleValue();
						if(i >= 0 && i < 360){
							if(tileCannon.getDirection() != i){
								tileCannon.setDirection(i);
								tileCannon.getWorldObj().markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
							}
							return null;
						}
						else
							throw new LuaException("number must be from 0 to 359");
					}
					else
						throw new LuaException("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new LuaException("too few arguments");
					else
						throw new LuaException("too many arguments");
				}
			case 1:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						double i = d.doubleValue();
						if(i >= -90 && i <= 90){
							if(tileCannon.getHeight() != i){
								tileCannon.setHeight(i);
								tileCannon.getWorldObj().markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
							}
							return null;
						}
						else
							throw new LuaException("number must be from 0 to 90");
					}
					else
						throw new LuaException("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new LuaException("too few arguments");
					else
						throw new LuaException("too many arguments");
				}
			case 2:
				if(arguments.length == 1){
					if(arguments[0] instanceof Double){
						Double d = (Double)arguments[0];
						double i = d.doubleValue();
						if(i >= 0 && i <= tileCannon.maxStrength){
							if(tileCannon.getStrength() != i){
								tileCannon.setStrength(i);
								tileCannon.getWorldObj().markBlockForUpdate(tileCannon.xCoord, tileCannon.yCoord, tileCannon.zCoord);
							}
							return null;
						}
						else
							throw new LuaException("number must be from 0 to maxStrength");
					}
					else
						throw new LuaException("argument must be a number");
				}
				else{
					if(arguments.length < 1)
						throw new LuaException("too few arguments");
					else
						throw new LuaException("too many arguments");
				}
			case 3:
				return new Object[]{tileCannon.getDirection()};
			case 4:
				return new Object[]{tileCannon.getHeight()};
			case 5:
				return new Object[]{tileCannon.getStrength()};
			case 6:
				return new Object[]{tileCannon.maxStrength};
		}
		return null;
	}
	
	@Override
	public void attach(IComputerAccess computer){
	}
	
	@Override
	public void detach(IComputerAccess computer){
	}
	
	@Override
	public boolean equals(IPeripheral other){
		if(other instanceof TileEntityCannonPeripheral){
			return tileCannon.equals(((TileEntityCannonPeripheral)other).tileCannon);
		}
		else
			return false;
	}
	
}
