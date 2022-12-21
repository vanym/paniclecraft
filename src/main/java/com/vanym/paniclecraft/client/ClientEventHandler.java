package com.vanym.paniclecraft.client;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.plugins.computercraft.ComputerCraftPlugin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    
    @SubscribeEvent
    public void textureStitchEvent(TextureStitchEvent.Pre event) {
        if (Loader.isModLoaded("ComputerCraft")) {
            if (event.map.getTextureType() == 0) {
                if (ComputerCraftPlugin.turtlePaintBrush != null) {
                    ComputerCraftPlugin.turtlePaintBrush.iconLeft =
                            event.map.registerIcon(DEF.MOD_ID + ":" + "turtle.PaintBrush.left");
                    ComputerCraftPlugin.turtlePaintBrush.iconRight =
                            event.map.registerIcon(DEF.MOD_ID + ":" + "turtle.PaintBrush.right");
                }
            }
        }
    }
}
