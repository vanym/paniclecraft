package com.vanym.paniclecraft.core.component.painting;

import com.vanym.paniclecraft.Core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnvilCopyEventHandler {
    
    public static final AnvilCopyEventHandler instance = new AnvilCopyEventHandler();
    
    @SubscribeEvent
    public void anvilCopy(AnvilUpdateEvent event) {
        if (event.getLeft().isEmpty() || event.getRight().isEmpty()
            || Core.instance.painting.itemPainting != event.getLeft().getItem()
            || event.getLeft().getItem() != event.getRight().getItem()) {
            return;
        }
        int limit = Core.instance.painting.itemPainting.getItemStackLimit(event.getLeft());
        int amount = Math.min(limit - event.getLeft().getCount(), event.getRight().getCount());
        if (amount <= 0) {
            return;
        }
        ItemStack output = event.getLeft().copy();
        event.setMaterialCost(amount);
        output.grow(amount);
        event.setOutput(output);
        event.setCost(Math.max(1, amount * Core.instance.painting.config.copyOnAnvilCost));
    }
}
