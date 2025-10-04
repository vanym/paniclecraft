package com.vanym.paniclecraft.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.command.CommandSource;

public class CommandDev extends CommandMod3 {
    
    public static final String NAME = "dev";
    
    public CommandDev() {
        super(NAME);
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> builder = super.register();
        builder.requires(JUtils.<CommandSource>predicate(Core.instance.devMode)
                               .and(builder.getRequirement()));
        return builder;
    }
}
