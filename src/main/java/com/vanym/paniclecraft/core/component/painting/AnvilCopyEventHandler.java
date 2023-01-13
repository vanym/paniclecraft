package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.Core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.AnvilUpdateEvent;

public class AnvilCopyEventHandler {
    
    public static final AnvilCopyEventHandler instance = new AnvilCopyEventHandler();
    
    @SubscribeEvent
    public void anvilCopy(AnvilUpdateEvent event) {
        if (event.left == null || event.right == null
            || Core.instance.painting.itemPainting != event.left.getItem()
            || event.left.getItem() != event.right.getItem()) {
            return;
        }
        int limit = Core.instance.painting.itemPainting.getItemStackLimit(event.left);
        int amount = Math.min(limit - event.left.stackSize, event.right.stackSize);
        if (amount <= 0) {
            return;
        }
        event.output = event.left.copy();
        event.materialCost = amount;
        event.output.stackSize += event.materialCost;
        event.cost = Math.max(1, amount * Core.instance.painting.config.copyOnAnvilCost);
    }
}
