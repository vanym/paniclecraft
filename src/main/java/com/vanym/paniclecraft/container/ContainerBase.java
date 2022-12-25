package com.vanym.paniclecraft.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class ContainerBase extends Container {
    
    protected void addPlayerInventorySlots(InventoryPlayer inv) {
        int i = -18;
        int j;
        int k;
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(inv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        
        for (j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j, 8 + j * 18, 161 + i));
        }
    }
}
