package com.vanym.paniclecraft.item;

import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemMod3 extends Item {
    public ItemMod3() {
        super();
    }
    
    @Override
    public String getUnlocalizedName() {
        // return String.format("item.%s%s", DEF.MOD_ID.toLowerCase() + ":",
        // getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
        return String.format("item.%s",
                             this.getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(DEF.MOD_ID + ":" +
            this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }
    
    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
