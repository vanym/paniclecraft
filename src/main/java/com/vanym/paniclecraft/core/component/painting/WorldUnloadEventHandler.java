package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldUnloadEventHandler {
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        for (Object tile : event.world.loadedTileEntityList) {
            if (tile instanceof TileEntityPaintingContainer) {
                TileEntityPaintingContainer tilePC = (TileEntityPaintingContainer)tile;
                tilePC.onWorldUnload();
            }
        }
    }
}
