package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

public class CommandPictureResize extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureResize(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getCommandName() {
        return "resize";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        int width, height;
        if (args.length == 1) {
            width = height = parseIntBounded(sender, args[0], 1,
                                             Math.min(Core.instance.painting.MAX_WIDTH,
                                                      Core.instance.painting.MAX_HEIGHT));
        } else if (args.length == 2) {
            width = parseIntBounded(sender, args[0], 1, Core.instance.painting.MAX_WIDTH);
            height = parseIntBounded(sender, args[1], 1, Core.instance.painting.MAX_HEIGHT);
        } else {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        ChatComponentTranslation success = new ChatComponentTranslation(
                String.format("commands.%s.%s.success", DEF.MOD_ID, "pictureresize"),
                picture.getWidth(),
                picture.getHeight(),
                width,
                height);
        if (picture.resize(width, height)) {
            sender.addChatMessage(success);
        } else {
            sender.addChatMessage(new ChatComponentTranslation(
                    String.format("commands.%s.%s.failure", DEF.MOD_ID, "pictureresize")));
        }
    }
}
