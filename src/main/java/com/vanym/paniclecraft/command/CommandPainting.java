package com.vanym.paniclecraft.command;

import java.util.SortedSet;
import java.util.TreeSet;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPainting;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
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
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
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
                return super.checkPermission(server, sender);
            }
        }
    }
    
    protected class CommandGiveTemplate extends CommandBase {
        
        @Override
        public String getName() {
            return "givetemplate";
        }
        
        @Override
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
            ITextComponent message = new TextComponentString("");
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
                    new TextComponentString(
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
        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
                throws CommandException {
            SortedSet<IPictureSize> sizes = new TreeSet<>();
            if (args.length == 0) {
                sizes.addAll(this.createSizesSet(new FixedPictureSize(16)));
                sizes.addAll(this.createSizesSet(Core.instance.painting.config.paintingDefaultSize));
            } else if (args.length == 1) {
                int row = parseInt(args[0]);
                IPictureSize size = new FixedPictureSize(row);
                sizes.addAll(this.createSizesSet(size));
            } else if (args.length == 2) {
                int width = parseInt(args[0]);
                int height = parseInt(args[1]);
                IPictureSize size = new FixedPictureSize(width, height);
                sizes.addAll(this.createSizesSet(size));
            } else {
                throw new WrongUsageException(this.getUsage(sender));
            }
            sender.sendMessage(this.createLine(sizes));
        }
    }
}
