package com.vanym.paniclecraft.client;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.IModComponent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(ModConfig config) {
        for (IModComponent component : Core.instance.getComponents()) {
            component.preInitClient(config);
        }
    }
    
    @Override
    public void init(ModConfig config) {
        for (IModComponent component : Core.instance.getComponents()) {
            component.initClient(config);
        }
    }
    
    @Override
    public void postInit(ModConfig config) {}
    
    @Override
    public void configChanged(ModConfig config) {
        for (IModComponent component : Core.instance.getComponents()) {
            component.configChangedClient(config);
        }
    }
}
