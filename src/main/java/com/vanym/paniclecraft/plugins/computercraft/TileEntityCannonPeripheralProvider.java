package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.plugins.computercraft.pte.TileEntityCannonPeripheral;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityCannonPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCannon) {
            return new TileEntityCannonPeripheral((TileEntityCannon)tile);
        }
        return null;
    }
    
}
