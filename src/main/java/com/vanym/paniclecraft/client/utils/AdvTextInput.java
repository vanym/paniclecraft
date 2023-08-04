package com.vanym.paniclecraft.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class AdvTextInput {
    protected final Consumer<String> setClip = GuiUtils::setClipboardString;
    protected final Supplier<String> getClip = GuiUtils::getClipboardString;
    
    protected final List<Element> text = new ArrayList<>();
    protected ChatStyle style = new ChatStyle();
    protected int cursorPos;
    protected int selectionPos;
    
    public boolean keyTyped(char character, int key) {
        if (character == 1 /* ctrl+a */) {
            this.selectAll();
            return true;
        } else if (character == 3 /* ctrl+c */) {
            this.copy();
            return true;
        } else if (character == 22 /* ctrl+v */) {
            this.paste();
            return true;
        } else if (character == 24 /* ctrl+x */) {
            this.cut();
            return true;
        } else {
            boolean words = GuiScreen.isCtrlKeyDown();
            if (key == 14 /* backspace */) {
                this.backspace(words);
                return true;
            } else if (key == 211 /* delete */) {
                this.delete(words);
                return true;
            } else {
                boolean select = GuiScreen.isShiftKeyDown();
                if (key == 205 /* right */) {
                    this.right(select, words);
                    return true;
                } else if (key == 203 /* left */) {
                    this.left(select, words);
                    return true;
                } else if (key == 199 /* home */ || key == 71 /* shift + home numpad */) {
                    this.home(select);
                    return true;
                } else if (key == 207 /* end */ || key == 79 /* shift + end numpad */) {
                    this.end(select);
                    return true;
                } else if (key == 210 /* insert */) {
                    // nope
                }
            }
        }
        return false;
    }
    
    public void insertText(String line) {
        this.removeSelected();
        IntStream.range(0, line.length())
                 .mapToObj(line::charAt)
                 .forEachOrdered(this::insertChar);
    }
    
    protected void insertChar(char c) {
        this.insertChar(c, this.style);
    }
    
    protected void insertChar(char c, ChatStyle style) {
        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            this.text.add(this.cursorPos, new Element(c, style));
            this.selectionPos = ++this.cursorPos;
        }
    }
    
    public void selectAll() {
        this.selectionPos = 0;
        this.cursorPos = this.text.size();
    }
    
    public void copy() {
        this.setClip.accept(this.getSelectedComponent().getUnformattedText());
    }
    
    public void cut() {
        this.copy();
        if (!this.isSelected()) {
            this.selectAll();
        }
        this.removeSelected();
    }
    
    public void paste() {
        this.insertText(this.getClip.get());
    }
    
    protected boolean backspace(boolean words) {
        return this.remove(-1, words);
    }
    
    protected boolean delete(boolean words) {
        return this.remove(1, words);
    }
    
    protected boolean remove(int i, boolean words) {
        if (!this.isSelected()) {
            ChatStyle style = this.style;
            this.move(i, true, words);
            this.style = style;
        }
        return this.removeSelected();
    }
    
    protected void right(boolean select, boolean words) {
        this.move(1, select, words);
    }
    
    protected void left(boolean select, boolean words) {
        this.move(-1, select, words);
    }
    
    protected void home(boolean select) {
        this.move(-this.text.size(), select, false);
    }
    
    protected void end(boolean select) {
        this.move(this.text.size(), select, false);
    }
    
    protected boolean move(int i, boolean select, boolean words) {
        int pos;
        if (words) {
            String str = this.getComponent().getUnformattedText();
            pos = GuiUtils.getWordPosition(str, i, this.cursorPos, true);
        } else {
            pos = this.cursorPos + i;
        }
        boolean moved = this.setCursorPos(pos);
        if (!select && this.selectionPos != this.cursorPos) {
            this.selectionPos = this.cursorPos;
            return true;
        }
        return moved;
    }
    
    public boolean remove(int i) {
        try {
            this.text.remove(i);
            if (this.selectionPos > i) {
                --this.selectionPos;
            }
            if (this.cursorPos > i) {
                --this.cursorPos;
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
    
    public boolean removeLast() {
        return this.remove(this.text.size() - 1);
    }
    
    public boolean removeBack() {
        return this.remove(this.cursorPos - 1);
    }
    
    public boolean removeSelected() {
        if (!this.isSelected()) {
            return false;
        }
        int min = Math.min(this.cursorPos, this.selectionPos);
        int max = Math.max(this.cursorPos, this.selectionPos);
        for (; min < max; max--) {
            this.text.remove(min);
        }
        this.selectionPos = this.cursorPos = min;
        return true;
    }
    
    public void applyStyle(ChatStyle style) {
        this.style = style.createShallowCopy().setParentStyle(this.style).createDeepCopy();
        if (this.isSelected()) {
            int min = Math.min(this.cursorPos, this.selectionPos);
            int max = Math.max(this.cursorPos, this.selectionPos);
            this.text.subList(min, max)
                     .stream()
                     .forEach(e->e.setStyle(style.createShallowCopy()
                                                 .setParentStyle(e.copyStyle())));
        }
    }
    
    public boolean isSelected() {
        return this.selectionPos != this.cursorPos;
    }
    
    public boolean setCursorPos(int pos) {
        pos = Math.max(0, Math.min(this.text.size(), pos));
        int offset = pos - this.cursorPos;
        this.cursorPos = pos;
        Element way;
        if (offset > 0) {
            way = this.text.get(pos - 1);
        } else if (offset < 0) {
            way = this.text.get(pos);
        } else {
            return false;
        }
        this.style = way.copyStyle();
        return true;
    }
    
    public void setSelectionPos(int pos) {
        this.selectionPos = Math.max(0, Math.min(this.text.size(), pos));
    }
    
    public void clear() {
        this.text.clear();
        this.selectionPos = this.cursorPos = 0;
    }
    
    @SuppressWarnings("unchecked")
    public void read(IChatComponent line) {
        line = FormattingUtils.parseLine(line.getFormattedText());
        this.clear();
        for (IChatComponent sub : (Iterable<IChatComponent>)line) {
            this.style = sub.getChatStyle();
            this.insertText(sub.getUnformattedTextForChat());
        }
    }
    
    public ChatStyle getStyle() {
        return this.style;
    }
    
    public IChatComponent getComponent() {
        return makeComponent(this.text.stream());
    }
    
    public IChatComponent getSelectedComponent() {
        if (!this.isSelected()) {
            return this.getComponent();
        }
        int min = Math.min(this.cursorPos, this.selectionPos);
        int max = Math.max(this.cursorPos, this.selectionPos);
        return makeComponent(this.text.subList(min, max).stream());
    }
    
    public int getCursorPos() {
        return this.cursorPos;
    }
    
    public int getSelectionPos() {
        return this.selectionPos;
    }
    
    protected static IChatComponent makeComponent(Stream<Element> stream) {
        String line = stream.map(String::valueOf).collect(Collectors.joining());
        return FormattingUtils.parseLine(line);
    }
    
    protected static class Element {
        
        public final char symbol;
        protected ChatStyle style;
        
        public Element(char symbol, ChatStyle style) {
            this.symbol = symbol;
            this.setStyle(style);
        }
        
        public void setStyle(ChatStyle style) {
            this.style = style.createDeepCopy();
        }
        
        public ChatStyle copyStyle() {
            return this.style.createDeepCopy();
        }
        
        @Override
        public String toString() {
            return EnumChatFormatting.RESET + this.style.getFormattingCode() + this.symbol;
        }
    }
}
