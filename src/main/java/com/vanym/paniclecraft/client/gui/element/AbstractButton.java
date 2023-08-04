package com.vanym.paniclecraft.client.gui.element;

import java.util.Objects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public abstract class AbstractButton extends GuiButton {
    
    public AbstractButton(int x, int y, int width, int height, String text) {
        super(Objects.hash(x, y, width, height) & 0xfffffff | 0x10000,
              x, y, width, height, text);
    }
    
    public AbstractButton(int id, int x, int y, int width, int height, String text) {
        super(id, x, y, width, height, text);
    }
    
    public abstract void onPress();
    
    public static boolean hook(GuiButton button) {
        if (button instanceof AbstractButton) {
            ((AbstractButton)button).onPress();
            return true;
        }
        return false;
    }
}
