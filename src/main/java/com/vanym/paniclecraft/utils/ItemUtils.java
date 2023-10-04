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
    
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    
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
        return Optional.ofNullable(stack.getChildTag(BLOCK_ENTITY_TAG));
    }
    
    public static CompoundNBT getOrCreateBlockEntityTag(ItemStack stack) {
        return stack.getOrCreateChildTag(BLOCK_ENTITY_TAG);
    }
    
    public static void cleanBlockEntityTag(ItemStack stack) {
        getTag(stack).filter(t->t.contains(BLOCK_ENTITY_TAG, 10))
                     .filter(t->t.getCompound(BLOCK_ENTITY_TAG).isEmpty())
                     .ifPresent(t->t.remove(BLOCK_ENTITY_TAG));
        cleanTag(stack);
    }
}
