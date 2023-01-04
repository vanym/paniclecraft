package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.WorldEvent;

public class PaintOnBlockEventHandler {
    
    @SubscribeEvent
    public void entityCanUpdate(EntityEvent.CanUpdate event) {
        if (event.entity instanceof EntityPaintOnBlock) {
            ++event.entity.ticksExisted;
            event.entity.onUpdate();
            event.canUpdate = false;
        }
    }
    
    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        event.world.addWorldAccess(new WorldAccess(event.world));
    }
    
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void worldUnload(WorldEvent.Unload event) {
        event.world.loadedEntityList.stream()
                                    .filter(e->e instanceof EntityPaintOnBlock)
                                    .forEach(e-> {
                                        EntityPaintOnBlock ePOB = (EntityPaintOnBlock)e;
                                        ePOB.onWorldUnload();
                                    });
    }
    
    protected class WorldAccess implements IWorldAccess {
        
        protected final World world;
        
        public WorldAccess(World world) {
            this.world = world;
        }
        
        @Override
        public void markBlockForUpdate(int x, int y, int z) {}
        
        @Override
        public void markBlockForRenderUpdate(int x, int y, int z) {}
        
        @Override
        public void markBlockRangeForRenderUpdate(
                int minX,
                int minY,
                int minZ,
                int maxX,
                int maxY,
                int maxZ) {}
        
        @Override
        public void playSound(
                String soundName,
                double x,
                double y,
                double z,
                float volume,
                float pitch) {}
        
        @Override
        public void playSoundToNearExcept(
                EntityPlayer player,
                String soundName,
                double x,
                double y,
                double z,
                float volume,
                float pitch) {}
        
        @Override
        public void spawnParticle(
                String particleType,
                double x,
                double y,
                double z,
                double velX,
                double velY,
                double velZ) {}
        
        @Override
        public void onEntityCreate(Entity entity) {}
        
        @Override
        public void onEntityDestroy(Entity entity) {
            if (entity instanceof EntityPaintOnBlock) {
                EntityPaintOnBlock entityPOB = (EntityPaintOnBlock)entity;
                entityPOB.onEntityDestroy();
            }
        }
        
        @Override
        public void playRecord(String recordName, int x, int y, int z) {}
        
        @Override
        public void broadcastSound(int type, int x, int y, int z, int i5) {}
        
        @Override
        public void playAuxSFX(EntityPlayer player, int type, int x, int y, int z, int i5) {}
        
        @Override
        public void destroyBlockPartially(int entityID, int x, int y, int z, int p) {}
        
        @Override
        public void onStaticEntitiesChanged() {}
    }
}
