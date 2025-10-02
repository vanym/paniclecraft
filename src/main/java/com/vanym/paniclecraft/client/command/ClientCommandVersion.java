package com.vanym.paniclecraft.client.command;

import com.vanym.paniclecraft.command.CommandVersion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;

@SideOnly(Side.CLIENT)
public class ClientCommandVersion extends CommandVersion {
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
