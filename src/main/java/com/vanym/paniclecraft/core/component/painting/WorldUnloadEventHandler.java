package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldUnloadEventHandler {
    
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if (!World.class.isInstance(event.getWorld())) {
            return;
        }
        World world = (World)event.getWorld();
        for (TileEntity tile : world.blockEntityList) {
            if (tile instanceof TileEntityPaintingContainer) {
                TileEntityPaintingContainer tilePC = (TileEntityPaintingContainer)tile;
                tilePC.onWorldUnload();
            }
        }
    }
}
