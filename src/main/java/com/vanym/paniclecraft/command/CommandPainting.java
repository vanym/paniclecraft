package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

public class CommandPainting extends TreeCommandBase {
    
    public CommandPainting() {
        this.addSubCommand(new CommandView(false));
        this.addSubCommand(new CommandView(true));
    }
    
    @Override
    public String getCommandName() {
        return "painting";
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean to) {
            super(to, WorldPictureProvider.PAINTING, WorldPictureProvider.PAINTINGFRAME);
        }
    }
}
