package com.vanym.paniclecraft.core;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vanym.paniclecraft.tileentity.TileEntityBase;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

public class SyncTileEntityUpdater {
    
    protected Set<TileEntityBase> tiles = ConcurrentHashMap.newKeySet(1);
    
    @SubscribeEvent
    protected void serverStarted(FMLServerStartedEvent event) {
        this.tiles.clear();
    }
    
    @SubscribeEvent
    protected void tick(TickEvent.WorldTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != Phase.START) {
            return;
        }
        for (Iterator<TileEntityBase> it = this.tiles.iterator(); it.hasNext();) {
            TileEntityBase tile = it.next();
            if (event.world == tile.getWorld()) {
                tile.markForUpdate();
                it.remove();
            }
        }
    }
    
    public void safeMarkForUpdate(TileEntityBase tile) {
        if (tile.hasWorld() && tile.getWorld().mainThread != Thread.currentThread()) {
            this.tiles.add(tile);
            return;
        }
        tile.markForUpdate();
    }
}
