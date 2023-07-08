package com.vanym.paniclecraft.command;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class CommandPainting extends TreeCommandBase {
    
    protected static final WorldPictureProvider[] PROVIDERS =
            new WorldPictureProvider[]{WorldPictureProvider.PAINTING,
                                       WorldPictureProvider.PAINTINGFRAME};
    
    public CommandPainting() {
        this.addSubCommand(new CommandView(false, false));
        this.addSubCommand(new CommandView(true, false));
        this.addSubCommand(new CommandView(false, true));
        this.addSubCommand(new CommandView(true, true));
        this.addSubCommand(new CommandPictureInfo(PROVIDERS));
        this.addSubCommand(new CommandPictureResize(PROVIDERS));
        this.addSubCommand(new CommandGiveTemplate());
    }
    
    @Override
    public String getName() {
        return "painting";
    }
    
    protected class CommandView extends CommandPaintingView {
        
        public CommandView(boolean edit, boolean to) {
            super(edit, to, PROVIDERS);
        }
        
        @Override
        public boolean checkPermission(CommandSource source) {
            if (!this.edit && !this.to
                && Core.instance.painting.server.freePaintingView) {
                return true;
            } else if (this.edit && !this.to
                && Core.instance.painting.server.freePaintingEditView) {
                return true;
            } else if (!this.edit && this.to
                && Core.instance.painting.server.freePaintingViewTo) {
                return true;
            } else if (this.edit && this.to
                && Core.instance.painting.server.freePaintingEditViewTo) {
                return true;
            } else {
                return super.checkPermission(source);
            }
        }
    }
    
    protected class CommandGiveTemplate extends CommandBase {
        
        @Override
        public String getName() {
            return "givetemplate";
        }
        
        public int getRequiredPermissionLevel() {
            return 2;
        }
        
        protected SortedSet<IPictureSize> createSizesSet(IPictureSize size) {
            SortedSet<IPictureSize> set = new TreeSet<>();
            for (FixedPictureSize current = new FixedPictureSize(size);
                 current.getWidth() <= Core.instance.painting.MAX_WIDTH
                     && current.getHeight() <= Core.instance.painting.MAX_HEIGHT;
                 current = new FixedPictureSize(current, 2)) {
                set.add(current);
            }
            return set;
        }
        
        protected ITextComponent createLine(Iterable<IPictureSize> sizes) {
            ITextComponent message = new StringTextComponent("");
            boolean f = true;
            for (IPictureSize size : sizes) {
                if (!f) {
                    message = message.appendText(", ");
                } else {
                    f = false;
                }
                message.appendSibling(this.createTemplate(size));
            }
            return message;
        }
        
        protected ITextComponent createTemplate(IPictureSize size) {
            ITextComponent template =
                    new StringTextComponent(
                            String.format("%d×%d", size.getWidth(), size.getHeight()));
            ItemStack stack = ItemPainting.getSizedItem(size);
            stack.setCount(stack.getMaxStackSize());
            Style style = template.getStyle();
            style.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    CommandUtils.makeGiveCommand("@p", stack)));
            style.setHoverEvent(CommandUtils.makeItemHover(stack));
            return template;
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
                           .executes(this::execute)
                           .then(Commands.argument("size", sizeArgumentType)
                                         .executes(this::execute))
                           .then(Commands.argument("width", widthArgumentType)
                                         .then(Commands.argument("height", heightArgumentType)
                                                       .executes(this::execute)));
        }
        
        public int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            SortedSet<IPictureSize> sizes = new TreeSet<>();
            try {
                int width, height;
                try {
                    width = height = IntegerArgumentType.getInteger(context, "size");
                } catch (IllegalArgumentException e) {
                    width = IntegerArgumentType.getInteger(context, "width");
                    height = IntegerArgumentType.getInteger(context, "height");
                }
                sizes.addAll(this.createSizesSet(new FixedPictureSize(width, height)));
            } catch (IllegalArgumentException e) {
                sizes.addAll(this.createSizesSet(new FixedPictureSize(16)));
                sizes.addAll(this.createSizesSet(Core.instance.painting.config.paintingDefaultSize));
            }
            context.getSource().sendFeedback(this.createLine(sizes), false);
            return sizes.size();
        }
    }
}
