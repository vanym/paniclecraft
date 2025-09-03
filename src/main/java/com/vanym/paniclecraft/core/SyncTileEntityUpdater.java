package com.vanym.paniclecraft.core;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vanym.paniclecraft.tileentity.TileEntityBase;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class SyncTileEntityUpdater {
    
    protected Set<TileEntityBase> tiles = ConcurrentHashMap.newKeySet(1);
    
    @SubscribeEvent
    protected void tick(TickEvent.WorldTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != Phase.START) {
            return;
        }
        for (Iterator<TileEntityBase> it = this.tiles.iterator(); it.hasNext();) {
            TileEntityBase tile = it.next();
            if (event.world == tile.getLevel()) {
                if (!tile.isRemoved()) {
                    tile.markForUpdate();
                }
                it.remove();
            }
        }
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) {
            return;
        }
        for (Iterator<TileEntityBase> it = this.tiles.iterator(); it.hasNext();) {
            TileEntityBase tile = it.next();
            if (event.getWorld() == tile.getLevel()) {
                it.remove();
            }
        }
    }
    
    public void safeMarkForUpdate(TileEntityBase tile) {
        if (tile.hasLevel() && tile.getLevel().thread != Thread.currentThread()) {
            this.tiles.add(tile);
            return;
        }
        tile.markForUpdate();
    }
}
