package com.vanym.paniclecraft.client;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.command.ClientCommandMod3;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandVersion;
import com.vanym.paniclecraft.core.CommonProxy;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.IModComponent;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public ClientCommandMod3 command;
    
    @Override
    public void preInit(ModConfig config) {
        super.preInit(config);
        this.command = new ClientCommandMod3();
        this.command.addSubCommand(new CommandVersion());
        ClientCommandHandler.instance.registerCommand(this.command);
        for (IModComponent component : Core.instance.getComponents()) {
            component.preInitClient(config);
        }
    }
    
    @Override
    public void init(ModConfig config) {
        super.init(config);
        for (IModComponent component : Core.instance.getComponents()) {
            component.initClient(config);
        }
    }
    
    @Override
    public void postInit(ModConfig config) {
        super.postInit(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        super.configChanged(config);
        for (IModComponent component : Core.instance.getComponents()) {
            component.configChangedClient(config);
        }
    }
    
    @Override
    public CommandMod3 getCommand() {
        return this.command;
    }
}
