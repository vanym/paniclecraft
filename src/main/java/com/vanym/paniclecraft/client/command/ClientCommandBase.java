package com.vanym.paniclecraft.client.command;

import com.vanym.paniclecraft.command.CommandBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;

@SideOnly(Side.CLIENT)
public abstract class ClientCommandBase extends CommandBase {
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
