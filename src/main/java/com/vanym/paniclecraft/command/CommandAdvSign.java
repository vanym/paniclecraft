package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

public class CommandAdvSign extends TreeCommandBase {
    
    public CommandAdvSign() {
        this.addSubCommand(new CommandEdit());
    }
    
    @Override
    public String getCommandName() {
        return "advsign";
    }
    
    protected class CommandEdit extends CommandBase {
        
        @Override
        public String getCommandName() {
            return "edit";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
            MovingObjectPosition target = CommandUtils.rayTraceBlocks(player);
            TileEntity tile = sender.getEntityWorld()
                                    .getTileEntity(target.blockX, target.blockY, target.blockZ);
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                tileAS.setEditor(player.getPersistentID());
                Core.instance.network.sendTo(new MessageAdvSignOpenGui(
                        tileAS.xCoord,
                        tileAS.yCoord,
                        tileAS.zCoord), player);
            } else {
                throw new CommandException(
                        String.format("commands.%s.exception.noadvsign", DEF.MOD_ID));
            }
        }
    }
}
