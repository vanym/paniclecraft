package com.vanym.paniclecraft.core.dev;

import java.util.Map;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.command.dev.CommandDev;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class CommonDevProxy {
    
    public final CommandDev devCommand = new CommandDev();
    
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        ForgeConfigSpec.Builder commonBuilder = configBuilders.get(ModConfig.Type.COMMON);
        this.devCommand.setRequirement(commonBuilder.define("devmode.devCommand", true)::get);
        Core.instance.command.addSubCommand(this.devCommand);
    }
}
