package com.vanym.paniclecraft.client.dev;

import java.util.Map;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.command.dev.CommandDev;
import com.vanym.paniclecraft.core.dev.CommonDevProxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@OnlyIn(Dist.CLIENT)
public class ClientDevProxy extends CommonDevProxy {
    
    public final CommandDev clientDevCommand = new CommandDev();
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        super.init(configBuilders);
        ForgeConfigSpec.Builder clientBuilder = configBuilders.get(ModConfig.Type.CLIENT);
        this.devCommand.setRequirement(clientBuilder.define("devmode.clientDevCommand", true)::get);
        Core.proxy.getCommand().addSubCommand(this.clientDevCommand);
    }
}
