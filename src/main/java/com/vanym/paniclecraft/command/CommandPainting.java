package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.ICommandSender;

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
        
        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            if (!this.edit && !this.to
                && Core.instance.painting.config.freePaintingView) {
                return true;
            } else if (this.edit && !this.to
                && Core.instance.painting.config.freePaintingEditView) {
                return true;
            } else if (!this.edit && this.to
                && Core.instance.painting.config.freePaintingViewTo) {
                return true;
            } else if (this.edit && this.to
                && Core.instance.painting.config.freePaintingEditViewTo) {
                return true;
            } else {
                return super.canCommandSenderUseCommand(sender);
            }
        }
    }
}
