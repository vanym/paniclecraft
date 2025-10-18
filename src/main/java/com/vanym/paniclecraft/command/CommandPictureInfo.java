package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

public class CommandPictureInfo extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureInfo(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getCommandName() {
        return "pictureinfo";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        Entity player = CommandUtils.getSenderAsEntity(sender);
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        sender.addChatMessage(new ChatComponentText(
                SideUtils.callSync(picture.syncObject(), picture::toString)));
    }
}
