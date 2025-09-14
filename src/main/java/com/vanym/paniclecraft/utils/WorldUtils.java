package com.vanym.paniclecraft.utils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WorldUtils {
    
    public static Optional<TileEntity> getTileEntity(IBlockReader world, BlockPos pos) {
        return Optional.ofNullable(world.getBlockEntity(pos));
    }
    
    public static <T extends TileEntity> Optional<
        T> getTileEntity(IBlockReader world, BlockPos pos, Class<T> clazz) {
        return getTileEntity(world, pos).filter(clazz::isInstance).map(clazz::cast);
    }
    
    public static Stream<Entity> getEntities(World world) {
        if (world instanceof ServerWorld) {
            return ((ServerWorld)world).getEntities();
        }
        return Optional.ofNullable(SideUtils.ccall(()->new Callable<Stream<Entity>>() {
            @Override
            public Stream<Entity> call() throws Exception {
                if (!(world instanceof ClientWorld)) {
                    return null;
                }
                ClientWorld clientWorld = (ClientWorld)world;
                return StreamSupport.stream(clientWorld.entitiesForRendering().spliterator(),
                                            false);
            }
        })).orElseGet(Stream::empty);
    }
}
