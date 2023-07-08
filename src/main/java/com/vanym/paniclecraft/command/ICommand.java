package com.vanym.paniclecraft.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;

public interface ICommand {
    
    public LiteralArgumentBuilder<CommandSource> register();
    
    public String getName();
}
