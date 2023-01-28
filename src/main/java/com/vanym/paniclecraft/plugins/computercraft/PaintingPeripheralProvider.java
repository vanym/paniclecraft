package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PaintingPeripheralProvider implements IPeripheralProvider {
    
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        int pside = ForgeDirection.OPPOSITES[side];
        Picture picture = WorldPictureProvider.PAINTING.getPicture(world, x, y, z, pside);
        if (picture != null) {
            return new PaintingPeripheral(picture);
        }
        return null;
    }
}
