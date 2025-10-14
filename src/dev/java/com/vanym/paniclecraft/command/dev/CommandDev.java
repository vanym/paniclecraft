package com.vanym.paniclecraft.command.dev;

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.command.CommandSource;

public class CommandDev extends CommandMod3 {
    
    public static final String NAME = "dev";
    
    protected Supplier<Boolean> requirement = ()->true;
    
    public CommandDev() {
        super(NAME);
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> builder = super.register();
        builder.requires(JUtils.<CommandSource>predicate(this.requirement)
                               .and(builder.getRequirement()));
        return builder;
    }
    
    public void setRequirement(Supplier<Boolean> requirement) {
        this.requirement = Objects.requireNonNull(requirement);
    }
}
