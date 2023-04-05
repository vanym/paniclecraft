package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CannonPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityCannon) {
            return new CannonPeripheral((TileEntityCannon)tile);
        }
        return null;
    }
}
