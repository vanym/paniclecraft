package com.vanym.paniclecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.vanym.paniclecraft.client.ClientProxy;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CreativeTabMod3;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.core.IProxy;
import com.vanym.paniclecraft.core.Version;
import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.core.component.ModComponentAdvSign;
import com.vanym.paniclecraft.core.component.ModComponentBroom;
import com.vanym.paniclecraft.core.component.ModComponentCannon;
import com.vanym.paniclecraft.core.component.ModComponentDeskGame;
import com.vanym.paniclecraft.core.component.ModComponentPainting;
import com.vanym.paniclecraft.core.component.ModComponentPortableWorkbench;
import com.vanym.paniclecraft.network.message.MessageComponentConfig;
import com.vanym.paniclecraft.server.ServerProxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.relauncher.Side;

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
                          .simpleChannel();
    
    protected final List<IModComponent> components = new ArrayList<>(
            Arrays.asList(this.broom, this.advSign, this.painting,
                          this.deskgame, this.cannon,
                          this.portableworkbench));
    
    protected final ForgeConfigSpec.BooleanValue versionCheck;
    
    public Core() {
        instance = this;
        proxy = DistExecutor.runForDist(()->ClientProxy::new, ()->ServerProxy::new);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        bus.addListener(this::setup);
        EnumMap<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders =
                new EnumMap<>(ModConfig.Type.class);
        configBuilders.entrySet().stream().forEach(e->e.setValue(new ForgeConfigSpec.Builder()));
        ForgeConfigSpec.Builder commonBuilder = configBuilders.get(ModConfig.Type.COMMON);
        this.versionCheck = commonBuilder.define("versionCheck", true);
        Map<ModConfig.Type, ForgeConfigSpec.Builder> initConfigBuilders =
                Collections.unmodifiableMap(configBuilders);
        Core.instance.getComponents().forEach(comp->comp.init(initConfigBuilders));
        EnumMap<ModConfig.Type, ForgeConfigSpec> specs = new EnumMap<>(ModConfig.Type.class);
        configBuilders.entrySet().stream().forEach(e->specs.put(e.getKey(), e.getValue().build()));
        specs.entrySet()
             .stream()
             .filter(e->!e.getValue().isEmpty())
             .forEach(e->context.registerConfig(e.getKey(), e.getValue()));
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
    
    @SubscribeEvent
    protected void serverStarting(FMLServerStartingEvent event) {
        event.getCommandDispatcher().register(command.register());
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata modMeta = event.getModMetadata();
        modMeta.modId = DEF.MOD_ID;
        modMeta.name = DEF.MOD_NAME;
        modMeta.authorList = Arrays.asList(new String[]{"ee_man"});
        modMeta.url = "https://github.com/vanym/paniclecraft";
        modMeta.description = "Create, Play or Draw and Clean up After";
        modMeta.version = DEF.VERSION;
        modMeta.autogenerated = false;
        
        this.config = new ModConfig(event.getSuggestedConfigurationFile());
        
        this.command = new CommandMod3();
        this.command.addSubCommand(new CommandVersion());
        
        MinecraftForge.EVENT_BUS.register(this);
        
        if (this.config.getBoolean("creativeTab", "general", true, "")) {
            this.tab = new CreativeTabMod3(DEF.MOD_ID);
        }
        
        if (this.config.getBoolean("versionCheck", "general", true, "")) {
            Version.startVersionCheck();
        }
        
        if (Loader.isModLoaded("computercraft")) {
            this.components.add(com.vanym.paniclecraft.plugins.computercraft.ComputerCraftPlugin.instance());
        }
        
        this.preInitCommon();
        
        for (IModComponent component : Core.instance.getComponents()) {
            component.preInit(this.config);
        }
        proxy.preInit(this.config);
    }
    
    protected void preInitCommon() {
        Core.instance.network.registerMessage(MessageComponentConfig.Handler.class,
                                              MessageComponentConfig.class, 5, Side.CLIENT);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
        for (IModComponent component : Core.instance.getComponents()) {
            component.init(this.config);
        }
        proxy.init(this.config);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(this.config);
        if (this.config.hasChanged()) {
            this.config.save();
        }
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID().equals(DEF.MOD_ID)) {
            return;
        }
        for (IModComponent component : Core.instance.getComponents()) {
            component.configChanged(this.config);
        }
        proxy.configChanged(this.config);
        this.sendConfigToAllPlayers();
        if (this.config.hasChanged()) {
            this.config.save();
        }
    }
    
    public FMLEmbeddedChannel getChannel(Side source) {
        return NetworkRegistry.INSTANCE.getChannel(DEF.MOD_ID, source);
    }
    
    @SubscribeEvent
    public void onConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        this.sendConfigToPlayer(event.getManager());
    }
    
    protected void sendConfigToAllPlayers() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && server.isServerRunning()) {
            PlayerList manager = server.getPlayerList();
            manager.getPlayers()
                   .stream()
                   .map(p->p.connection.netManager)
                   .forEach(this::sendConfigToPlayer);
        }
    }
    
    protected void sendConfigToPlayer(NetworkManager manager) {
        Core.instance.getComponents().forEach(manager.isLocalChannel() ? component-> {
            IServerSideConfig config = component.getServerSideConfig();
            if (config != null) {
                component.setServerSideConfig(config);
            }
        } : component-> {
            MessageComponentConfig message = new MessageComponentConfig(component);
            if (message.isEmpty()) {
                return;
            }
            FMLEmbeddedChannel channel = Core.instance.getChannel(Side.SERVER);
            Packet<?> packet = channel.generatePacketFrom(message);
            manager.sendPacket(packet);
        });
    }
}
