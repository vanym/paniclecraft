package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public abstract class ItemMod3 extends Item {
    public ItemMod3() {
        super();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(DEF.MOD_ID + ":" + this.getName());
    }
    
    public String getName() {
        String unlocalizedName = this.getUnlocalizedName();
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
