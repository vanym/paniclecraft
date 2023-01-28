package com.vanym.paniclecraft.client;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.ModComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(ModConfig config) {
        for (ModComponent component : Core.instance.getComponents()) {
            component.preInitClient(config);
        }
    }
    
    @Override
    public void init(ModConfig config) {
        for (ModComponent component : Core.instance.getComponents()) {
            component.initClient(config);
        }
    }
    
    @Override
    public void postInit(ModConfig config) {}
    
    @Override
    public void configChanged(ModConfig config) {
        for (ModComponent component : Core.instance.getComponents()) {
            component.configChangedClient(config);
        }
    }
    
    public static void deleteTexture(int texID) {
        GL11.glDeleteTextures(texID);
        // System.out.println(texID);
    }
}
