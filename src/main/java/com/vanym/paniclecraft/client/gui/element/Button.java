package com.vanym.paniclecraft.client.gui.element;

import java.util.Objects;
import java.util.function.Consumer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class Button extends AbstractButton {
    protected final Consumer<GuiButton> onPress;
    
    public Button(int x, int y, int width, int height, String text, Consumer<GuiButton> onPress) {
        super(x, y, width, height, text);
        this.onPress = Objects.requireNonNull(onPress);
    }
    
    public Button(int id, int x, int y, int w, int h, String text, Consumer<GuiButton> onPress) {
        super(id, x, y, w, h, text);
        this.onPress = Objects.requireNonNull(onPress);
    }
    
    @Override
    public void onPress() {
        this.onPress.accept(this);
    }
}
