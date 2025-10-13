package com.vanym.paniclecraft.core;

import java.util.Map;

import com.vanym.paniclecraft.core.dev.DevProxy;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class CommonProxy implements IProxy {
    
    private final DevProxy dev = new DevProxy();
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        this.dev.init(configBuilders);
    }
}
