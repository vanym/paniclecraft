package com.vanym.paniclecraft.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISidePaintingProvider {
    public Painting getPainting(int side);
    
    public Painting getPainting(int side, int x, int y);
    
    public int getPictureSide(Painting picture);
    
    public void markForUpdate();
    
    public void needUpdate();
    
    @SideOnly(Side.CLIENT)
    public void onWorldUnload();
}
