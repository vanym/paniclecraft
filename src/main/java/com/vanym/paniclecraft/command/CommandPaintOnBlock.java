package com.vanym.paniclecraft.command;

import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandPaintOnBlock extends TreeCommandBase {
    
    public CommandPaintOnBlock() {
        this.addSubCommand(new CommandClearArea());
    }
    
    @Override
    public String getCommandName() {
        return "paintonblock";
    }
    
    protected class CommandClearArea extends CommandBase {
        
        public CommandClearArea() {
            this.path = new String[]{CommandMod3.NAME,
                                     CommandPaintOnBlock.this.getCommandName(),
                                     this.getCommandName()};
        }
        
        @Override
        public String getCommandName() {
            return "cleararea";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 3;
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 && args.length != 4) {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            double radius = parseDoubleBounded(sender, args[0], 0.0D, 1024.0D);
            ChunkCoordinates coords = sender.getPlayerCoordinates();
            if (args.length == 4) {
                coords.set(parseInt(sender, args[1]),
                           parseInt(sender, args[2]),
                           parseInt(sender, args[3]));
            }
            World world = sender.getEntityWorld();
            Vec3 vec = Vec3.createVectorHelper(coords.posX + 0.5D,
                                               coords.posY + 0.5D,
                                               coords.posZ + 0.5D);
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(vec.xCoord, vec.yCoord, vec.zCoord,
                                                             vec.xCoord, vec.yCoord, vec.zCoord)
                                             .expand(radius, radius, radius);
            int count = EntityPaintOnBlock.clearArea(world, box);
            String name = world.provider.getDimensionName();
            String line = this.getTranslationPrefix() + ".clear";
            func_152373_a(sender, this, line, count, name, box.toString().substring(3));
        }
    }
}
