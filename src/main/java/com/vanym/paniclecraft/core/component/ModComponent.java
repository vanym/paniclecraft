package com.vanym.paniclecraft.core.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;

public interface ModComponent {
    
    public static final String ENABLE_FLAG = "enable";
    
    @SideOnly(Side.CLIENT)
    public static final String CLIENT_RENDER = "clientRender";
    
    public void preInit(Configuration config);
    
    default public void init(Configuration config) {}
    
    default public void configChanged(Configuration config) {}
    
    @SideOnly(Side.CLIENT)
    default public void preInitClient(Configuration config) {}
    
    @SideOnly(Side.CLIENT)
    default public void initClient(Configuration config) {}
    
    @SideOnly(Side.CLIENT)
    default public void configChangedClient(Configuration config) {}
    
    public String getName();
    
    public boolean isEnabled();
}
