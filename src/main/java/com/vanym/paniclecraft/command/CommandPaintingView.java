package com.vanym.paniclecraft.command;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkHooks;

public class CommandPaintingView extends CommandBase {
    
    protected final WorldPictureProvider[] providers;
    protected final boolean edit;
    protected final boolean to;
    
    public CommandPaintingView(boolean edit, boolean to, WorldPictureProvider... providers) {
        this.providers = providers;
        this.edit = edit;
        this.to = to;
    }
    
    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (this.edit) {
            sb.append("edit");
        }
        sb.append("view");
        if (this.to) {
            sb.append("to");
        }
        return sb.toString();
    }
    
    protected boolean checkPermission(CommandSource source) {
        return source.hasPermissionLevel(2);
    }
    
    @Override
    protected String getTranslationPrefix() {
        return "commands." + DEF.MOD_ID + ".paintingview";
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        RequiredArgumentBuilder<CommandSource, Integer> maxRadius =
                Commands.argument("maxRadius", IntegerArgumentType.integer(0, 4096))
                        .executes(this::execute);
        if (this.to) {
            return Commands.literal(this.getName())
                           .requires(this::checkPermission)
                           .then(Commands.argument("viewer", EntityArgument.player())
                                         .executes(this::execute)
                                         .then(maxRadius));
        } else {
            return Commands.literal(this.getName())
                           .requires(this::checkPermission)
                           .executes(this::execute)
                           .then(maxRadius);
        }
    }
    
    public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        ServerPlayerEntity viewer;
        if (this.to) {
            viewer = EntityArgument.getPlayer(context, "viewer");
        } else {
            viewer = player;
        }
        int maxRadius = ((Supplier<Integer>)()-> {
            try {
                return IntegerArgumentType.getInteger(context, "maxRadius");
            } catch (IllegalArgumentException e) {
                return 1024;
            }
        }).get();
        try {
            ContainerPaintingViewServer.Provider view =
                    Arrays.stream(this.providers)
                          .map(CommandUtils.makeProviderRayTraceMapper(player))
                          .map(p->ContainerPaintingViewServer.makeFullView(p, maxRadius))
                          .filter(v->v != null)
                          .findFirst()
                          .get();
            view.setEditable(this.edit);
            NetworkHooks.openGui(viewer, view, view);
            return 1;
        } catch (NoSuchElementException e) {
            throw CommandUtils.REQUIRES_PAINTING_EXCEPTION_TYPE.create();
        }
    }
}
