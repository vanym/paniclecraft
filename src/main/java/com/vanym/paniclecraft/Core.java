package com.vanym.paniclecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.vanym.paniclecraft.client.ClientProxy;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CreativeTabMod3;
import com.vanym.paniclecraft.core.IProxy;
import com.vanym.paniclecraft.core.SyncTileEntityUpdater;
import com.vanym.paniclecraft.core.Version;
import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.core.component.ModComponentAdvSign;
import com.vanym.paniclecraft.core.component.ModComponentBroom;
import com.vanym.paniclecraft.core.component.ModComponentCannon;
import com.vanym.paniclecraft.core.component.ModComponentDeskGame;
import com.vanym.paniclecraft.core.component.ModComponentPainting;
import com.vanym.paniclecraft.core.component.ModComponentPortableWorkbench;
import com.vanym.paniclecraft.recipe.RecipeDummy;
import com.vanym.paniclecraft.recipe.conditions.ConfigCondition;
import com.vanym.paniclecraft.server.ServerProxy;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(DEF.MOD_ID)
public class Core {
    
    public static Core instance;
    public static IProxy proxy;
    
    public final ModComponentBroom broom = new ModComponentBroom();
    public final ModComponentAdvSign advSign = new ModComponentAdvSign();
    public final ModComponentPainting painting = new ModComponentPainting();
    public final ModComponentDeskGame deskgame = new ModComponentDeskGame();
    public final ModComponentCannon cannon = new ModComponentCannon();
    public final ModComponentPortableWorkbench portableworkbench =
            new ModComponentPortableWorkbench();
    
    public final CreativeTabMod3 tab = new CreativeTabMod3(DEF.MOD_ID);
    
    public final CommandMod3 command = new CommandMod3();
    
    public final SimpleChannel network =
            ChannelBuilder.named(new ResourceLocation(DEF.MOD_ID, "main_channel"))
                          .clientAcceptedVersions("1"::equals)
                          .serverAcceptedVersions("1"::equals)
                          .networkProtocolVersion(()->"1")
                          .simpleChannel();
    
    public final SyncTileEntityUpdater syncTileEntityUpdater = new SyncTileEntityUpdater();
    
    protected final List<IModComponent> components = new ArrayList<>(
            Arrays.asList(this.broom, this.advSign, this.painting,
                          this.deskgame, this.cannon,
                          this.portableworkbench));
    
    protected final Supplier<Boolean> versionCheck;
    
    public Core() {
        instance = this;
        proxy = DistExecutor.runForDist(()->ClientProxy::new, ()->ServerProxy::new);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        bus.addListener(this::setup);
        bus.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        RecipeDummy.REGISTER.register(bus);
        this.command.addSubCommand(new CommandVersion());
        if (ModList.get().isLoaded("computercraft")) {
            this.components.add(com.vanym.paniclecraft.plugins.computercraft.ComputerCraftPlugin.instance());
        }
        EnumMap<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders =
                new EnumMap<>(ModConfig.Type.class);
        configBuilders.putAll(Arrays.stream(ModConfig.Type.values())
                                    .collect(Collectors.toMap(Function.identity(),
                                                              e->new ForgeConfigSpec.Builder())));
        ForgeConfigSpec.Builder commonBuilder = configBuilders.get(ModConfig.Type.COMMON);
        this.versionCheck = commonBuilder.define("versionCheck", true)::get;
        Map<ModConfig.Type, ForgeConfigSpec.Builder> initConfigBuilders =
                Collections.unmodifiableMap(configBuilders);
        Core.instance.getComponents().forEach(comp->comp.init(initConfigBuilders));
        EnumMap<ModConfig.Type, ForgeConfigSpec> specs = new EnumMap<>(ModConfig.Type.class);
        configBuilders.entrySet().stream().forEach(e->specs.put(e.getKey(), e.getValue().build()));
        specs.entrySet()
             .stream()
             .filter(e->!e.getValue().isEmpty())
             .forEach(e->context.registerConfig(e.getKey(), e.getValue()));
        MinecraftForge.EVENT_BUS.register(this.syncTileEntityUpdater);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public List<IModComponent> getComponents() {
        return Collections.unmodifiableList(this.components);
    }
    
    protected void setup(FMLCommonSetupEvent event) {
        if (this.versionCheck.get()) {
            Version.startVersionCheck();
        }
    }
    
    protected void serverStarting(FMLServerStartingEvent event) {
        event.getCommandDispatcher().register(this.command.register());
    }
    
    @SubscribeEvent
    protected void registerConditions(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
    }
}
