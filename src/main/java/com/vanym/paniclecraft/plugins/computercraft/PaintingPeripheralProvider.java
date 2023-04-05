package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintingPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
        int pside = side.getOpposite().getIndex();
        Picture picture = WorldPictureProvider.PAINTING.getPicture(world, pos.getX(), pos.getY(),
                                                                   pos.getZ(), pside);
        if (picture != null) {
            return new PaintingPeripheral(picture);
        }
        return null;
    }
}
