package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
        List<Predicate<CommandSource>> reqs = new ArrayList<>(this.commandList.size());
        this.commandList.stream()
                        .map(ICommand::register)
                        .peek(sub->reqs.add(Objects.requireNonNull(sub.getRequirement())))
                        .forEach(builder::then);
        builder.requires(s->reqs.stream().anyMatch(r->r.test(s)));
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
