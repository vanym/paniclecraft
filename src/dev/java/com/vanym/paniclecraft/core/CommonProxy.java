package com.vanym.paniclecraft.core;

import java.util.Map;

import com.vanym.paniclecraft.client.dev.ClientDevProxy;
import com.vanym.paniclecraft.core.dev.CommonDevProxy;
import com.vanym.paniclecraft.utils.DistUtils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class CommonProxy implements IProxy {
    
    private final CommonDevProxy dev =
            DistUtils.call(()->ClientDevProxy::new, ()->CommonDevProxy::new);
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        this.dev.init(configBuilders);
    }
}
