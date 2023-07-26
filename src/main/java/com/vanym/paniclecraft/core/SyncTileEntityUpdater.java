package com.vanym.paniclecraft.core;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vanym.paniclecraft.tileentity.TileEntityBase;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.event.world.WorldEvent;

public class SyncTileEntityUpdater {
    
    protected Thread serverThread;
    
    protected Set<TileEntityBase> tiles = ConcurrentHashMap.newKeySet(1);
    
    // called from Core
    public void serverStarted(FMLServerStartedEvent event) {
        this.serverThread = Thread.currentThread();
    }
    
    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER || event.phase != Phase.START) {
            return;
        }
        for (Iterator<TileEntityBase> it = this.tiles.iterator(); it.hasNext();) {
            TileEntityBase tile = it.next();
            if (event.world == tile.getWorldObj()) {
                if (!tile.isInvalid()) {
                    tile.markForUpdate();
                }
                it.remove();
            }
        }
    }
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if (this.serverThread != Thread.currentThread()) {
            return;
        }
        for (Iterator<TileEntityBase> it = this.tiles.iterator(); it.hasNext();) {
            TileEntityBase tile = it.next();
            if (event.world == tile.getWorldObj()) {
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
