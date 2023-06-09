package com.vanym.paniclecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CreativeTabMod3;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.core.IProxy;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.Version;
import com.vanym.paniclecraft.core.component.IModComponent;
import com.vanym.paniclecraft.core.component.IModComponent.IServerSideConfig;
import com.vanym.paniclecraft.core.component.ModComponentAdvSign;
import com.vanym.paniclecraft.core.component.ModComponentBroom;
import com.vanym.paniclecraft.core.component.ModComponentCannon;
import com.vanym.paniclecraft.core.component.ModComponentDeskGame;
import com.vanym.paniclecraft.core.component.ModComponentPainting;
import com.vanym.paniclecraft.core.component.ModComponentPortableWorkbench;
import com.vanym.paniclecraft.network.message.MessageComponentConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
    modid = DEF.MOD_ID,
    name = DEF.MOD_NAME,
    version = DEF.VERSION,
    acceptedMinecraftVersions = "[1.12.2]",
    guiFactory = "com.vanym.paniclecraft.client.gui.config.GuiModConfigFactory")
public class Core implements IGuiHandler {
    
    @Instance(DEF.MOD_ID)
    public static Core instance;
    
    @SidedProxy(
        clientSide = "com.vanym.paniclecraft.client.ClientProxy",
        serverSide = "com.vanym.paniclecraft.server.ServerProxy",
        modId = DEF.MOD_ID)
    public static IProxy proxy;
    
    public final ModComponentBroom broom = new ModComponentBroom();
    public final ModComponentAdvSign advSign = new ModComponentAdvSign();
    public final ModComponentPainting painting = new ModComponentPainting();
    public final ModComponentDeskGame deskgame = new ModComponentDeskGame();
    public final ModComponentCannon cannon = new ModComponentCannon();
    public final ModComponentPortableWorkbench portableworkbench =
            new ModComponentPortableWorkbench();
    
    public CreativeTabMod3 tab;
    
    public CommandMod3 command;
    
    public ModConfig config;
    
    public final SimpleNetworkWrapper network =
            NetworkRegistry.INSTANCE.newSimpleChannel(DEF.MOD_ID);
    
    protected final List<IModComponent> components = new ArrayList<>(
            Arrays.asList(this.broom, this.advSign, this.painting,
                          this.deskgame, this.cannon,
                          this.portableworkbench));
    
    public List<IModComponent> getComponents() {
        return Collections.unmodifiableList(this.components);
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
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(this.command);
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
    
    @Override
    public Object getServerGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        if (ID >= 0 && ID < GUIs.values().length) {
            return GUIs.values()[ID].getServerGuiElement(ID, player, world, x, y, z);
        } else {
            return null;
        }
    }
    
    @Override
    public Object getClientGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        if (ID >= 0 && ID < GUIs.values().length) {
            return GUIs.values()[ID].getClientGuiElement(ID, player, world, x, y, z);
        } else {
            return null;
        }
    }
}
