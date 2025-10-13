package com.vanym.paniclecraft.client;

import java.util.Map;
import java.util.function.Supplier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.command.ClientCommandMod3;
import com.vanym.paniclecraft.client.utils.ChatScreenUtils;
import com.vanym.paniclecraft.client.utils.ClientChatSuggester;
import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public final ClientCommandMod3 command = new ClientCommandMod3();
    public final CommandDev devCommand = new CommandDev();
    
    protected final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
    
    protected final ClientChatSuggester suggester =
            new ClientChatSuggester(this.commandDispatcher, CommandMod3.NAME);
    protected Supplier<Boolean> enableSuggester = ()->true;
    
    public ClientProxy() {
        this.command.addSubCommand(new CommandVersion());
        this.command.addSubCommand(this.devCommand);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(EventPriority.HIGH, this::configChanged);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        super.init(configBuilders);
        ForgeConfigSpec.Builder builder = configBuilders.get(ModConfig.Type.CLIENT);
        builder.push("general");
        this.enableSuggester =
                builder.comment("Suggester for '/" + ClientCommandMod3.NAME + "' command\n" +
                    "Disable it in case of some problems with chat screen")
                       .define("clientChatSuggester", true)::get;
        builder.pop();
    }
    
    @Override
    public CommandMod3 getCommand() {
        return this.command;
    }
    
    protected void setup(FMLCommonSetupEvent event) {
        this.commandDispatcher.register(this.command.register());
    }
    
    protected void configChanged(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT
            || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
            return;
        }
        if (this.enableSuggester.get()) {
            this.suggester.register();
        } else {
            this.suggester.unregister();
        }
    }
    
    @SubscribeEvent
    protected void clientCommand(ClientChatEvent event) {
        if (!event.getOriginalMessage().startsWith("/")) {
            return;
        }
        try {
            Minecraft mc = Minecraft.getInstance();
            CommandSource source = mc.player.getCommandSource();
            this.commandDispatcher.execute(event.getOriginalMessage().substring(1), source);
            event.setCanceled(true);
            if (ChatScreenUtils.needToLogMessage()) {
                mc.ingameGUI.getChatGUI().addToSentMessages(event.getOriginalMessage());
            }
        } catch (CommandSyntaxException e) {
        }
    }
    
    @Override
    public CommandDev getDevCommand() {
        return this.devCommand;
    }
}
