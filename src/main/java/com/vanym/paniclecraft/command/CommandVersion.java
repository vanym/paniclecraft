package com.vanym.paniclecraft.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.core.Version;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.VersionChecker.Status;

public class CommandVersion extends CommandBase {
    
    @Override
    public String getName() {
        return "version";
    }
    
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal(this.getName())
                       .executes(this::execute);
    }
    
    public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
        List<String> formatArgs = new ArrayList<>();
        formatArgs.add(Version.getVersion());
        Status status = Version.getStatus();
        if (Stream.of(Status.PENDING, Status.FAILED, Status.UP_TO_DATE).noneMatch(e->e == status)) {
            formatArgs.add(Version.getTarget());
        }
        String transl = this.getTranslationPrefix() + "." +
            Objects.toString(status).toLowerCase(Locale.ROOT);
        context.getSource()
               .sendFeedback(new TranslationTextComponent(transl, formatArgs.toArray()), false);
        return 1;
    }
}
