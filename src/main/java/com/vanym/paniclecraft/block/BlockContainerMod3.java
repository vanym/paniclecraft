package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class BlockContainerMod3 extends BlockContainer {
    
    public BlockContainerMod3(Material material) {
        super(material);
        this.setCreativeTab(Core.instance.tab);
    }
    
    @Override
    public Block setUnlocalizedName(String name) {
        this.setRegistryName(DEF.MOD_ID, name);
        return super.setUnlocalizedName(name);
    }
    
    public String getName() {
        String unlocalizedName = this.getUnlocalizedName();
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
