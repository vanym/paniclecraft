package com.vanym.paniclecraft.client.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class IconFlippedBugFixed extends IconFlipped {
    
    protected final IIcon baseIcon;
    protected final boolean flipV;
    
    public IconFlippedBugFixed(IIcon icon, boolean flipU, boolean flipV) {
        super(icon, flipU, flipV);
        this.baseIcon = icon;
        this.flipV = flipV;
    }
    
    @Override
    public float getMinV() {
        return this.flipV ? this.baseIcon.getMaxV() : this.baseIcon.getMinV();
    }
}
