package com.vanym.paniclecraft.client.gui.element;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextProcessing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHexColorField extends TextFieldWidget {
    
    protected static final String NUM_CHARS = "0123456789ABCDEFabcdef";
    
    protected static final List<TextFormatting> COLORS_ENABLED =
            Arrays.asList(TextFormatting.RESET,
                          TextFormatting.RED, TextFormatting.RED,
                          TextFormatting.GREEN, TextFormatting.GREEN,
                          TextFormatting.BLUE, TextFormatting.BLUE);
    
    protected static final List<TextFormatting> COLORS_DISABLED =
            Arrays.asList(TextFormatting.RESET,
                          TextFormatting.DARK_RED, TextFormatting.DARK_RED,
                          TextFormatting.DARK_GREEN, TextFormatting.DARK_GREEN,
                          TextFormatting.DARK_BLUE, TextFormatting.DARK_BLUE);
    
    protected boolean isEnabled = true;
    
    protected Consumer<Integer> setter;
    
    public GuiHexColorField(FontRenderer font, int x, int y) {
        this(font, x, y, 50, 12);
    }
    
    public GuiHexColorField(FontRenderer font, int x, int y, int width, int height) {
        super(font, x, y, width, height, StringTextComponent.EMPTY);
        this.setMaxLength(7);
        this.setFormatter(this::format);
        this.fixate();
    }
    
    public void setSetter(Consumer<Integer> setter) {
        this.setter = setter;
    }
    
    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (!focus) {
            this.fixate();
        }
    }
    
    protected void fixate() {
        int rgb;
        try {
            rgb = decodeColor(this.getValue());
        } catch (NumberFormatException e) {
            rgb = 0;
        }
        this.setRGB(rgb);
    }
    
    public void setRGB(int rgb) {
        this.setValue(String.format("#%06X", rgb));
    }
    
    @Override
    public void setEditable(boolean enabled) {
        super.setEditable(enabled);
        this.isEnabled = enabled;
    }
    
    protected int getSelectionEnd() {
        return this.highlightPos;
    }
    
    @Override
    public void insertText(String text) {
        this.clearSign();
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        int sel = this.getSelectionEnd();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (sel == 0 && i == 0 && c == '#') {
                sb.append(c);
                continue;
            }
            if (NUM_CHARS.indexOf(c) == -1) {
                continue;
            }
            c = Character.toUpperCase(c);
            sb.append(c);
        }
        super.insertText(sb.toString());
    }
    
    protected boolean clearSign() {
        String text = this.getValue();
        int pos = this.getCursorPosition();
        int sel = this.getSelectionEnd();
        boolean skiped = false;
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (NUM_CHARS.indexOf(c) >= 0) {
                sb.append(c);
            } else {
                skiped = true;
                if (pos > i) {
                    --pos;
                }
                if (sel > i) {
                    --sel;
                }
            }
        }
        this.setValue(sb.toString());
        this.moveCursorTo(pos);
        this.setHighlightPos(sel);
        return skiped;
    }
    
    @Override
    public boolean charTyped(char character, int modifiers) {
        String previousText = this.getValue();
        if (!super.charTyped(character, modifiers)) {
            return false;
        }
        this.afterCheck(previousText);
        return true;
    }
    
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        String previousText = this.getValue();
        if (!super.keyPressed(key, scanCode, modifiers)) {
            return false;
        }
        this.afterCheck(previousText);
        return true;
    }
    
    protected void afterCheck(String previousText) {
        this.checkPrefix();
        if (this.setter == null) {
            return;
        }
        String text = this.getValue();
        if (previousText.equals(text)) {
            return;
        }
        Integer previousColor;
        try {
            previousColor = decodeColor(previousText);
        } catch (NumberFormatException e) {
            previousColor = null;
        }
        int color;
        try {
            color = decodeColor(text);
        } catch (NumberFormatException e) {
            color = 0;
        }
        if (previousColor != null && color == previousColor.intValue()) {
            return;
        }
        this.setter.accept(color);
    }
    
    protected boolean checkPrefix() {
        String text = this.getValue();
        if (text.isEmpty() || text.startsWith("#")) {
            return false;
        }
        int pos = this.getCursorPosition();
        int sel = this.getSelectionEnd();
        this.setValue("#" + text);
        this.moveCursorTo(pos + 1);
        this.setHighlightPos(sel + 1);
        return true;
    }
    
    protected IReorderingProcessor format(String text, int pos) {
        List<TextFormatting> colorsList = this.getFormatColors();
        List<TextFormatting> colorsSubList =
                colorsList.subList(Math.min(pos, colorsList.size()), colorsList.size());
        return (consumer)-> {
            Iterator<TextFormatting> it = colorsSubList.iterator();
            int i;
            for (i = 0; i < text.length() && it.hasNext(); ++i) {
                Style style = Style.EMPTY.withColor(it.next());
                String str = String.valueOf(text.charAt(i));
                if (!TextProcessing.iterate(str, style, consumer)) {
                    return false;
                }
            }
            return TextProcessing.iterate(text.substring(i), Style.EMPTY, consumer);
        };
    }
    
    protected List<TextFormatting> getFormatColors() {
        return this.isEnabled ? COLORS_ENABLED : COLORS_DISABLED;
    }
    
    protected static int decodeColor(String text) {
        return Integer.decode(text + "0000000".substring(Math.min(text.length(), 7)));
    }
}
