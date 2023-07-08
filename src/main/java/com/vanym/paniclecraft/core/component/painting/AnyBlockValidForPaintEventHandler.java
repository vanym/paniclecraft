package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnyBlockValidForPaintEventHandler {
    
    public static final AnyBlockValidForPaintEventHandler instance =
            new AnyBlockValidForPaintEventHandler();
    
    @SubscribeEvent
    public void validCheck(EntityPaintOnBlock.BlockSideValidForPaint event) {
        if (!event.air && !event.liquid) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
