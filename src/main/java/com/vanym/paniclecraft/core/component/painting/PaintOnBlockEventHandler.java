package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.WorldUtils;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        WorldUtils.getEntities(world)
                  .filter(EntityPaintOnBlock.class::isInstance)
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
