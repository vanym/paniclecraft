package com.vanym.paniclecraft.command;

import java.util.Arrays;
import java.util.UUID;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandPaintOnBlock extends TreeCommandBase {
    
    protected static final WorldPictureProvider[] PROVIDERS =
            new WorldPictureProvider[]{WorldPictureProvider.PAINTONBLOCK};
    
    public CommandPaintOnBlock() {
        this.addSubCommand(new CommandInfo());
        this.addSubCommand(new CommandPictureInfo(PROVIDERS));
        this.addSubCommand(new CommandClearArea());
        this.addSubCommand(new CommandView(false, false));
        this.addSubCommand(new CommandView(true, false));
        this.addSubCommand(new CommandView(false, true));
        this.addSubCommand(new CommandView(true, true));
        this.addSubCommand(new CommandPictureResize(PROVIDERS));
    }
    
    @Override
    public String getCommandName() {
        return "paintonblock";
    }
    
    protected class CommandClearArea extends CommandBase {
        
        public CommandClearArea() {}
        
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
                int x, y, z;
                x = MathHelper.floor_double(func_110666_a(sender, coords.posX, args[1]));
                y = MathHelper.floor_double(func_110666_a(sender, coords.posY, args[2]));
                z = MathHelper.floor_double(func_110666_a(sender, coords.posZ, args[3]));
                coords.set(x, y, z);
            }
            World world = sender.getEntityWorld();
            AxisAlignedBB box = GeometryUtils.getPointBox(coords.posX + 0.5D,
                                                          coords.posY + 0.5D,
                                                          coords.posZ + 0.5D)
                                             .expand(radius, radius, radius);
            int count = EntityPaintOnBlock.clearArea(world, box);
            String name = world.provider.getDimensionName();
            String line = this.getTranslationPrefix() + ".clear";
            func_152373_a(sender, this, line, count, name, box.toString().substring(3));
        }
    }
    
    protected class CommandInfo extends CommandBase {
        
        public CommandInfo() {}
        
        @Override
        public String getCommandName() {
            return "info";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            ChunkCoordinates pos = this.getBlockTarget(sender, Arrays.asList(args));
            EntityPaintOnBlock entityPOB =
                    EntityPaintOnBlock.getEntity(sender.getEntityWorld(),
                                                 pos.posX, pos.posY, pos.posZ);
            if (entityPOB == null) {
                throw new CommandException(
                        this.getTranslationPrefix() + ".nopaintonblock",
                        new Object[]{pos.posX, pos.posY, pos.posZ});
            }
            String name = entityPOB.getClass().getSimpleName();
            int id = entityPOB.getEntityId();
            UUID uuid = entityPOB.getUniqueID();
            String line = String.format("%s[x=%d, y=%d, z=%d, id=%d, uuid=%s]", name,
                                        entityPOB.getBlockX(),
                                        entityPOB.getBlockY(),
                                        entityPOB.getBlockZ(),
                                        id, uuid.toString());
            sender.addChatMessage(new ChatComponentText(line));
        }
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean edit, boolean to) {
            super(edit, to, PROVIDERS);
        }
        
        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            if (!this.edit && !this.to
                && Core.instance.painting.server.allowPaintOnBlockView) {
                return true;
            } else if (this.edit && !this.to
                && Core.instance.painting.server.allowPaintOnBlockEditView) {
                return true;
            } else if (!this.edit && this.to
                && Core.instance.painting.server.allowPaintOnBlockViewTo) {
                return true;
            } else if (this.edit && this.to
                && Core.instance.painting.server.allowPaintOnBlockEditViewTo) {
                return true;
            } else {
                return super.canCommandSenderUseCommand(sender);
            }
        }
    }
}
