package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Optional;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    
    public static IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
        int pside = side.getOpposite().getIndex();
        return Optional.ofNullable(WorldPictureProvider.PAINTING.getPicture(world, pos, pside))
                       .map(PaintingPeripheral::new)
                       .orElse(null);
    }
}
