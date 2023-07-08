package com.vanym.paniclecraft.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

public abstract class ContainerBase extends Container {
    
    protected ContainerBase(ContainerType<?> type, int id) {
        super(type, id);
    }
    
    protected void addPlayerInventorySlots(PlayerInventory inv) {
        int i = -18;
        int j;
        int k;
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(inv, j, 8 + j * 18, 161 + i));
        }
    }
}
