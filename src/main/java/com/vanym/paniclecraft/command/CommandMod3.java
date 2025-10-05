package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.DEF;

public class CommandMod3 extends TreeCommandBase {
    
    public static final String NAME = DEF.MOD_ID;
    
    protected final String name;
    
    public CommandMod3() {
        this(NAME);
    }
    
    protected CommandMod3(String name) {
        this.name = name;
        this.path = new String[]{name};
    }
    
    @Override
    public void addSubCommand(ICommand subCommand) {
        super.addSubCommand(subCommand);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
