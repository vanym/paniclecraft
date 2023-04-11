package com.vanym.paniclecraft.command;

import java.util.UUID;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
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
    public String getName() {
        return "paintonblock";
    }
    
    protected class CommandClearArea extends CommandBase {
        
        public CommandClearArea() {}
        
        @Override
        public String getName() {
            return "cleararea";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 3;
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            if (args.length != 1 && args.length != 4) {
                throw new WrongUsageException(this.getUsage(sender));
            }
            double radius = parseDouble(args[0], 0.0D, 1024.0D);
            BlockPos coords = sender.getPosition();
            if (args.length == 4) {
                coords = parseBlockPos(sender, args, 1, true);
            }
            World world = sender.getEntityWorld();
            AxisAlignedBB box = GeometryUtils.getPointBox(coords.getX() + 0.5D,
                                                          coords.getY() + 0.5D,
                                                          coords.getZ() + 0.5D)
                                             .grow(radius);
            int count = EntityPaintOnBlock.clearArea(world, box);
            String name = world.provider.getDimensionType().getName();
            String line = this.getTranslationPrefix() + ".clear";
            notifyCommandListener(sender, this, line, count, name, box.toString().substring(3));
        }
    }
    
    protected class CommandInfo extends CommandBase {
        
        public CommandInfo() {}
        
        @Override
        public String getName() {
            return "info";
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            BlockPos pos;
            if (args.length == 0) {
                EntityPlayerMP player = CommandUtils.getSenderAsPlayer(sender);
                RayTraceResult target = CommandUtils.rayTraceBlocks(player);
                pos = target.getBlockPos();
            } else if (args.length == 3) {
                pos = parseBlockPos(sender, args, 0, true);
            } else {
                throw new WrongUsageException(this.getUsage(sender));
            }
            EntityPaintOnBlock entityPOB =
                    EntityPaintOnBlock.getEntity(sender.getEntityWorld(), pos);
            if (entityPOB == null) {
                throw new CommandException(
                        this.getTranslationPrefix() + ".nopaintonblock",
                        new Object[]{pos.getX(), pos.getY(), pos.getZ()});
            }
            String name = entityPOB.getClass().getSimpleName();
            int id = entityPOB.getEntityId();
            UUID uuid = entityPOB.getUniqueID();
            BlockPos entityPos = entityPOB.getBlockPos();
            String line = String.format("%s[x=%d, y=%d, z=%d, id=%d, uuid=%s]", name,
                                        entityPos.getX(),
                                        entityPos.getY(),
                                        entityPos.getZ(),
                                        id, uuid.toString());
            sender.sendMessage(new TextComponentString(line));
        }
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean edit, boolean to) {
            super(edit, to, PROVIDERS);
        }
        
        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            if (!this.edit && !this.to
                && Core.instance.painting.server.freePaintOnBlockView) {
                return true;
            } else if (this.edit && !this.to
                && Core.instance.painting.server.freePaintOnBlockEditView) {
                return true;
            } else if (!this.edit && this.to
                && Core.instance.painting.server.freePaintOnBlockViewTo) {
                return true;
            } else if (this.edit && this.to
                && Core.instance.painting.server.freePaintOnBlockEditViewTo) {
                return true;
            } else {
                return super.checkPermission(server, sender);
            }
        }
    }
}
