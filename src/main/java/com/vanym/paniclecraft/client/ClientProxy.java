package com.vanym.paniclecraft.client;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.component.ModComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(Configuration config) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        for (ModComponent component : Core.instance.getComponents()) {
            component.preInitClient(config);
        }
    }
    
    @Override
    public void init(Configuration config) {
        for (ModComponent component : Core.instance.getComponents()) {
            component.initClient(config);
        }
    }
    
    @Override
    public void postInit(Configuration config) {}
    
    public static void deleteTexture(int texID) {
        GL11.glDeleteTextures(texID);
        // System.out.println(texID);
    }
}
