package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.WorldUtils;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.TurtleUpgrades;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
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
        MinecraftForge.EVENT_BUS.register(this);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        serverBuilder.push(this.getName());
        this.peripheralCannon = serverBuilder.define("peripheralCannon", true)::get;
        this.peripheralChessDesk = serverBuilder.define("peripheralChessDesk", true)::get;
        this.peripheralPainting = serverBuilder.define("peripheralPainting", false)::get;
        this.peripheralPaintingFrame = serverBuilder.define("peripheralPaintingFrame", false)::get;
        this.turtleUpgradePaintBrush = serverBuilder.define("turtleUpgradePaintBrush", true)::get;
        serverBuilder.pop();
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        Stream.of(Pair.of(this.peripheralCannon, this.cannonPeripheralProvider),
                  Pair.of(this.peripheralChessDesk, this.chessDeskPeripheralProvider),
                  Pair.of(this.peripheralPainting, this.paintingPeripheralProvider),
                  Pair.of(this.peripheralPaintingFrame, this.paintingFramePeripheralProvider))
              .map(pair->makeConditionalProvider(pair.getLeft(), pair.getRight()))
              .forEach(ComputerCraftAPI::registerPeripheralProvider);
        ComputerCraftAPI.registerTurtleUpgrade(this.turtlePaintBrush);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.NORMAL, this::configChanged);
        this.applyConfig();
    }
    
    // Subscribes in setup
    protected void configChanged(ModConfig.ConfigReloading event) {
        if (event.getConfig().getType() != ModConfig.Type.SERVER
            || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
            return;
        }
        this.applyConfig();
    }
    
    protected void applyConfig() {
        if (this.turtleUpgradePaintBrush.get()) {
            TurtleUpgrades.enable(this.turtlePaintBrush);
        } else {
            TurtleUpgrades.disable(this.turtlePaintBrush);
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
    
    protected static IPeripheralProvider makeConditionalProvider(
            Supplier<Boolean> condition,
            IPeripheralProvider provider) {
        return (wrld, pos, side)->!condition.get() ? null : provider.getPeripheral(wrld, pos, side);
    }
}
