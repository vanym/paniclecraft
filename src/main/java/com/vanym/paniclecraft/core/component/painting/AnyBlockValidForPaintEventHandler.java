package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
