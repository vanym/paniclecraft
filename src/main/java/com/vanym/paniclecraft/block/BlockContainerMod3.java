package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public abstract class BlockContainerMod3 extends BlockContainer {
    
    public BlockContainerMod3(Material material) {
        super(material);
        this.setCreativeTab(Core.instance.tab);
    }
    
    @Override
    public String getUnlocalizedName() {
        return getUnlocalizedName(this.getRegistryName());
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public String getLocalizedName() {
        return net.minecraft.util.text.translation.//
                I18n.translateToLocal(this.getUnlocalizedName()).trim();
    }
    
    public static String getUnlocalizedName(ResourceLocation id) {
        return "block." + id.getResourceDomain() + "." + id.getResourcePath();
    }
}
