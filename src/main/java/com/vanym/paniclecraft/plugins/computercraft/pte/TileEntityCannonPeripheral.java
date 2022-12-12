package com.vanym.paniclecraft.plugins.computercraft.pte;

import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class TileEntityCannonPeripheral implements IPeripheral {
    
    public final TileEntityCannon tileCannon;
    
    public TileEntityCannonPeripheral(TileEntityCannon cannon) {
        this.tileCannon = cannon;
    }
    
    @Override
    public String getType() {
        return "cannon";
    }
    
    @Override
    public String[] getMethodNames() {
        return new String[]{"setDirection", "setHeight", "setStrength", "getDirection", "getHeight",
                            "getStrength", "getMaxStrength"};
    }
    
    @Override
    public Object[] callMethod(
            IComputerAccess computer,
            ILuaContext context,
            int method,
            Object[] arguments) throws LuaException, InterruptedException {
        switch (method) {
            case 0:
                if (arguments.length == 1) {
                    if (arguments[0] instanceof Double) {
                        Double d = (Double)arguments[0];
                        double i = d.doubleValue();
                        if (i >= 0 && i < 360) {
                            if (this.tileCannon.getDirection() != i) {
                                this.tileCannon.setDirection(i);
                                this.tileCannon.getWorldObj()
                                               .markBlockForUpdate(this.tileCannon.xCoord,
                                                                   this.tileCannon.yCoord,
                                                                   this.tileCannon.zCoord);
                            }
                            return null;
                        } else {
                            throw new LuaException("number must be from 0 to 359");
                        }
                    } else {
                        throw new LuaException("argument must be a number");
                    }
                } else {
                    if (arguments.length < 1) {
                        throw new LuaException("too few arguments");
                    } else {
                        throw new LuaException("too many arguments");
                    }
                }
            case 1:
                if (arguments.length == 1) {
                    if (arguments[0] instanceof Double) {
                        Double d = (Double)arguments[0];
                        double i = d.doubleValue();
                        if (i >= -90 && i <= 90) {
                            if (this.tileCannon.getHeight() != i) {
                                this.tileCannon.setHeight(i);
                                this.tileCannon.getWorldObj()
                                               .markBlockForUpdate(this.tileCannon.xCoord,
                                                                   this.tileCannon.yCoord,
                                                                   this.tileCannon.zCoord);
                            }
                            return null;
                        } else {
                            throw new LuaException("number must be from 0 to 90");
                        }
                    } else {
                        throw new LuaException("argument must be a number");
                    }
                } else {
                    if (arguments.length < 1) {
                        throw new LuaException("too few arguments");
                    } else {
                        throw new LuaException("too many arguments");
                    }
                }
            case 2:
                if (arguments.length == 1) {
                    if (arguments[0] instanceof Double) {
                        Double d = (Double)arguments[0];
                        double i = d.doubleValue();
                        if (i >= 0 && i <= this.tileCannon.maxStrength) {
                            if (this.tileCannon.getStrength() != i) {
                                this.tileCannon.setStrength(i);
                                this.tileCannon.getWorldObj()
                                               .markBlockForUpdate(this.tileCannon.xCoord,
                                                                   this.tileCannon.yCoord,
                                                                   this.tileCannon.zCoord);
                            }
                            return null;
                        } else {
                            throw new LuaException("number must be from 0 to maxStrength");
                        }
                    } else {
                        throw new LuaException("argument must be a number");
                    }
                } else {
                    if (arguments.length < 1) {
                        throw new LuaException("too few arguments");
                    } else {
                        throw new LuaException("too many arguments");
                    }
                }
            case 3:
                return new Object[]{this.tileCannon.getDirection()};
            case 4:
                return new Object[]{this.tileCannon.getHeight()};
            case 5:
                return new Object[]{this.tileCannon.getStrength()};
            case 6:
                return new Object[]{this.tileCannon.maxStrength};
        }
        return null;
    }
    
    @Override
    public void attach(IComputerAccess computer) {
    }
    
    @Override
    public void detach(IComputerAccess computer) {
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other instanceof TileEntityCannonPeripheral) {
            return this.tileCannon.equals(((TileEntityCannonPeripheral)other).tileCannon);
        } else {
            return false;
        }
    }
    
}
