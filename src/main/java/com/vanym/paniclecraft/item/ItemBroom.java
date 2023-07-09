package com.vanym.paniclecraft.item;

import java.util.List;
import java.util.stream.Stream;

import com.vanym.paniclecraft.utils.GeometryUtils;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBroom extends ItemMod3 implements IFuelHandler {
    
    public final double distance;
    
    public ItemBroom(int maxDamage, double distance) {
        this.setRegistryName("broom");
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
        AxisAlignedBB box = GeometryUtils.getPointBox(player.posX, player.posY, player.posZ)
                                         .expand(distance, distance, distance)
                                         .expand(2.0D, 2.0D, 2.0D);
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
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel.getItem() instanceof ItemBroom) {
            return 200;
        }
        return 0;
    }
}
