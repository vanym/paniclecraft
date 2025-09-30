package com.vanym.paniclecraft.client.command;

import com.vanym.paniclecraft.command.CommandVersion;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ClientCommandVersion extends CommandVersion {
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
