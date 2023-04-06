package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemPaintRemover extends ItemPaintingTool {
    
    protected static final int DAMAGE_REMOVER = 0;
    protected static final int DAMAGE_SMALLREMOVER = 1;
    
    protected static final Map<Integer, String> SUBTYPES;
    static {
        SUBTYPES = new HashMap<>();
        SUBTYPES.put(DAMAGE_REMOVER, "paintingtool_remover");
        SUBTYPES.put(DAMAGE_SMALLREMOVER, "paintingtool_remover_small");
    }
    
    public ItemPaintRemover() {
        super();
        this.setUnlocalizedName("paintremover");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public ItemStack getRemover() {
        return new ItemStack(this, 1, DAMAGE_REMOVER);
    }
    
    public ItemStack getSmallRemover() {
        return new ItemStack(this, 1, DAMAGE_SMALLREMOVER);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        String name = SUBTYPES.get(damage);
        if (name == null) {
            return this.getUnlocalizedName() + damage;
        }
        return "item." + name;
    }
    
    @Override
    public void getSubItems(CreativeTabs creativetab, NonNullList<ItemStack> list) {
        if (!this.isInCreativeTab(creativetab)) {
            return;
        }
        if (!Core.instance.painting.clientConfig.forceUnhidePaintRemover
            && !Core.instance.painting.config.allowPaintOnBlock
            && creativetab != null
            && creativetab == Core.instance.tab) {
            // This item is used to remove paint from block,
            // so hide it if painting on block is not allowed
            return;
        }
        ItemPaintRemover remover = this;
        list.add(remover.getRemover());
        list.add(remover.getSmallRemover());
    }
    
    @Override
    public PaintingToolType getPaintingToolType(ItemStack itemStack) {
        return PaintingToolType.REMOVER;
    }
    
    @Override
    public Color getPaintingToolColor(ItemStack itemStack) {
        return null;
    }
    
    @Override
    public double getPaintingToolRadius(ItemStack itemStack, IPictureSize picture) {
        Double tagRadius = getTagRadius(itemStack);
        if (tagRadius != null) {
            return tagRadius;
        }
        switch (itemStack.getItemDamage()) {
            case DAMAGE_REMOVER:
                return getRadius(Core.instance.painting.config.removerRadiuses, picture);
            case DAMAGE_SMALLREMOVER:
                return getRadius(Core.instance.painting.config.smallRemoverRadiuses, picture);
            default:
                return 0.1D;
        }
    }
    
}
