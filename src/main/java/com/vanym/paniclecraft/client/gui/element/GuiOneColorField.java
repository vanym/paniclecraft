package com.vanym.paniclecraft.client.gui.element;

import java.util.function.Consumer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiOneColorField extends TextFieldWidget {
    
    protected static final String NUM_CHARS = "0123456789";
    
    protected Consumer<Integer> setter;
    
    public GuiOneColorField(FontRenderer font, int x, int y, int width, int height) {
        super(font, x, y, width, height, "");
        this.setMaxStringLength(3);
    }
    
    public void setSetter(Consumer<Integer> setter) {
        this.setter = setter;
    }
    
    @Override
    public void setFocused(boolean focus) {
        super.setFocused(focus);
        if (focus) {
            return;
        }
        int num;
        try {
            num = Integer.decode(this.getText());
        } catch (NumberFormatException e) {
            num = 0;
        }
        this.setText(Integer.toString(num));
    }
    
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
    
    @Override
    public void writeText(String text) {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (NUM_CHARS.indexOf(c) == -1) {
                continue;
            }
            sb.append(c);
        }
        super.writeText(sb.toString());
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
        this.checkNum();
        String text = this.getText();
        if (previousText.equals(text)) {
            return;
        }
        int previousColor;
        int color;
        try {
            previousColor = Integer.decode(previousText);
        } catch (NumberFormatException e) {
            previousColor = 0;
        }
        try {
            color = Integer.decode(text);
        } catch (NumberFormatException e) {
            color = 0;
        }
        if (color == previousColor) {
            return;
        }
        if (this.setter != null) {
            this.setter.accept(color);
        }
    }
    
    protected boolean checkNum() {
        String text = this.getText();
        if (text.isEmpty()) {
            return false;
        }
        int pos = this.getCursorPosition();
        int sel = this.getSelectionEnd();
        int num;
        try {
            num = Integer.decode(text);
        } catch (NumberFormatException e) {
            return false;
        }
        if (num > 0xff) {
            this.setText(Integer.toString(0xff));
            this.setCursorPosition(pos);
            this.setSelectionPos(sel);
            return true;
        }
        if (num < 0) {
            this.setText(Integer.toString(0));
            return true;
        }
        return false;
    }
}
