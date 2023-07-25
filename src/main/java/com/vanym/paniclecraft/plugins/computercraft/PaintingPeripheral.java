package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Optional;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PaintingPeripheral extends PicturePeripheral {
    
    protected final Picture picture;
    
    public PaintingPeripheral(Picture picture) {
        this.picture = picture;
    }
    
    @Override
    public String getType() {
        return "painting";
    }
    
    @Override
    public boolean equals(IPeripheral other) {
        if (other != null && other instanceof PaintingPeripheral) {
            PaintingPeripheral pp = (PaintingPeripheral)other;
            return this.picture.equals(pp.picture);
        }
        return false;
    }
    
    @Override
    protected Picture getPicture() {
        return this.picture;
    }
    
    public static IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        int pside = ForgeDirection.OPPOSITES[side];
        return Optional.ofNullable(WorldPictureProvider.PAINTING.getPicture(world, x, y, z, pside))
                       .map(PaintingPeripheral::new)
                       .orElse(null);
    }
}
