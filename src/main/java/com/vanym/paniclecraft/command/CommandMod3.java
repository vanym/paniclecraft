package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.DEF;

import net.minecraft.command.ICommand;

public class CommandMod3 extends TreeCommandBase {
    
    public static final String NAME = DEF.MOD_ID;
    
    public CommandMod3() {
        this.path = new String[]{this.getCommandName()};
    }
    
    @Override
    public void addSubCommand(ICommand subCommand) {
        super.addSubCommand(subCommand);
    }
    
    @Override
    public String getCommandName() {
        return NAME;
    }
}
