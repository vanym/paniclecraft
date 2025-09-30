package com.vanym.paniclecraft.core;

import java.util.Optional;

import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;

public interface IProxy {
    public void preInit(ModConfig config);
    
    public void init(ModConfig config);
    
    public void postInit(ModConfig config);
    
    public void configChanged(ModConfig config);
    
    public CommandMod3 getCommand();
    
    public Optional<CommandDev> getDevCommand();
}
