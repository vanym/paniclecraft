package com.vanym.paniclecraft.plugins.computercraft;

import java.util.function.Function;

import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.WorldUtils;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public class ComputerCraftPlugin implements IModComponent {
    
    protected static ComputerCraftPlugin instance;
    
    protected final IPeripheralProvider cannonPeripheralProvider =
            makeProvider(TileEntityCannon.class, CannonPeripheral::new);
    
    protected final IPeripheralProvider chessDeskPeripheralProvider =
            makeProvider(TileEntityChessDesk.class, ChessDeskPeripheral::new);
    
    protected final IPeripheralProvider paintingPeripheralProvider =
            PaintingPeripheral::getPeripheral;
    protected final IPeripheralProvider paintingFramePeripheralProvider =
            PaintingFramePeripheral::getPeripheral;
    protected final TurtlePaintBrush turtlePaintBrush = new TurtlePaintBrush();
    protected final TurtleSuckPaintingFrame turtleSuckPaintingFrame = new TurtleSuckPaintingFrame();
    
    @Override
    public void preInit(ModConfig config) {
        if (config.getBoolean("peripheralCannon", this.getName(), true, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.cannonPeripheralProvider);
        }
        if (config.getBoolean("peripheralChessDesk", this.getName(), true, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.chessDeskPeripheralProvider);
        }
        if (config.getBoolean("peripheralPainting", this.getName(), false, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.paintingPeripheralProvider);
        }
        if (config.getBoolean("peripheralPaintingFrame", this.getName(), false, "")) {
            ComputerCraftAPI.registerPeripheralProvider(this.paintingFramePeripheralProvider);
        }
        if (config.getBoolean("turtleUpgradePaintBrush", this.getName(), true, "")) {
            ComputerCraftAPI.registerTurtleUpgrade(this.turtlePaintBrush);
        }
        if (config.getBoolean("turtleSuckPaintingFrame", this.getName(), true, "")) {
            MinecraftForge.EVENT_BUS.register(this.turtleSuckPaintingFrame);
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
    
    protected static <T extends TileEntity> IPeripheralProvider makeProvider(
            Class<T> tile,
            Function<T, IPeripheral> creator) {
        return (world, pos, side)->WorldUtils.getTileEntity(world, pos, tile)
                                             .map(creator)
                                             .orElse(null);
    }
}
