package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public abstract class TreeCommandBase extends CommandBase {
    
    protected final List<ICommand> commandList = new ArrayList<>();
    
    protected TreeCommandBase() {
        super();
    }
    
    protected void addSubCommand(ICommand subCommand) {
        if (subCommand instanceof CommandBase) {
            ((CommandBase)subCommand).setParentPath(this.path);
        }
        this.commandList.add(subCommand);
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal(this.getName());
        this.commandList.stream().map(ICommand::register).forEach(builder::then);
        return builder;
    }
    
    @Override
    protected void setParentPath(String[] path) {
        super.setParentPath(path);
        this.commandList.stream()
                        .filter(c->c instanceof CommandBase)
                        .map(c->(CommandBase)c)
                        .forEach(c->c.setParentPath(this.path));
    }
}
