package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.PictureUtils;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
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
        EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        sender.addChatMessage(new ChatComponentText(
                PictureUtils.callSync(picture, picture::toString)));
    }
}
