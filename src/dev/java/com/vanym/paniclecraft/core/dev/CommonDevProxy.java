package com.vanym.paniclecraft.core.dev;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.command.dev.CommandDev;
import com.vanym.paniclecraft.core.ModConfig;

public class CommonDevProxy {
    
    public final CommandDev devCommand = new CommandDev();
    
    public void preInit(ModConfig config) {}
    
    public void init(ModConfig config) {
        if (config.getBoolean("devCommand", "devmode", true, "")) {
            Core.instance.command.addSubCommand(this.devCommand);
        }
    }
    
    public void postInit(ModConfig config) {}
    
    public void configChanged(ModConfig config) {}
}
