package com.vanym.paniclecraft.client.gui.element;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHexColorField extends TextFieldWidget {
    
    protected static final String NUM_CHARS = "0123456789ABCDEFabcdef";
    
    protected static final List<String> COLORS_ENABLED = Arrays.asList("\u00a79", "\u00a79",
                                                                       "\u00a7a", "\u00a7a",
                                                                       "\u00a7c", "\u00a7c");
    
    protected static final List<String> COLORS_DISABLED = Arrays.asList("\u00a71", "\u00a71",
                                                                        "\u00a72", "\u00a72",
                                                                        "\u00a74", "\u00a74");
    
    protected boolean isEnabled = true;
    
    protected Consumer<Integer> setter;
    
    public GuiHexColorField(FontRenderer font, int x, int y) {
        this(font, x, y, 50, 12);
    }
    
    public GuiHexColorField(FontRenderer font, int x, int y, int width, int height) {
        super(font, x, y, width, height, "");
        this.setMaxStringLength(7);
        this.fixate();
    }
    
    public void setSetter(Consumer<Integer> setter) {
        this.setter = setter;
    }
    
    @Override
    public void setFocused2(boolean focus) {
        super.setFocused2(focus);
        if (!focus) {
            this.fixate();
        }
    }
    
    protected void fixate() {
        int rgb;
        try {
            rgb = Integer.decode(this.getText());
        } catch (NumberFormatException e) {
            rgb = 0;
        }
        this.setRGB(rgb);
    }
    
    public void setRGB(int rgb) {
        this.setText(String.format("#%06X", rgb));
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.isEnabled = enabled;
    }
    
    protected int getSelectionEnd() {
        return this.selectionEnd;
    }
    
    @Override
    public void writeText(String text) {
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
        super.writeText(sb.toString());
    }
    
    protected boolean clearSign() {
        String text = this.getText();
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
        this.setText(sb.toString());
        this.setCursorPosition(pos);
        this.setSelectionPos(sel);
        return skiped;
    }
    
    @Override
    public boolean charTyped(char character, int modifiers) {
        String previousText = this.getText();
        if (!super.charTyped(character, modifiers)) {
            return false;
        }
        this.afterCheck(previousText);
        return true;
    }
    
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        String previousText = this.getText();
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
        String text = this.getText();
        if (previousText.equals(text)) {
            return;
        }
        Integer previousColor;
        try {
            previousColor = Integer.decode(previousText);
        } catch (NumberFormatException e) {
            previousColor = null;
        }
        int color;
        try {
            color = Integer.decode(text);
        } catch (NumberFormatException e) {
            color = 0;
        }
        if (previousColor != null && color == previousColor.intValue()) {
            return;
        }
        this.setter.accept(color);
    }
    
    protected boolean checkPrefix() {
        String text = this.getText();
        if (text.isEmpty() || text.startsWith("#")) {
            return false;
        }
        int pos = this.getCursorPosition();
        int sel = this.getSelectionEnd();
        this.setText("#" + text);
        this.setCursorPosition(pos + 1);
        this.setSelectionPos(sel + 1);
        return true;
    }
    
    @Override
    public void renderButton(int x, int y, float partialTicks) {
        if (!this.getVisible()) {
            return;
        }
        int pos = this.getCursorPosition();
        int sel = this.getSelectionEnd();
        String text = this.getText();
        String textNum = this.getText();
        Iterator<String> it;
        if (this.isEnabled) {
            it = COLORS_ENABLED.iterator();
        } else {
            it = COLORS_DISABLED.iterator();
        }
        StringBuilder sb = new StringBuilder(textNum);
        int length = sb.length();
        for (int i = length - 1; i >= 1; --i) {
            String colorCode = it.hasNext() ? it.next() : "\u00a7f";
            sb.insert(i, colorCode);
        }
        this.setMaxStringLength(7 + Math.max(0, length - 1) * 2);
        this.setText(sb.toString());
        this.setCursorPosition(convertPos(pos));
        this.setSelectionPos(convertPos(sel));
        super.renderButton(x, y, partialTicks);
        this.setText(text);
        this.setMaxStringLength(7);
        this.setCursorPosition(pos);
        this.setSelectionPos(sel);
    }
    
    protected static int convertPos(int pos) {
        return pos + Math.max(0, pos - 1) * 2;
    }
}
