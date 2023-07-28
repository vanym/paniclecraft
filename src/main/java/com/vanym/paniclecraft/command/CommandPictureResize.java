package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandPictureResize extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureResize(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getName() {
        return "resize";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        int width, height;
        if (args.length == 1) {
            width = height = parseInt(args[0], 1,
                                      Math.min(Core.instance.painting.MAX_WIDTH,
                                               Core.instance.painting.MAX_HEIGHT));
        } else if (args.length == 2) {
            width = parseInt(args[0], 1, Core.instance.painting.MAX_WIDTH);
            height = parseInt(args[1], 1, Core.instance.painting.MAX_HEIGHT);
        } else {
            throw new WrongUsageException(this.getUsage(sender));
        }
        EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        TextComponentTranslation success = new TextComponentTranslation(
                String.format("commands.%s.%s.success", DEF.MOD_ID, "pictureresize"),
                picture.getWidth(),
                picture.getHeight(),
                width,
                height);
        if (SideUtils.callSync(picture.syncObject(), ()->picture.resize(width, height))) {
            sender.sendMessage(success);
        } else {
            sender.sendMessage(new TextComponentTranslation(
                    String.format("commands.%s.%s.failure", DEF.MOD_ID, "pictureresize")));
        }
    }
}
