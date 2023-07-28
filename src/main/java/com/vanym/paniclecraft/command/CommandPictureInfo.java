package com.vanym.paniclecraft.command;

import java.util.Arrays;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandPictureInfo extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    
    public CommandPictureInfo(WorldPictureProvider... providers) {
        this.providers = providers;
    }
    
    @Override
    public String getName() {
        return "pictureinfo";
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(this.getName())
                       .requires(cs->cs.hasPermissionLevel(this.getRequiredPermissionLevel()))
                       .executes(this::execute);
    }
    
    public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.asPlayer();
        Picture picture = CommandUtils.rayTracePicture(player, Arrays.stream(this.providers));
        source.sendFeedback(new StringTextComponent(
                SideUtils.callSync(picture.syncObject(), picture::toString)), false);
        return 1;
    }
}
