package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Map;
import java.util.function.Supplier;

import com.vanym.paniclecraft.core.component.IModComponent;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ComputerCraftPlugin implements IModComponent {
    
    protected static ComputerCraftPlugin instance;
    
    public CannonPeripheralProvider tileEntityCannonPeripheralProvider;
    protected Supplier<Boolean> peripheralCannon;
    
    public ChessDeskPeripheralProvider tileEntityChessDeskPeripheralProvider;
    protected Supplier<Boolean> peripheralChessDesk;
    
    public PaintingPeripheralProvider tileEntityPaintingPeripheralProvider;
    protected Supplier<Boolean> peripheralPainting;
    public PaintingFramePeripheralProvider tileEntityPaintingFramePeripheralProvider;
    protected Supplier<Boolean> peripheralPaintingFrame;
    public TurtlePaintBrush turtlePaintBrush;
    protected Supplier<Boolean> turtleUpgradePaintBrush;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        serverBuilder.push(this.getName());
        ForgeConfigSpec.BooleanValue peripheralCannon =
                serverBuilder.define("peripheralCannon", true);
        this.peripheralCannon = ()->peripheralCannon.get();
        ForgeConfigSpec.BooleanValue peripheralChessDesk =
                serverBuilder.define("peripheralChessDesk", true);
        this.peripheralChessDesk = ()->peripheralChessDesk.get();
        ForgeConfigSpec.BooleanValue peripheralPainting =
                serverBuilder.define("peripheralPainting", false);
        this.peripheralPainting = ()->peripheralPainting.get();
        ForgeConfigSpec.BooleanValue peripheralPaintingFrame =
                serverBuilder.define("peripheralPaintingFrame", false);
        this.peripheralPaintingFrame = ()->peripheralPaintingFrame.get();
        ForgeConfigSpec.BooleanValue turtleUpgradePaintBrush =
                serverBuilder.define("turtleUpgradePaintBrush", true);
        this.turtleUpgradePaintBrush = ()->turtleUpgradePaintBrush.get();
        serverBuilder.pop();
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        if (this.peripheralCannon.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityCannonPeripheralProvider =
                    new CannonPeripheralProvider());
        }
        if (this.peripheralChessDesk.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityChessDeskPeripheralProvider =
                    new ChessDeskPeripheralProvider());
        }
        if (this.peripheralPainting.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityPaintingPeripheralProvider =
                    new PaintingPeripheralProvider());
        }
        if (this.peripheralPaintingFrame.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.tileEntityPaintingFramePeripheralProvider =
                    new PaintingFramePeripheralProvider());
        }
        if (this.turtleUpgradePaintBrush.get()) {
            this.turtlePaintBrush = new TurtlePaintBrush();
            ComputerCraftAPI.registerTurtleUpgrade(this.turtlePaintBrush);
        }
    }
    
    @Override
    public String getName() {
        return "computercraftplugin";
    }
    
    public static ComputerCraftPlugin instance() {
        if (instance == null) {
            instance = new ComputerCraftPlugin();
        }
        return instance;
    }
}
