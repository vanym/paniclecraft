package com.vanym.paniclecraft.item;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemPaintRemover extends ItemPaintingTool {
    
    public static enum Type {
        REMOVER("paintingtool_remover"),
        SMALLREMOVER("paintingtool_remover_small");
        
        public final String id;
        
        Type(String id) {
            this.id = id;
        }
    }
    
    protected final Type type;
    
    public ItemPaintRemover(Type type) {
        super(new Item.Properties().maxStackSize(1));
        this.type = type;
        this.setRegistryName(type.id);
    }
    
    @Override
    public void fillItemGroup(ItemGroup creativetab, NonNullList<ItemStack> list) {
        if (!this.isInGroup(creativetab)) {
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
        list.add(new ItemStack(remover));
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
        switch (this.type) {
            case REMOVER:
                return getRadius(Core.instance.painting.config.removerRadiuses, picture);
            case SMALLREMOVER:
                return getRadius(Core.instance.painting.config.smallRemoverRadiuses, picture);
            default:
                return 0.1D;
        }
    }
    
}
