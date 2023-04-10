package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PaintOnBlockEventHandler {
    
    @SubscribeEvent
    public void entityCanUpdate(EntityEvent.CanUpdate event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPaintOnBlock) {
            ++entity.ticksExisted;
            entity.onUpdate();
            event.setCanUpdate(false);
        }
    }
    
    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new WorldEventListener(event.getWorld()));
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        event.getWorld().loadedEntityList.stream()
                                         .filter(EntityPaintOnBlock.class::isInstance)
                                         .map(EntityPaintOnBlock.class::cast)
                                         .forEach(EntityPaintOnBlock::onWorldUnload);
    }
    
    protected class WorldEventListener implements IWorldEventListener {
        
        protected final World world;
        
        public WorldEventListener(World world) {
            this.world = world;
        }
        
        @Override
        public void notifyBlockUpdate(
                World world,
                BlockPos pos,
                IBlockState oldState,
                IBlockState newState,
                int flags) {
            EntityPaintOnBlock entityPOB = EntityPaintOnBlock.getEntity(this.world, pos);
            if (entityPOB != null) {
                entityPOB.checkValidness();
            }
        }
        
        @Override
        public void onEntityAdded(Entity entity) {}
        
        @Override
        public void onEntityRemoved(Entity entity) {
            if (entity instanceof EntityPaintOnBlock) {
                EntityPaintOnBlock entityPOB = (EntityPaintOnBlock)entity;
                entityPOB.onEntityDestroy();
            }
        }
        
        @Override
        public void notifyLightSet(BlockPos pos) {}
        
        @Override
        public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}
        
        @Override
        public void playSoundToAllNearExcept(
                EntityPlayer player,
                SoundEvent sound,
                SoundCategory category,
                double x,
                double y,
                double z,
                float volume,
                float pitch) {}
        
        @Override
        public void playRecord(SoundEvent sound, BlockPos pos) {}
        
        @Override
        public void spawnParticle(
                int particleID,
                boolean ignoreRange,
                double xCoord,
                double yCoord,
                double zCoord,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                int... parameters) {}
        
        @Override
        public void spawnParticle(
                int id,
                boolean ignoreRange,
                boolean minParticles,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                int... parameters) {}
        
        @Override
        public void broadcastSound(int soundID, BlockPos pos, int data) {}
        
        @Override
        public void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {}
        
        @Override
        public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
    }
}
