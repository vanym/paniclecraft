package com.vanym.paniclecraft.core;

import java.util.Map;

import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface IProxy {
    
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders);
    
    public CommandMod3 getCommand();
    
    public CommandDev getDevCommand();
}
