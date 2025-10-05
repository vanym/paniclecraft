package com.vanym.paniclecraft.server;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.core.CommonProxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
    
    @Override
    public CommandMod3 getCommand() {
        return Core.instance.command;
    }
    
    @Override
    public CommandDev getDevCommand() {
        return Core.instance.devCommand;
    }
}
