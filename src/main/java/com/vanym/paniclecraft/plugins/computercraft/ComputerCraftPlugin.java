package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.client.ModConfig;
import com.vanym.paniclecraft.core.component.ModComponent;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.MinecraftForge;

public class ComputerCraftPlugin implements ModComponent {
    
    protected static ComputerCraftPlugin instance;
    
    public TileEntityCannonPeripheralProvider tileEntityCannonPeripheralProvider;
    
    public PaintingPeripheralProvider tileEntityPaintingPeripheralProvider;
    public PaintingFramePeripheralProvider tileEntityPaintingFramePeripheralProvider;
    public TurtlePaintBrush turtlePaintBrush;
    
    @Override
    public void preInit(ModConfig config) {
        if (config.getBoolean("peripheralCannon", this.getName(), true, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityCannonPeripheralProvider =
                    new TileEntityCannonPeripheralProvider());
        }
        if (config.getBoolean("peripheralPainting", this.getName(), false, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityPaintingPeripheralProvider =
                    new PaintingPeripheralProvider());
        }
        if (config.getBoolean("peripheralPaintingFrame", this.getName(), false, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityPaintingFramePeripheralProvider =
                    new PaintingFramePeripheralProvider());
        }
        if (config.getBoolean("turtleUpgradePaintBrush", this.getName(), true, "")) {
            this.turtlePaintBrush = new TurtlePaintBrush();
            ComputerCraftAPI.registerTurtleUpgrade(this.turtlePaintBrush);
            MinecraftForge.EVENT_BUS.register(this.turtlePaintBrush);
        }
    }
    
    @Override
    public String getName() {
        return "computercraftplugin";
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public static ComputerCraftPlugin instance() {
        if (instance == null) {
            instance = new ComputerCraftPlugin();
        }
        return instance;
    }
}
