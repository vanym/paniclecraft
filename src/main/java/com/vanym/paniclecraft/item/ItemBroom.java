package com.vanym.paniclecraft.item;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemBroom extends ItemMod3 {
    
    protected final double defDis;
    
    public ItemBroom(int maxDam, double dis) {
        super();
        this.setUnlocalizedName("broom");
        this.setMaxStackSize(1);
        this.bFull3D = true;
        this.setMaxDamage(maxDam);
        this.defDis = dis;
    }
    
    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return true;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3) {
        if (!par2World.isRemote) {
            this.collectItems(par1ItemStack, par2World, par3);
        }
        return par1ItemStack;
    }
    
    public void collectItems(ItemStack par1ItemStack, World par2World, EntityPlayer par3) {
        float ent = 0.125F;
        int lvl = 0;
        // if(Core.enchantmentRange != null)
        // lvl =
        // EnchantmentHelper.getEnchantmentLevel(Core.enchantmentRange.effectId,
        // par1ItemStack);
        double dis = this.defDis * (1 + ent * lvl);
        @SuppressWarnings("rawtypes")
        List list =
                par2World.getEntitiesWithinAABB(EntityItem.class,
                                                AxisAlignedBB.getBoundingBox(par3.posX - (dis + 2),
                                                                             par3.posY - (dis + 2),
                                                                             par3.posZ - (dis + 2),
                                                                             par3.posX + (dis + 2),
                                                                             par3.posY + (dis + 2),
                                                                             par3.posZ + (dis +
                                                                                          2)));
        for (int g = 0; g < list.size(); g++) {
            EntityItem itemEntity = (EntityItem)list.get(g);
            if (par3.getDistance(itemEntity.posX, itemEntity.posY, itemEntity.posZ) <= dis
                && par3.canEntityBeSeen(itemEntity)) {
                int stackwas = itemEntity.getEntityItem().stackSize;
                itemEntity.onCollideWithPlayer(par3);
                par1ItemStack.damageItem(stackwas - itemEntity.getEntityItem().stackSize, par3);
            }
        }
        return;
    }
}
