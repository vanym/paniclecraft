package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.function.Supplier;

import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBroom extends ItemMod3 {
    
    public final Supplier<Integer> durability;
    public final Supplier<Double> distance;
    
    public ItemBroom(Supplier<Integer> durability, Supplier<Double> distance) {
        super(new Item.Properties().maxDamage(3072));
        this.setRegistryName("broom");
        this.durability = durability;
        this.distance = distance;
    }
    
    @Override
    public int getMaxDamage(ItemStack stack) {
        return this.durability.get();
    }
    
    @Override
    public boolean isDamageable() {
        return this.durability.get() > 0;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            PlayerEntity player,
            Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            this.collectItems(stack, world, player, hand);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
    
    protected void collectItems(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        final double distance = this.distance.get();
        AxisAlignedBB box = GeometryUtils.getPointBox(player.posX, player.posY, player.posZ)
                                         .grow(distance)
                                         .grow(2.0D);
        List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, box);
        list.stream()
            .filter(e->player.getDistance(e) <= distance)
            .filter(player::canEntityBeSeen)
            .forEach(e-> {
                int itemSizeWas = e.getItem().getCount();
                e.onCollideWithPlayer(player);
                int itemSize = !e.isAlive() ? 0 : e.getItem().getCount();
                stack.damageItem(itemSizeWas - itemSize, player, (p)->p.sendBreakAnimation(hand));
            });
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        return 200;
    }
}
