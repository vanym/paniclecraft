package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.MainUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class CommandPaintingView extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPaintingView(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getCommandName() {
        return "view";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        int maxRadius;
        if (args.length == 0) {
            maxRadius = 1024;
        } else if (args.length == 1) {
            maxRadius = parseInt(sender, args[0]);
        } else {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        if (!(sender instanceof EntityPlayerMP)) {
            ChatComponentStyle message = new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".playerless");
            message.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(message);
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP)sender;
        MovingObjectPosition target = MainUtils.rayTraceBlocks(player, 6.0D);
        if (target == null || target.typeOfHit != MovingObjectType.BLOCK) {
            ChatComponentStyle message = new ChatComponentTranslation(
                    this.getTranslationPrefix() + ".noblock");
            message.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(message);
            return;
        }
        for (WorldPictureProvider provider : this.providers) {
            WorldPicturePoint point = new WorldPicturePoint(
                    provider,
                    sender.getEntityWorld(),
                    target.blockX,
                    target.blockY,
                    target.blockZ,
                    target.sideHit);
            ContainerPaintingViewServer view =
                    ContainerPaintingViewServer.makeFullView(point, maxRadius);
            if (view != null) {
                ContainerPaintingViewServer.openGui(player, view);
                return;
            }
        }
        ChatComponentStyle message = new ChatComponentTranslation(
                this.getTranslationPrefix() + ".nopainting");
        message.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(message);
    }
}
