package com.vanym.paniclecraft.client.utils;

import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientChatSuggester {
    
    protected final WeakHashMap<CommandSuggestionHelper, String> lastApplied = new WeakHashMap<>();
    
    protected final CommandDispatcher<CommandSource> dispatcher;
    protected final Predicate<String> trigger;
    
    public ClientChatSuggester(CommandDispatcher<CommandSource> dispatcher) {
        this(dispatcher, t->true);
    }
    
    public ClientChatSuggester(CommandDispatcher<CommandSource> dispatcher, String prefixTrigger) {
        this(dispatcher, s->s.startsWith(prefixTrigger));
        Objects.requireNonNull(prefixTrigger);
    }
    
    public ClientChatSuggester(CommandDispatcher<CommandSource> dispatcher,
            Predicate<String> trigger) {
        this.dispatcher = Objects.requireNonNull(dispatcher);
        this.trigger = Objects.requireNonNull(trigger);
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    protected void chatKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof ChatScreen)) {
            return;
        }
        ChatScreen chat = (ChatScreen)screen;
        if (chat.commandSuggestions == null) {
            return;
        }
        CommandSuggestionHelper suggh = chat.commandSuggestions;
        if (suggh.suggestions == null
            && event.getKeyCode() == GLFW.GLFW_KEY_TAB) {
            this.lastApplied.remove(suggh);
        }
    }
    
    @SubscribeEvent
    protected void chatTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof ChatScreen)) {
            return;
        }
        ChatScreen chat = (ChatScreen)screen;
        if (chat.commandSuggestions == null) {
            return;
        }
        CommandSuggestionHelper suggh = chat.commandSuggestions;
        String text = suggh.input.getValue();
        if (!text.startsWith("/")) {
            this.lastApplied.remove(suggh);
            return;
        }
        if (suggh.input.getValue().equals(this.lastApplied.get(suggh))) {
            return;
        }
        this.lastApplied.put(suggh, suggh.input.getValue());
        text = text.substring(1);
        if (!this.trigger.test(text)) {
            return;
        }
        applySuggestions(suggh, this.dispatcher);
    }
    
    protected static void applySuggestions(
            CommandSuggestionHelper suggh,
            CommandDispatcher<CommandSource> dispatcher) {
        String text = suggh.input.getValue();
        if (suggh.suggestions != null) {
            CommandSuggestionHelper.Suggestions sugsList = suggh.suggestions;
            text = text.substring(0, sugsList.suggestions.getRange().getEnd());
        }
        StringReader sr = new StringReader(text);
        sr.skip();
        CommandSource source = Minecraft.getInstance().player.createCommandSourceStack();
        ParseResults<CommandSource> pr = dispatcher.parse(sr, source);
        int cursor = pr.getReader().getTotalLength();
        CommandContextBuilder<CommandSource> context = pr.getContext();
        SuggestionContext<CommandSource> suggContext = context.findSuggestionContext(cursor);
        CommandNode<CommandSource> parent = suggContext.parent;
        int start = Math.min(suggContext.startPos, cursor);
        String input = pr.getReader().getString().substring(0, cursor);
        for (CommandNode<CommandSource> node : parent.getChildren()) {
            if (!node.canUse(source)) {
                continue;
            }
            try {
                node.listSuggestions(context.build(input), new SuggestionsBuilder(input, start))
                    .thenAccept((suggs)->addSuggestions(suggh, suggs));
            } catch (CommandSyntaxException e) {
            }
        }
    }
    
    protected static void addSuggestions(CommandSuggestionHelper suggh, Suggestions suggs) {
        if (suggh.suggestions == null) {
            setSuggestions(suggh, suggs);
            return;
        }
        suggs.getList().forEach(sugg->addSuggestion(suggh, sugg));
    }
    
    protected static void addSuggestion(CommandSuggestionHelper suggh, Suggestion sugg) {
        CommandSuggestionHelper.Suggestions sugsList = suggh.suggestions;
        if (sugsList.suggestions.getList()
                                .stream()
                                .map(Suggestion::getText)
                                .anyMatch(sugg.getText()::equals)) {
            return;
        }
        int index = (int)sugsList.suggestions.getList()
                                             .stream()
                                             .map(Suggestion::getText)
                                             .map(sugg.getText()::compareTo)
                                             .filter(i->i > 0)
                                             .count();
        sugsList.suggestions.getList().add(index, sugg);
        int width = suggh.font.width(sugg.getText());
        if (width > sugsList.rect.getWidth()) {
            sugsList.rect = new Rectangle2d(
                    sugsList.rect.getX(),
                    sugsList.rect.getY(),
                    width,
                    sugsList.rect.getHeight());
        }
        if (sugsList.suggestions.getList().size() <= 10) {
            sugsList.rect = new Rectangle2d(
                    sugsList.rect.getX(),
                    sugsList.rect.getY() - 12,
                    sugsList.rect.getWidth(),
                    sugsList.rect.getHeight() + 12);
        }
        if (index <= sugsList.current) {
            sugsList.cycle(1);
        }
    }
    
    protected static void setSuggestions(CommandSuggestionHelper suggh, Suggestions suggs) {
        if (suggs.isEmpty()) {
            return;
        }
        int textWidth = suggs.getList()
                             .stream()
                             .map(Suggestion::getText)
                             .map(suggh.font::width)
                             .max(Integer::compare)
                             .orElse(0);
        int width = suggh.input.getScreenX(suggs.getRange().getStart());
        width = MathHelper.clamp(width, 0, suggh.screen.width - textWidth);
        suggh.suggestions =
                suggh.new Suggestions(width, suggh.screen.height - 12, textWidth, suggs, false);
    }
}
