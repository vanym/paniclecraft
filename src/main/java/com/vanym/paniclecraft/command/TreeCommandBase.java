package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class TreeCommandBase extends CommandBase {
    
    protected final Map<String, ICommand> subCommands = new HashMap<>();
    protected final List<ICommand> commandList = new ArrayList<>();
    
    protected TreeCommandBase() {
        super();
    }
    
    @SuppressWarnings("unchecked")
    protected void addSubCommand(ICommand subCommand) {
        if (subCommand instanceof CommandBase) {
            ((CommandBase)subCommand).setParentPath(this.path);
        }
        this.commandList.add(subCommand);
        this.subCommands.put(subCommand.getCommandName(), subCommand);
        List<String> aliases = subCommand.getCommandAliases();
        if (aliases != null) {
            aliases.stream().forEach(a->this.subCommands.put(a, subCommand));
        }
    }
    
    @Override
    protected void setParentPath(String[] path) {
        super.setParentPath(path);
        this.commandList.stream()
                        .filter(c->c instanceof CommandBase)
                        .map(c->(CommandBase)c)
                        .forEach(c->c.setParentPath(this.path));
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getPath());
        sb.append(" (");
        sb.append(String.join("|", this.getPossibleCommandsNames(sender)));
        sb.append(")");
        return sb.toString();
    }
    
    protected IChatComponent getUsage(ICommandSender sender) {
        ChatComponentTranslation message = new ChatComponentTranslation(
                "commands.generic.usage",
                new Object[]{new ChatComponentTranslation(this.getCommandUsage(sender))});
        message.getChatStyle().setColor(EnumChatFormatting.RED);
        return message;
    }
    
    protected List<ICommand> getPossibleCommands(ICommandSender sender) {
        return Arrays.asList(this.commandList.stream()
                                             .filter(c->c.canCommandSenderUseCommand(sender))
                                             .toArray(ICommand[]::new));
    }
    
    protected List<String> getPossibleCommandsNames(ICommandSender sender) {
        return this.getPossibleCommands(sender)
                   .stream()
                   .map(c->c.getCommandName())
                   .collect(Collectors.toList());
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ICommand command;
        if (args.length == 0 || args[0].trim().isEmpty()) {
            command = null;
        } else {
            command = this.subCommands.get(args[0]);
        }
        if (command == null) {
            sender.addChatMessage(this.getUsage(sender));
            return;
        }
        if (!command.canCommandSenderUseCommand(sender)) {
            throw new CommandException("commands.generic.permission");
        }
        command.processCommand(sender, dropFirstString(args));
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1 && !args[0].isEmpty()) {
            return this.getPossibleCommandsNames(sender)
                       .stream()
                       .filter(s->s.startsWith(args[0]))
                       .collect(Collectors.toList());
        } else if (args.length > 1) {
            ICommand command = this.subCommands.get(args[0]);
            if (command != null) {
                return command.addTabCompletionOptions(sender, dropFirstString(args));
            }
        }
        return this.getPossibleCommandsNames(sender);
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !this.getPossibleCommands(sender).isEmpty();
    }
    
    protected static String[] dropFirstString(String[] args) {
        List<String> list = Arrays.asList(args);
        List<String> sub = list.subList(1, list.size());
        return sub.toArray(new String[0]);
    }
}
