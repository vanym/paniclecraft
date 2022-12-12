package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.plugins.computercraft.t.TurtlePaintBrush;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.config.Configuration;

public class ComputerCraftPlugin {
    
    public static TurtlePaintBrush turtlePaintBrush;
    public static TileEntityCannonPeripheralProvider tileEntityCannonPeripheralProvider;
    public static TileEntityPaintingPeripheralProvider tileEntityPaintingPeripheralProvider;
    
    public static void init(Configuration config) {
        if (config.getBoolean("Peripheral_Painting", "ComputerCraft", false, "")) {
            tileEntityPaintingPeripheralProvider = new TileEntityPaintingPeripheralProvider();
            ComputerCraftAPI.registerPeripheralProvider(tileEntityPaintingPeripheralProvider);
        }
        if (config.getBoolean("Peripheral_Cannon", "ComputerCraft", true, "")) {
            tileEntityCannonPeripheralProvider = new TileEntityCannonPeripheralProvider();
            ComputerCraftAPI.registerPeripheralProvider(tileEntityPaintingPeripheralProvider);
        }
        if (config.getBoolean("TurtleUpgrade_PaintBrush", "ComputerCraft", true, "")) {
            turtlePaintBrush = new TurtlePaintBrush();
            ComputerCraftAPI.registerTurtleUpgrade(turtlePaintBrush);
        }
    }
}
