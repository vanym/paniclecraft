package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.vanym.paniclecraft.DEF;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public abstract class CommandBase extends net.minecraft.command.CommandBase {
    
    protected String[] path;
    
    protected String getPath() {
        if (this.path != null) {
            return "/" + String.join(" ", this.path);
        } else {
            return this.getCommandName();
        }
    }
    
    protected void setParentPath(String[] path) {
        if (path != null) {
            List<String> list = new ArrayList<>(Arrays.asList(path));
            list.add(this.getCommandName());
            this.path = list.toArray(new String[list.size()]);
        }
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return this.getTranslationPrefix() + ".usage";
    }
    
    protected String getTranslationPrefix() {
        List<String> path = new ArrayList<>();
        path.add("commands");
        if (this.path != null) {
            path.addAll(Arrays.asList(this.path));
        } else {
            path.add(this.getCommandName());
        }
        return String.join(".", path);
    }
    
    protected ChunkCoordinates getBlockTarget(ICommandSender sender) {
        return this.getBlockTarget(sender, Arrays.asList());
    }
    
    protected ChunkCoordinates getBlockTarget(
            ICommandSender sender,
            List<String> args) {
        if (args.isEmpty()) {
            if (!(sender instanceof EntityPlayer)) {
                throw new CommandException(
                        String.format("commands.%s.exception.playerless", DEF.MOD_ID));
            }
            EntityPlayer player = (EntityPlayer)sender;
            MovingObjectPosition target = CommandUtils.rayTraceBlocks(player);
            return new ChunkCoordinates(target.blockX, target.blockY, target.blockZ);
        } else {
            try {
                Iterator<String> it = args.iterator();
                ChunkCoordinates coords = sender.getPlayerCoordinates();
                return new ChunkCoordinates(
                        MathHelper.floor_double(func_110666_a(sender, coords.posX, it.next())),
                        MathHelper.floor_double(func_110666_a(sender, coords.posY, it.next())),
                        MathHelper.floor_double(func_110666_a(sender, coords.posZ, it.next())));
            } catch (NoSuchElementException e) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
        }
    }
}
