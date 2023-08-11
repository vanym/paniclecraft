package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.api.event.BlockSidePaintabilityEvent;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnyBlockPaintableEventHandler {
    
    public static final AnyBlockPaintableEventHandler instance =
            new AnyBlockPaintableEventHandler();
    
    @SubscribeEvent
    public void validCheck(BlockSidePaintabilityEvent event) {
        if (!event.isAir() && !event.isLiquid()) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
