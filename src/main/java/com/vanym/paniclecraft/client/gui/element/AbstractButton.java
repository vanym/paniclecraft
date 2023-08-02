package com.vanym.paniclecraft.client.gui.element;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractButton extends GuiButton {
    
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
