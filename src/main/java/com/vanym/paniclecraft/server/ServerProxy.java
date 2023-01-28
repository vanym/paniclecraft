package com.vanym.paniclecraft.server;

import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.ModConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    
    @Override
    public void preInit(ModConfig config) {}
    
    @Override
    public void init(ModConfig config) {}
    
    @Override
    public void postInit(ModConfig config) {}
    
    @Override
    public void configChanged(ModConfig config) {}
    
}
