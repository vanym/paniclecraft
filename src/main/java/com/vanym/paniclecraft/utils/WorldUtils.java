package com.vanym.paniclecraft.utils;

import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class WorldUtils {
    
    public static Optional<TileEntity> getTileEntity(IBlockAccess world, BlockPos pos) {
        return Optional.ofNullable(world.getTileEntity(pos));
    }
    
    public static <T extends TileEntity> Optional<
        T> getTileEntity(IBlockAccess world, BlockPos pos, Class<T> clazz) {
        return getTileEntity(world, pos).filter(clazz::isInstance).map(clazz::cast);
    }
}
