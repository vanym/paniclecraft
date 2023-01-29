package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
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
    public boolean isItemTool(ItemStack stack) {
        return true;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            this.collectItems(stack, world, player);
        }
        return stack;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void collectItems(ItemStack stack, World world, EntityPlayer player) {
        final double distance = this.distance;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                                                         player.posX, player.posY, player.posZ)
                                         .expand(distance, distance, distance)
                                         .expand(2, 2, 2);
        List list = world.getEntitiesWithinAABB(EntityItem.class, box);
        Stream<EntityItem> items = list.stream().map(EntityItem.class::cast);
        Stream<EntityItem> matches =
                items.filter(e->player.getDistance(e.posX, e.posY, e.posZ) <= distance)
                     .filter(player::canEntityBeSeen);
        matches.forEach(e-> {
            int itemSizeWas = e.getEntityItem().stackSize;
            e.onCollideWithPlayer(player);
            int itemSize = e.getEntityItem().stackSize;
            stack.damageItem(itemSizeWas - itemSize, player);
        });
    }
}
