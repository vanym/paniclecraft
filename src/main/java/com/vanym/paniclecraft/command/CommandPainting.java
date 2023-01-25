package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

public class CommandPainting extends TreeCommandBase {
    
    public CommandPainting() {
        this.addSubCommand(new CommandView(false, false));
        this.addSubCommand(new CommandView(true, false));
        this.addSubCommand(new CommandView(false, true));
        this.addSubCommand(new CommandView(true, true));
    }
    
    @Override
    public String getCommandName() {
        return "painting";
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean edit, boolean to) {
            super(edit, to, WorldPictureProvider.PAINTING, WorldPictureProvider.PAINTINGFRAME);
        }
    }
}
