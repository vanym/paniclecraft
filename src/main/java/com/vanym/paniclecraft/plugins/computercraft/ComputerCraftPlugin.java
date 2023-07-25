package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.WorldUtils;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ComputerCraftPlugin implements IModComponent {
    
    protected static ComputerCraftPlugin instance;
    
    protected final IPeripheralProvider cannonPeripheralProvider =
            makeProvider(TileEntityCannon.class, CannonPeripheral::new);
    protected Supplier<Boolean> peripheralCannon;
    
    protected final IPeripheralProvider chessDeskPeripheralProvider =
            makeProvider(TileEntityChessDesk.class, ChessDeskPeripheral::new);
    protected Supplier<Boolean> peripheralChessDesk;
    
    protected final IPeripheralProvider paintingPeripheralProvider =
            PaintingPeripheral::getPeripheral;
    protected Supplier<Boolean> peripheralPainting;
    protected final IPeripheralProvider paintingFramePeripheralProvider =
            PaintingFramePeripheral::getPeripheral;
    protected Supplier<Boolean> peripheralPaintingFrame;
    protected final TurtlePaintBrush turtlePaintBrush = new TurtlePaintBrush();
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
            ComputerCraftAPI.registerPeripheralProvider(this.cannonPeripheralProvider);
        }
        if (this.peripheralChessDesk.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.chessDeskPeripheralProvider);
        }
        if (this.peripheralPainting.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.paintingPeripheralProvider);
        }
        if (this.peripheralPaintingFrame.get()) {
            ComputerCraftAPI.registerPeripheralProvider(this.paintingFramePeripheralProvider);
        }
        if (this.turtleUpgradePaintBrush.get()) {
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
    
    protected static <T extends TileEntity> IPeripheralProvider makeProvider(
            Class<T> tile,
            Function<T, IPeripheral> creator) {
        return (world, pos, side)->WorldUtils.getTileEntity(world, pos, tile)
                                             .map(creator)
                                             .orElse(null);
    }
}
