package com.vanym.paniclecraft.command;

import java.util.UUID;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.MainUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandPaintOnBlock extends TreeCommandBase {
    
    public CommandPaintOnBlock() {
        this.addSubCommand(new CommandInfo());
        this.addSubCommand(new CommandClearArea());
        this.addSubCommand(new CommandView(false, false));
        this.addSubCommand(new CommandView(true, false));
        this.addSubCommand(new CommandView(false, true));
        this.addSubCommand(new CommandView(true, true));
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
            int x, y, z;
            if (args.length == 0) {
                if (!(sender instanceof EntityPlayer)) {
                    ChatComponentStyle message = new ChatComponentTranslation(
                            this.getTranslationPrefix() + ".playerless");
                    message.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(message);
                    return;
                }
                EntityPlayer player = (EntityPlayer)sender;
                MovingObjectPosition target = MainUtils.rayTraceBlocks(player, 6.0D);
                if (target == null || target.typeOfHit != MovingObjectType.BLOCK) {
                    ChatComponentStyle message = new ChatComponentTranslation(
                            this.getTranslationPrefix() + ".noblock");
                    message.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(message);
                    return;
                }
                x = target.blockX;
                y = target.blockY;
                z = target.blockZ;
            } else if (args.length == 3) {
                ChunkCoordinates coords = sender.getPlayerCoordinates();
                x = MathHelper.floor_double(func_110666_a(sender, coords.posX, args[0]));
                y = MathHelper.floor_double(func_110666_a(sender, coords.posY, args[1]));
                z = MathHelper.floor_double(func_110666_a(sender, coords.posZ, args[2]));
            } else {
                throw new WrongUsageException(this.getCommandUsage(sender));
            }
            EntityPaintOnBlock entityPOB =
                    EntityPaintOnBlock.getEntity(sender.getEntityWorld(), x, y, z);
            if (entityPOB == null) {
                ChatComponentStyle message = new ChatComponentTranslation(
                        this.getTranslationPrefix() + ".nopaintonblock",
                        new Object[]{x, y, z});
                sender.addChatMessage(message);
                return;
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
            super(edit, to, WorldPictureProvider.PAINTONBLOCK);
        }
        
        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            if (!this.edit && !this.to
                && Core.instance.painting.config.freePaintOnBlockView) {
                return true;
            } else if (this.edit && !this.to
                && Core.instance.painting.config.freePaintOnBlockEditView) {
                return true;
            } else if (!this.edit && this.to
                && Core.instance.painting.config.freePaintOnBlockViewTo) {
                return true;
            } else if (this.edit && this.to
                && Core.instance.painting.config.freePaintOnBlockEditViewTo) {
                return true;
            } else {
                return super.canCommandSenderUseCommand(sender);
            }
        }
    }
}
