package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class TreeCommandBase extends CommandBase {
    
    protected final Map<String, ICommand> subCommands = new HashMap<>();
    protected final List<ICommand> commandList = new ArrayList<>();
    
    protected TreeCommandBase() {
        super();
    }
    
    protected void addSubCommand(ICommand subCommand) {
        if (subCommand instanceof CommandBase) {
            ((CommandBase)subCommand).setParentPath(this.path);
        }
        this.commandList.add(subCommand);
        this.subCommands.put(subCommand.getName(), subCommand);
        List<String> aliases = subCommand.getAliases();
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
    public String getUsage(ICommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getPath());
        sb.append(" (");
        sb.append(String.join("|", this.getPossibleCommandsNames(sender)));
        sb.append(")");
        return sb.toString();
    }
    
    protected ITextComponent getUsageComponent(ICommandSender sender) {
        TextComponentTranslation message = new TextComponentTranslation(
                "commands.generic.usage",
                new Object[]{new TextComponentTranslation(this.getUsage(sender))});
        message.getStyle().setColor(TextFormatting.RED);
        return message;
    }
    
    protected List<ICommand> getPossibleCommands(ICommandSender sender) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return Arrays.asList(this.commandList.stream()
                                             .filter(c->c.checkPermission(server, sender))
                                             .toArray(ICommand[]::new));
    }
    
    protected List<String> getPossibleCommandsNames(ICommandSender sender) {
        return this.getPossibleCommands(sender)
                   .stream()
                   .map(ICommand::getName)
                   .collect(Collectors.toList());
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        ICommand command;
        if (args.length == 0 || args[0].trim().isEmpty()) {
            command = null;
        } else {
            command = this.subCommands.get(args[0]);
        }
        if (command == null) {
            sender.sendMessage(this.getUsageComponent(sender));
            return;
        }
        if (!command.checkPermission(server, sender)) {
            throw new CommandException("commands.generic.permission");
        }
        command.execute(server, sender, dropFirstString(args));
    }
    
    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            @Nullable BlockPos pos) {
        if (args.length == 1 && !args[0].isEmpty()) {
            return this.getPossibleCommandsNames(sender)
                       .stream()
                       .filter(s->s.toLowerCase().startsWith(args[0].toLowerCase()))
                       .collect(Collectors.toList());
        } else if (args.length > 1) {
            ICommand command = this.subCommands.get(args[0]);
            if (command != null) {
                return command.getTabCompletions(server, sender, dropFirstString(args), pos);
            }
        }
        return this.getPossibleCommandsNames(sender);
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !this.getPossibleCommands(sender).isEmpty();
    }
    
    protected static String[] dropFirstString(String[] args) {
        List<String> list = Arrays.asList(args);
        List<String> sub = list.subList(1, list.size());
        return sub.toArray(new String[0]);
    }
}
