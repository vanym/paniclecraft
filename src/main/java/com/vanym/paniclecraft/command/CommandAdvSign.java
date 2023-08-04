package com.vanym.paniclecraft.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;

public class CommandAdvSign extends TreeCommandBase {
    
    protected static final SimpleCommandExceptionType REQUIRES_ADVSIGN_EXCEPTION_TYPE =
            new SimpleCommandExceptionType(
                    new TranslationTextComponent(
                            String.format("commands.%s.exception.noadvsign", DEF.MOD_ID)));
    
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
            ServerPlayerEntity player = context.getSource().asPlayer();
            BlockRayTraceResult target = CommandUtils.rayTraceBlocks(player);
            TileEntity tile = player.getEntityWorld().getTileEntity(target.getPos());
            if (tile instanceof TileEntityAdvSign) {
                TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
                tileAS.setEditor(player.getUniqueID());
                Core.instance.network.sendTo(new MessageAdvSignOpenGui(tileAS.getPos()),
                                             player.connection.getNetworkManager(),
                                             NetworkDirection.PLAY_TO_CLIENT);
            } else {
                throw REQUIRES_ADVSIGN_EXCEPTION_TYPE.create();
            }
            return 1;
        }
    }
}
