package com.vanym.paniclecraft.core;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vanym.paniclecraft.tileentity.TileEntityBase;

import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

public class SyncTileEntityUpdater {
    
    protected Thread serverThread;
    
    protected Set<TileEntityBase> tiles = ConcurrentHashMap.newKeySet(1);
    
    // called from Core
    public void serverStarted(FMLServerStartedEvent event) {
        this.serverThread = Thread.currentThread();
        this.tiles.clear();
    }
    
    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER || event.phase != Phase.START) {
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
        if (this.serverThread != Thread.currentThread()) {
            this.tiles.add(tile);
            return;
        }
        tile.markForUpdate();
    }
}
