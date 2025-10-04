package com.vanym.paniclecraft.client.utils;

import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import net.minecraft.client.Minecraft;
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
    
    protected final WeakHashMap<ChatScreen, String> lastApplied = new WeakHashMap<>();
    
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
        if (chat.suggestions == null && event.getKeyCode() == GLFW.GLFW_KEY_TAB) {
            this.lastApplied.remove(chat);
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
        if (chat.input == null) {
            return;
        }
        String text = chat.input.getValue();
        if (!text.startsWith("/")) {
            this.lastApplied.remove(chat);
            return;
        }
        if (chat.input.getValue().equals(this.lastApplied.get(chat))) {
            return;
        }
        this.lastApplied.put(chat, chat.input.getValue());
        text = text.substring(1);
        if (!this.trigger.test(text)) {
            return;
        }
        applySuggestions(chat, this.dispatcher);
    }
    
    protected static void applySuggestions(
            ChatScreen chat,
            CommandDispatcher<CommandSource> dispatcher) {
        String text = chat.input.getValue();
        if (chat.suggestions != null) {
            ChatScreen.SuggestionsList sugsList = chat.suggestions;
            text = text.substring(0, sugsList.suggestions.getRange().getEnd());
        }
        StringReader sr = new StringReader(text);
        sr.skip();
        CommandSource source = Minecraft.getInstance().player.createCommandSourceStack();
        ParseResults<CommandSource> pr = dispatcher.parse(sr, source);
        dispatcher.getCompletionSuggestions(pr).thenAccept((suggs)->addSuggestions(chat, suggs));
    }
    
    protected static void addSuggestions(ChatScreen chat, Suggestions suggs) {
        if (chat.suggestions == null) {
            setSuggestions(chat, suggs);
            return;
        }
        suggs.getList().forEach(sugg->addSuggestion(chat, sugg));
    }
    
    protected static void addSuggestion(ChatScreen chat, Suggestion sugg) {
        ChatScreen.SuggestionsList sugsList = chat.suggestions;
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
        if (sugsList.suggestions.getList().size() <= 10) {
            sugsList.rect = new Rectangle2d(
                    sugsList.rect.getX(),
                    sugsList.rect.getY() - 12,
                    Math.max(chat.font.width(sugg.getText()),
                             sugsList.rect.getWidth()),
                    sugsList.rect.getHeight() + 12);
        }
        if (index <= sugsList.current) {
            sugsList.cycle(1);
        }
    }
    
    protected static void setSuggestions(ChatScreen chat, Suggestions suggs) {
        if (suggs.isEmpty()) {
            return;
        }
        int textWidth = suggs.getList()
                             .stream()
                             .map(Suggestion::getText)
                             .map(chat.font::width)
                             .max(Integer::compare)
                             .orElse(0);
        int width = chat.input.getScreenX(suggs.getRange().getStart());
        width = MathHelper.clamp(width, 0, chat.width - textWidth);
        chat.suggestions =
                chat.new SuggestionsList(width, chat.height - 12, textWidth, suggs);
    }
}
