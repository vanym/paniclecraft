package com.vanym.paniclecraft.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.client.command.ClientCommandMod3;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public final ClientCommandMod3 command = new ClientCommandMod3();
    
    protected final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
    
    public ClientProxy() {
        this.command.addSubCommand(new CommandVersion());
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    public CommandMod3 getCommand() {
        return this.command;
    }
    
    protected void setup(FMLCommonSetupEvent event) {
        this.commandDispatcher.register(this.command.register());
    }
    
    @SubscribeEvent
    protected void clientCommand(ClientChatEvent event) {
        if (!event.getOriginalMessage().startsWith("/")) {
            return;
        }
        try {
            CommandSource source = Minecraft.getInstance().player.getCommandSource();
            this.commandDispatcher.execute(event.getOriginalMessage().substring(1), source);
            event.setCanceled(true);
        } catch (CommandSyntaxException e) {
        }
    }
}
