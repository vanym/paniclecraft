package com.vanym.paniclecraft.server;

import java.util.Optional;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.core.CommonProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    
    @Override
    public CommandMod3 getCommand() {
        return Core.instance.command;
    }
    
    @Override
    public Optional<CommandDev> getDevCommand() {
        return Core.instance.devCommand;
    }
}
