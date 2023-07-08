package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class CannonPeripheral extends PeripheralBase {
    
    public final TileEntityCannon cannon;
    
    public CannonPeripheral(TileEntityCannon cannon) {
        this.cannon = cannon;
    }
    
    @Override
    public String getType() {
        return "cannon";
    }
    
    @PeripheralMethod(0)
    public double getDirection() {
        return this.cannon.getDirection();
    }
    
    @PeripheralMethod(1)
    public double getHeight() {
        return this.cannon.getHeight();
    }
    
    @PeripheralMethod(2)
    public double getStrength() {
        return this.cannon.getStrength();
    }
    
    @PeripheralMethod(3)
    public double getMaxStrength() {
        return Core.instance.cannon.maxStrength.get();
    }
    
    @PeripheralMethod(10)
    public void setDirection(double direction) {
        this.cannon.setDirection(direction);
        this.cannon.markForUpdate();
    }
    
    @PeripheralMethod(11)
    public void setHeight(double height) throws LuaException {
        if (!this.cannon.setHeight(height)) {
            throw new LuaException(
                    String.format("height must be from %s to %s",
                                  TileEntityCannon.MIN_HEIGHT,
                                  TileEntityCannon.MAX_HEIGHT));
        }
        this.cannon.markForUpdate();
    }
    
    @PeripheralMethod(12)
    public void setStrength(double strength) throws LuaException {
        if (!this.cannon.setStrength(strength)) {
            throw new LuaException("strength must be from 0 to maxStrength");
        }
        this.cannon.markForUpdate();
    }
    
    @Override
    public void attach(IComputerAccess computer) {}
    
    @Override
    public void detach(IComputerAccess computer) {}
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other instanceof CannonPeripheral) {
            return this.cannon.equals(((CannonPeripheral)other).cannon);
        } else {
            return false;
        }
    }
    
}
