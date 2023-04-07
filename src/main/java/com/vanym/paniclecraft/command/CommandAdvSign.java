package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;

public class CommandAdvSign extends TreeCommandBase {
    
    public CommandAdvSign() {
        this.addSubCommand(new CommandEdit());
    }
    
    @Override
    public String getName() {
        return "advsign";
    }
    
    protected class CommandEdit extends CommandBase {
        
        @Override
        public String getName() {
            return "edit";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
            RayTraceResult target = CommandUtils.rayTraceBlocks(player);
            TileEntity tile = sender.getEntityWorld().getTileEntity(target.getBlockPos());
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                tileAS.setEditor(player);
                Core.instance.network.sendTo(new MessageAdvSignOpenGui(tileAS.getPos()), player);
            } else {
                throw new CommandException(
                        String.format("commands.%s.exception.noadvsign", DEF.MOD_ID));
            }
        }
    }
}
