package com.vanym.paniclecraft.utils;

import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemUtils {
    
    public static <T extends LivingEntity> Consumer<T> onBroken(EquipmentSlotType slotType) {
        return e->e.sendBreakAnimation(slotType);
    }
    
    public static <T extends LivingEntity> Consumer<T> onBroken(Hand hand) {
        return e->e.sendBreakAnimation(hand);
    }
    
    public static <T extends LivingEntity> Consumer<T> onBroken(ItemStack stack) {
        if (stack == ItemStack.EMPTY) {
            return e-> {};
        }
        return e->Arrays.stream(EquipmentSlotType.values())
                        .filter(slot->stack == e.getItemStackFromSlot(slot))
                        .findAny()
                        .ifPresent(e::sendBreakAnimation);
    }
}
