package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandPictureInfo extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureInfo(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getName() {
        return "pictureinfo";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException(this.getUsage(sender));
        }
        EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        sender.sendMessage(new TextComponentString(picture.toString()));
    }
}
