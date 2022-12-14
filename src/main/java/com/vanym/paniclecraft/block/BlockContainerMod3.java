package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public abstract class BlockContainerMod3 extends BlockContainer {
    
    public BlockContainerMod3(Material material) {
        super(material);
        this.setCreativeTab(Core.instance.tab);
    }
    
    @Override
    public String getUnlocalizedName() {
        // return String.format("tile.%s%s", DEF.MOD_ID.toLowerCase() + ":",
        // getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
        return String.format("tile.%s",
                             this.getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(String.format(DEF.MOD_ID +
            ":%s", this.getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }
    
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
