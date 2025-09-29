package com.vanym.paniclecraft.client.command;

import com.vanym.paniclecraft.command.CommandVersion;

import net.minecraft.command.ICommandSender;

public class ClientCommandVersion extends CommandVersion {
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
