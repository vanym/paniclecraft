package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.IModComponent;

import dan200.computercraft.api.ComputerCraftAPI;

public class ComputerCraftPlugin implements IModComponent {
    
    protected static ComputerCraftPlugin instance;
    
    public CannonPeripheralProvider tileEntityCannonPeripheralProvider;
    
    public ChessDeskPeripheralProvider tileEntityChessDeskPeripheralProvider;
    
    public PaintingPeripheralProvider tileEntityPaintingPeripheralProvider;
    public PaintingFramePeripheralProvider tileEntityPaintingFramePeripheralProvider;
    public TurtlePaintBrush turtlePaintBrush;
    
    @Override
    public void preInit(ModConfig config) {
        if (config.getBoolean("peripheralCannon", this.getName(), true, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityCannonPeripheralProvider =
                    new CannonPeripheralProvider());
        }
        if (config.getBoolean("peripheralChessDesk", this.getName(), true, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityChessDeskPeripheralProvider =
                    new ChessDeskPeripheralProvider());
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
