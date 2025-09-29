package com.vanym.paniclecraft.core;

public abstract class CommonProxy implements IProxy {
    
    @Override
    public void preInit(ModConfig config) {}
    
    @Override
    public void init(ModConfig config) {}
    
    @Override
    public void postInit(ModConfig config) {}
    
    @Override
    public void configChanged(ModConfig config) {}
}
