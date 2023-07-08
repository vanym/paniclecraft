package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintingFramePeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame tilePF = (TileEntityPaintingFrame)tile;
            return new PaintingFramePeripheral(tilePF, side.getOpposite());
        }
        return null;
    }
}
