package com.vanym.paniclecraft.core;

import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabMod3 extends CreativeTabs {
    
    public Item iconitem;
    
    public CreativeTabMod3(String modid) {
        super(modid);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return DEF.MOD_NAME;
    }
    
    @Override
    public Item getTabIconItem() {
        return this.iconitem;
    }
    
}
