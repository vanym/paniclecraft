package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.vanym.paniclecraft.core.Version;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.ForgeVersion.Status;

public class CommandVersion extends CommandBase {
    
    @Override
    public String getCommandName() {
        return "version";
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        List<String> formatArgs = new ArrayList<>();
        formatArgs.add(Version.getVersion());
        Status status = Version.getStatus();
        if (Stream.of(Status.PENDING, Status.FAILED, Status.UP_TO_DATE).noneMatch(e->e == status)) {
            formatArgs.add(Version.getTarget());
        }
        String transl = this.getTranslationPrefix() + "." + Objects.toString(status).toLowerCase();
        sender.addChatMessage(new ChatComponentTranslation(transl, formatArgs.toArray()));
    }
}
