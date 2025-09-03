package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.function.Supplier;

import com.vanym.paniclecraft.utils.GeometryUtils;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBroom extends Item {
    
    public final Supplier<Integer> durability;
    public final Supplier<Double> distance;
    
    public ItemBroom(Supplier<Integer> durability, Supplier<Double> distance) {
        super(Props.create().durability(3072));
        this.setRegistryName("broom");
        this.durability = durability;
        this.distance = distance;
    }
    
    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.durability.get();
    }
    
    @Override
    public boolean canBeDepleted() {
        return this.durability.get() > 0;
    }
    
    @Override
    public ActionResult<ItemStack> use(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            this.collectItems(stack, world, player, hand);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
    
    protected void collectItems(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        final double distance = this.distance.get();
        AxisAlignedBB box = GeometryUtils.getPointBox(player.getX(), player.getY(), player.getZ())
                                         .inflate(distance)
                                         .inflate(2.0D);
        List<ItemEntity> list = world.getEntitiesOfClass(ItemEntity.class, box);
        list.stream()
            .filter(e->player.distanceTo(e) <= distance)
            .filter(player::canSee)
            .forEach(e-> {
                int itemSizeWas = e.getItem().getCount();
                e.playerTouch(player);
                int itemSize = !e.isAlive() ? 0 : e.getItem().getCount();
                stack.hurtAndBreak(itemSizeWas - itemSize, player, ItemUtils.onBroken(hand));
            });
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        return 200;
    }
}
