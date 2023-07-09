package com.vanym.paniclecraft.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.StatCollector;

public abstract class BlockContainerMod3 extends BlockContainer implements IMod3Block {
    
    public BlockContainerMod3(Material material) {
        super(material);
    }
    
    @Override
    public String getUnlocalizedName() {
        return IMod3Block.getUnlocalizedName(this.getRegistryName());
    }
    
    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName()).trim();
    }
}
