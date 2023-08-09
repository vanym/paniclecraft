package com.vanym.paniclecraft.command;

import java.util.UUID;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
        
        public int getRequiredPermissionLevel() {
            return 3;
        }
        
        @Override
        public LiteralArgumentBuilder<CommandSource> register() {
            DoubleArgumentType radiusArgumentType = DoubleArgumentType.doubleArg(0.0D, 1024.0D);
            return Commands.literal(this.getName())
                           .requires(cs->cs.hasPermissionLevel(this.getRequiredPermissionLevel()))
                           .then(Commands.argument("radius", radiusArgumentType)
                                         .executes(this::execute)
                                         .then(Commands.argument("location", Vec3Argument.vec3())
                                                       .executes(this::execute)));
        }
        
        public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            CommandSource source = context.getSource();
            double radius = DoubleArgumentType.getDouble(context, "radius");
            Vec3d coords;
            try {
                coords = Vec3Argument.getLocation(context, "location").getPosition(source);
            } catch (IllegalArgumentException e) {
                coords = source.getPos();
            }
            World world = source.getWorld();
            AxisAlignedBB box = GeometryUtils.getPointBox(coords.getX(),
                                                          coords.getY(),
                                                          coords.getZ())
                                             .grow(radius);
            int count = EntityPaintOnBlock.clearArea(world, box);
            String name = world.getWorldInfo().getWorldName();
            source.sendFeedback(new TranslationTextComponent(
                    this.getTranslationPrefix() + ".clear",
                    count,
                    name,
                    box.toString().substring(3)), true);
            return count;
        }
    }
    
    protected class CommandInfo extends CommandBase {
        
        protected final Dynamic3CommandExceptionType REQUIRES_PAINTONBLOCK_EXCEPTION_TYPE =
                new Dynamic3CommandExceptionType(
                        (x, y, z)->new TranslationTextComponent(
                                this.getTranslationPrefix() + ".nopaintonblock",
                                x,
                                y,
                                z));
        
        public CommandInfo() {}
        
        @Override
        public String getName() {
            return "info";
        }
        
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        @Override
        public LiteralArgumentBuilder<CommandSource> register() {
            return Commands.literal(this.getName())
                           .requires(cs->cs.hasPermissionLevel(this.getRequiredPermissionLevel()))
                           .executes(this::execute)
                           .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                         .executes(this::execute));
        }
        
        public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            CommandSource source = context.getSource();
            BlockPos pos;
            try {
                pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
            } catch (IllegalArgumentException e) {
                pos = CommandUtils.rayTraceBlocks(source.asPlayer()).getPos();
            }
            EntityPaintOnBlock entityPOB =
                    EntityPaintOnBlock.getEntity(source.getWorld(), pos);
            if (entityPOB == null) {
                throw this.REQUIRES_PAINTONBLOCK_EXCEPTION_TYPE.create(pos.getX(),
                                                                       pos.getY(),
                                                                       pos.getZ());
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
            source.sendFeedback(new StringTextComponent(line), false);
            return 1;
        }
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean edit, boolean to) {
            super(edit, to, PROVIDERS);
        }
        
        @Override
        public boolean checkPermission(CommandSource source) {
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
                return super.checkPermission(source);
            }
        }
    }
}
