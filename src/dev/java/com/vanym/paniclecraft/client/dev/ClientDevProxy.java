package com.vanym.paniclecraft.client.dev;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.command.dev.ClientCommandBlockDamage;
import com.vanym.paniclecraft.command.dev.CommandDev;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.dev.CommonDevProxy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientDevProxy extends CommonDevProxy {
    
    public final CommandDev clientDevCommand = new CommandDev();
    
    @Override
    public void preInit(ModConfig config) {
        super.preInit(config);
    }
    
    @Override
    public void init(ModConfig config) {
        super.init(config);
        if (config.getBoolean("clientDevCommand", "devmode", true, "")) {
            Core.proxy.getCommand().addSubCommand(this.clientDevCommand);
        }
        this.clientDevCommand.addSubCommand(new ClientCommandBlockDamage());
    }
    
    @Override
    public void postInit(ModConfig config) {
        super.postInit(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        super.configChanged(config);
    }
}
