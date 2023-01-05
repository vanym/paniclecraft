package com.vanym.paniclecraft.item;

import java.awt.Color;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemPaintRemover extends ItemPaintingTool {
    
    protected static final int DAMAGE_REMOVER = 0;
    protected static final int DAMAGE_SMALLREMOVER = 1;
    
    @SideOnly(Side.CLIENT)
    public IIcon iconRemover;
    @SideOnly(Side.CLIENT)
    public IIcon iconSmallRemover;
    
    public ItemPaintRemover() {
        super();
        this.setUnlocalizedName("paintRemover");
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
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName() + itemStack.getItemDamage();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        switch (damage) {
            default:
            case DAMAGE_REMOVER:
                return this.iconRemover;
            case DAMAGE_SMALLREMOVER:
                return this.iconSmallRemover;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        final String PREFIX = DEF.MOD_ID + ":" + this.getName();
        this.iconRemover = iconRegister.registerIcon(PREFIX);
        this.iconSmallRemover = iconRegister.registerIcon(PREFIX + "_small");
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativetab, List list) {
        if (!(item instanceof ItemPaintRemover)) {
            return;
        }
        ItemPaintRemover remover = (ItemPaintRemover)item;
        if (!Core.instance.painting.clientConfig.forceUnhidePaintRemover
            && !Core.instance.painting.config.allowPaintOnBlock
            && creativetab != null
            && creativetab == Core.instance.tab) {
            // This item is used to remove paint from block,
            // so hide it if painting on block is not allowed
            return;
        }
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
                return getRadius(Core.instance.painting.config.brushRadiuses, picture);
            case DAMAGE_SMALLREMOVER:
                return getRadius(Core.instance.painting.config.smallBrushRadiuses, picture);
            default:
                return 0.1D;
        }
    }
    
}
