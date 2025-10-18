package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public abstract class CommandBase extends net.minecraft.command.CommandBase {
    
    protected String[] path;
    
    protected String getPath() {
        if (this.path != null) {
            return "/" + String.join(" ", this.path);
        } else {
            return this.getName();
        }
    }
    
    protected void setParentPath(String[] path) {
        if (path != null) {
            List<String> list = new ArrayList<>(Arrays.asList(path));
            list.add(this.getName());
            this.path = list.toArray(new String[list.size()]);
        }
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return this.getTranslationPrefix() + ".usage";
    }
    
    protected String getTranslationPrefix() {
        List<String> path = new ArrayList<>();
        path.add("commands");
        if (this.path != null) {
            path.addAll(Arrays.asList(this.path));
        } else {
            path.add(this.getName());
        }
        return String.join(".", path);
    }
    
    protected BlockPos getBlockTarget(ICommandSender sender) throws CommandException {
        return this.getBlockTarget(sender, Arrays.asList());
    }
    
    protected BlockPos getBlockTarget(
            ICommandSender sender,
            List<String> args) throws CommandException {
        if (args.isEmpty()) {
            Entity player = CommandUtils.getSenderAsEntity(sender);
            RayTraceResult target = CommandUtils.rayTraceBlocks(player);
            return target.getBlockPos();
        } else if (args.size() == 3) {
            return parseBlockPos(sender, args.toArray(new String[3]), 0, true);
        } else {
            throw new WrongUsageException(this.getUsage(sender));
        }
    }
}
