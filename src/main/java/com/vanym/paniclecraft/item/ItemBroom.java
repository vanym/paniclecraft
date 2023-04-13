package com.vanym.paniclecraft.item;

import java.util.List;

import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBroom extends ItemMod3 {
    
    public final double distance;
    
    public ItemBroom(int maxDamage, double distance) {
        this.setUnlocalizedName("broom");
        this.setFull3D();
        this.setMaxDamage(maxDamage);
        this.setMaxStackSize(1);
        this.distance = distance;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World world,
            EntityPlayer player,
            EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            this.collectItems(stack, world, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    protected void collectItems(ItemStack stack, World world, EntityPlayer player) {
        final double distance = this.distance;
        AxisAlignedBB box = GeometryUtils.getPointBox(player.posX, player.posY, player.posZ)
                                         .grow(distance)
                                         .grow(2.0D);
        List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, box);
        list.stream()
            .filter(e->player.getDistance(e.posX, e.posY, e.posZ) <= distance)
            .filter(player::canEntityBeSeen)
            .forEach(e-> {
                int itemSizeWas = e.getItem().getCount();
                e.onCollideWithPlayer(player);
                int itemSize = e.isDead ? 0 : e.getItem().getCount();
                stack.damageItem(itemSizeWas - itemSize, player);
            });
    }
    
    @Override
    public int getItemBurnTime(ItemStack fuel) {
        return 200;
    }
}
