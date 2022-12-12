package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.plugins.computercraft.pte.TileEntityPaintingPeripheral;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPaintingPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPainting
            && ForgeDirection.OPPOSITES[tile.getBlockMetadata()] == side) {
            return new TileEntityPaintingPeripheral((TileEntityPainting)tile);
        }
        return null;
    }
    
}
