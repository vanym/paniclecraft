package com.vanym.paniclecraft.client;

import java.util.Optional;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.command.ClientCommandMod3;
import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.IModComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public ClientCommandMod3 command;
    public Optional<CommandDev> devCommand = Optional.empty();
    
    @Override
    public void preInit(ModConfig config) {
        this.command = new ClientCommandMod3();
        this.command.addSubCommand(new CommandVersion());
        if (Core.instance.devCommand.isPresent()) {
            this.devCommand = Optional.of(new CommandDev());
            this.command.addSubCommand(this.devCommand.get());
        }
        ClientCommandHandler.instance.registerCommand(this.command);
        for (IModComponent component : Core.instance.getComponents()) {
            component.preInitClient(config);
        }
    }
    
    @Override
    public void init(ModConfig config) {
        for (IModComponent component : Core.instance.getComponents()) {
            component.initClient(config);
        }
    }
    
    @Override
    public void postInit(ModConfig config) {}
    
    @Override
    public void configChanged(ModConfig config) {
        for (IModComponent component : Core.instance.getComponents()) {
            component.configChangedClient(config);
        }
    }
    
    @Override
    public CommandMod3 getCommand() {
        return this.command;
    }
    
    @Override
    public Optional<CommandDev> getDevCommand() {
        return this.devCommand;
    }
}
