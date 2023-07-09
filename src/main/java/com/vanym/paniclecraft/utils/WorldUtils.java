package com.vanym.paniclecraft.utils;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class WorldUtils {
    
    public static Stream<Entity> getEntities(World world) {
        if (EffectiveSide.get().isClient() && world instanceof ClientWorld) {
            ClientWorld clientWorld = (ClientWorld)world;
            return StreamSupport.stream(clientWorld.getAllEntities().spliterator(), false);
        } else if (world instanceof ServerWorld) {
            return ((ServerWorld)world).getEntities();
        } else {
            return Stream.empty();
        }
    }
}
