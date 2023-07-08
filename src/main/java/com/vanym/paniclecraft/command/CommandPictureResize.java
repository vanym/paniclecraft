package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandPictureResize extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureResize(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getName() {
        return "resize";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        IntegerArgumentType widthArgumentType =
                IntegerArgumentType.integer(1, Core.instance.painting.MAX_WIDTH);
        IntegerArgumentType heightArgumentType =
                IntegerArgumentType.integer(1, Core.instance.painting.MAX_HEIGHT);
        IntegerArgumentType sizeArgumentType =
                IntegerArgumentType.integer(1, Math.min(widthArgumentType.getMaximum(),
                                                        heightArgumentType.getMaximum()));
        return Commands.literal(this.getName())
                       .requires(cs->cs.hasPermissionLevel(this.getRequiredPermissionLevel()))
                       .then(Commands.argument("size", sizeArgumentType).executes(this::execute))
                       .then(Commands.argument("width", widthArgumentType)
                                     .then(Commands.argument("height", heightArgumentType)
                                                   .executes(this::execute)));
    }
    
    public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
        int width, height;
        try {
            width = height = IntegerArgumentType.getInteger(context, "size");
        } catch (IllegalArgumentException e) {
            width = IntegerArgumentType.getInteger(context, "width");
            height = IntegerArgumentType.getInteger(context, "height");
        }
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        TranslationTextComponent success = new TranslationTextComponent(
                String.format("commands.%s.%s.success", DEF.MOD_ID, "pictureresize"),
                picture.getWidth(),
                picture.getHeight(),
                width,
                height);
        if (picture.resize(width, height)) {
            source.sendFeedback(success, false);
            return 1;
        } else {
            source.sendErrorMessage(new TranslationTextComponent(
                    String.format("commands.%s.%s.failure", DEF.MOD_ID, "pictureresize")));
            return 0;
        }
    }
}
