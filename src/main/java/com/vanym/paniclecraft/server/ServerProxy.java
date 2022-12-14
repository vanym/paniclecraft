package com.vanym.paniclecraft.server;

import com.vanym.paniclecraft.core.CommonProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    
    @Override
    public void preInit(Configuration config) {
    }
    
    @Override
    public void init(Configuration config) {
    }
    
    @Override
    public void postInit(Configuration config) {
    }
    
}
