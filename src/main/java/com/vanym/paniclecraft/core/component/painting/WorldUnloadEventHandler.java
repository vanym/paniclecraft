package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldUnloadEventHandler {
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        for (Object tile : event.getWorld().loadedTileEntityList) {
            if (tile instanceof TileEntityPaintingContainer) {
                TileEntityPaintingContainer tilePC = (TileEntityPaintingContainer)tile;
                tilePC.onWorldUnload();
            }
        }
    }
}
