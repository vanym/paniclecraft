package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PaintingFramePeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
            ForgeDirection pside = ForgeDirection.getOrientation(side).getOpposite();
            return new PaintingFramePeripheral(tilePF, pside);
        }
        return null;
    }
}
