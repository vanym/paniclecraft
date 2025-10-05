package com.vanym.paniclecraft.core;

import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class CommonProxy implements IProxy {
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {}
}
