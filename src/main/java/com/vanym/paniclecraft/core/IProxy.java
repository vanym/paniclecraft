package com.vanym.paniclecraft.core;

public interface IProxy {
    public void preInit(ModConfig config);
    
    public void init(ModConfig config);
    
    public void postInit(ModConfig config);
    
    public void configChanged(ModConfig config);
}
