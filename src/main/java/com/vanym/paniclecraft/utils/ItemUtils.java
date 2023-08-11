package com.vanym.paniclecraft.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    
    public static Optional<CompoundNBT> getTag(ItemStack stack) {
        return Optional.ofNullable(stack.getTag());
    }
    
    public static CompoundNBT getOrCreateTag(ItemStack stack) {
        return stack.getOrCreateTag();
    }
    
    public static void cleanTag(ItemStack stack) {
        if (getTag(stack).filter(CompoundNBT::isEmpty).isPresent()) {
            stack.setTag(null);
        }
    }
    
    public static Optional<CompoundNBT> getBlockEntityTag(ItemStack stack) {
        return getTag(stack);
    }
    
    public static CompoundNBT getOrCreateBlockEntityTag(ItemStack stack) {
        return getOrCreateTag(stack);
    }
    
    public static void cleanBlockEntityTag(ItemStack stack) {
        cleanTag(stack);
    }
}
