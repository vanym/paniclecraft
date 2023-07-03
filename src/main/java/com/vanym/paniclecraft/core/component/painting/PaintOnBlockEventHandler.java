package com.vanym.paniclecraft.core.component.painting;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class PaintOnBlockEventHandler {
    
    @SubscribeEvent
    public void entityCanUpdate(EntityEvent.CanUpdate event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPaintOnBlock) {
            ++entity.ticksExisted;
            entity.tick();
            event.setCanUpdate(false);
        }
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        World world = event.getWorld().getWorld();
        Stream<Entity> s;
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT) {
            ClientWorld clientWorld = (ClientWorld)world;
            s = StreamSupport.stream(clientWorld.getAllEntities().spliterator(), false);
        } else if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            ServerWorld serverWorld = (ServerWorld)world;
            s = serverWorld.getEntities();
        } else {
            return;
        }
        s.filter(EntityPaintOnBlock.class::isInstance)
         .map(EntityPaintOnBlock.class::cast)
         .forEach(EntityPaintOnBlock::onWorldUnload);
    }
    
    @SubscribeEvent
    public void blockNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        this.blockChange(event);
    }
    
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        this.blockChange(event);
    }
    
    protected void blockChange(BlockEvent event) {
        World world = event.getWorld().getWorld();
        EntityPaintOnBlock entityPOB = EntityPaintOnBlock.getEntity(world, event.getPos());
        if (entityPOB != null) {
            entityPOB.checkValidness();
        }
    }
}
