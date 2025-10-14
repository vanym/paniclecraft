package com.vanym.paniclecraft.core;

import com.vanym.paniclecraft.client.dev.ClientDevProxy;
import com.vanym.paniclecraft.core.dev.CommonDevProxy;
import com.vanym.paniclecraft.utils.DistUtils;

public abstract class CommonProxy implements IProxy {
    
    private final CommonDevProxy dev =
            DistUtils.call(()->ClientDevProxy::new, ()->CommonDevProxy::new);
    
    @Override
    public void preInit(ModConfig config) {
        this.dev.preInit(config);
    }
    
    @Override
    public void init(ModConfig config) {
        this.dev.init(config);
    }
    
    @Override
    public void postInit(ModConfig config) {
        this.dev.postInit(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        this.dev.configChanged(config);
    }
}
