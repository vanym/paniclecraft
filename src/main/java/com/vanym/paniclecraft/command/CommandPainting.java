package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

public class CommandPainting extends TreeCommandBase {
    
    public CommandPainting() {
        this.addSubCommand(new CommandPaintingView(
                WorldPictureProvider.PAINTING,
                WorldPictureProvider.PAINTINGFRAME));
    }
    
    @Override
    public String getCommandName() {
        return "painting";
    }
}
