package com.vanym.paniclecraft.command;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.command.CommandUtils.NopaintingException;
import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandPaintingView extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    protected final boolean edit;
    protected final boolean to;
    
    public CommandPaintingView(boolean edit, boolean to, WorldPictureProvider... providers) {
        this.providers = providers;
        this.edit = edit;
        this.to = to;
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (this.edit) {
            sb.append("edit");
        }
        sb.append("view");
        if (this.to) {
            sb.append("to");
        }
        return sb.toString();
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return super.getTranslationPrefix() + ".usage";
    }
    
    @Override
    protected String getTranslationPrefix() {
        return "commands." + DEF.MOD_ID + ".paintingview";
    }
    
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return super.isUsernameIndex(args, index) || (this.to && index == 0);
    }
    
    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            @Nullable BlockPos pos) {
        if (this.to && args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return super.getTabCompletions(server, sender, args, pos);
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
        EntityPlayerMP viewer;
        int i = 0;
        if (this.to) {
            if (args.length == i) {
                throw new WrongUsageException(this.getUsage(sender));
            }
            viewer = getPlayer(server, sender, args[i++]);
        } else {
            viewer = player;
        }
        int maxRadius;
        if (args.length == i) {
            maxRadius = 1024;
        } else if (args.length == (i + 1)) {
            maxRadius = parseInt(args[i++]);
        } else {
            throw new WrongUsageException(this.getUsage(sender));
        }
        try {
            ContainerPaintingViewServer view =
                    Arrays.stream(this.providers)
                          .map(CommandUtils.makeProviderRayTraceMapper(player))
                          .map(p->ContainerPaintingViewServer.makeFullView(p, maxRadius))
                          .filter(v->v != null)
                          .findFirst()
                          .get();
            view.setEditable(this.edit);
            ContainerPaintingViewServer.openGui(viewer, view);
        } catch (NoSuchElementException e) {
            throw new NopaintingException();
        }
    }
}
