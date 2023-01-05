package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AnyBlockValidForPaintEventHandler {
    
    public static final AnyBlockValidForPaintEventHandler instance =
            new AnyBlockValidForPaintEventHandler();
    
    @SubscribeEvent
    public void validCheck(EntityPaintOnBlock.BlockValidForPaint event) {
        if (!event.air && !event.liquid) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
