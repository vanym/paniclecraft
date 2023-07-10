package com.vanym.paniclecraft.utils;

import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class WorldUtils {
    
    public static Optional<TileEntity> getTileEntity(IBlockAccess world, int x, int y, int z) {
        return Optional.ofNullable(world.getTileEntity(x, y, z));
    }
    
    public static <T extends TileEntity> Optional<
        T> getTileEntity(IBlockAccess world, int x, int y, int z, Class<T> clazz) {
        return getTileEntity(world, x, y, z).filter(clazz::isInstance).map(clazz::cast);
    }
}
