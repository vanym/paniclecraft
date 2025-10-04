package com.vanym.paniclecraft.client.command;

import com.vanym.paniclecraft.command.CommandMod3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientCommandMod3 extends CommandMod3 {
    
    public static final String NAME = CommandMod3.NAME + "client";
    
    public ClientCommandMod3() {
        super(NAME);
    }
}
