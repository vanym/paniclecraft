package com.vanym.paniclecraft.core;

import com.vanym.paniclecraft.command.CommandDev;
import com.vanym.paniclecraft.command.CommandMod3;

public interface IProxy {
    
    public CommandMod3 getCommand();
    
    public CommandDev getDevCommand();
}
