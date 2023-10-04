package com.vanym.paniclecraft.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;

import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvTextInput {
    protected final Consumer<String> setClip = GuiUtils::setClipboardString;
    protected final Supplier<String> getClip = GuiUtils::getClipboardString;
    
    protected final List<Element> text = new ArrayList<>();
    protected Style style = new Style();
    protected int cursorPos;
    protected int selectionPos;
    
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (Screen.isSelectAll(key) /* ctrl+a */) {
            this.selectAll();
            return true;
        } else if (Screen.isCopy(key) /* ctrl+c */) {
            this.copy();
            return true;
        } else if (Screen.isPaste(key) /* ctrl+v */) {
            this.paste();
            return true;
        } else if (Screen.isCut(key) /* ctrl+x */) {
            this.cut();
            return true;
        } else {
            boolean words = Screen.hasControlDown();
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                this.backspace(words);
                return true;
            } else if (key == GLFW.GLFW_KEY_DELETE) {
                this.delete(words);
                return true;
            } else {
                boolean select = Screen.hasShiftDown();
                if (key == GLFW.GLFW_KEY_RIGHT) {
                    this.right(select, words);
                    return true;
                } else if (key == GLFW.GLFW_KEY_LEFT) {
                    this.left(select, words);
                    return true;
                } else if (key == GLFW.GLFW_KEY_HOME) {
                    this.home(select);
                    return true;
                } else if (key == GLFW.GLFW_KEY_END) {
                    this.end(select);
                    return true;
                } else if (key == GLFW.GLFW_KEY_INSERT) {
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
    
    protected void insertChar(char c, Style style) {
        if (SharedConstants.isAllowedCharacter(c)) {
            this.text.add(this.cursorPos, new Element(c, style));
            this.selectionPos = ++this.cursorPos;
        }
    }
    
    public void selectAll() {
        this.selectionPos = 0;
        this.cursorPos = this.text.size();
    }
    
    public void copy() {
        this.setClip.accept(this.getSelectedComponent().getString());
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
            Style style = this.style;
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
            String str = this.getComponent().getString();
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
    
    public void applyStyle(Style style) {
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
    
    public void read(ITextComponent line) {
        line = FormattingUtils.parseLine(line.getFormattedText());
        this.clear();
        line.stream().forEachOrdered(sub-> {
            this.style = sub.getStyle();
            this.insertText(sub.getUnformattedComponentText());
        });
    }
    
    public Style getStyle() {
        return this.style;
    }
    
    public ITextComponent getComponent() {
        return makeComponent(this.text.stream());
    }
    
    public ITextComponent getSelectedComponent() {
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
    
    protected static ITextComponent makeComponent(Stream<Element> stream) {
        String line = stream.map(String::valueOf).collect(Collectors.joining());
        return FormattingUtils.parseLine(line);
    }
    
    protected static class Element {
        
        public final char symbol;
        protected Style style;
        
        public Element(char symbol, Style style) {
            this.symbol = symbol;
            this.setStyle(style);
        }
        
        public void setStyle(Style style) {
            this.style = style.createDeepCopy();
        }
        
        public Style copyStyle() {
            return this.style.createDeepCopy();
        }
        
        @Override
        public String toString() {
            return TextFormatting.RESET + this.style.getFormattingCode() + this.symbol;
        }
    }
}
