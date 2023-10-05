package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class GuiUtils {
    
    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                                 GL11.GL_TRUE, GL11.GL_FALSE);
        float[] f = color.getRGBComponents(null);
        GL11.glColor4f(f[0], f[1], f[2], f[3]);
        Tessellator tessellator = Tessellator.instance;
        double dx = x2 - x1, dy = y2 - y1, steps = Math.max(Math.abs(dx), Math.abs(dy));
        double x = x1, y = y1, mx = (double)dx / steps, my = (double)dy / steps;
        tessellator.startDrawing(GL11.GL_QUADS);
        for (int i = 0; i <= steps; ++i, x += mx, y += my) {
            tessellator.addVertex(x + 1, y + 0, 0.0D);
            tessellator.addVertex(x + 0, y + 0, 0.0D);
            tessellator.addVertex(x + 0, y + 1, 0.0D);
            tessellator.addVertex(x + 1, y + 1, 0.0D);
        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public static void drawHighlight(int x1, int y1, int x2, int y2) {
        new GuiTextField(null, 0, 0, Integer.MAX_VALUE, 0).drawCursorVertical(x1, y1, x2, y2);
    }
    
    public static void drawString8xOutline(
            FontRenderer font,
            String line,
            int x,
            int y,
            int textColor) {
        drawString8xOutline(font, line, x, y, textColor, ~textColor & 0xffffff);
    }
    
    public static void drawString8xOutline(
            FontRenderer font,
            String line,
            int x,
            int y,
            int textColor,
            int outlineColor) {
        final int offsetSize = font.getUnicodeFlag() ? 1 : 2;
        for (int py = -offsetSize; py <= offsetSize; ++py) {
            for (int px = -offsetSize; px <= offsetSize; ++px) {
                if (px == 0 && py == 0) {
                    continue;
                }
                GL11.glPushMatrix();
                GL11.glTranslatef(px * 0.5F, py * 0.5F, 0.0F);
                font.drawString(line, x, y, outlineColor);
                GL11.glPopMatrix();
            }
        }
        font.drawString(line, x, y, textColor);
    }
    
    public static Point2D.Double getMousePoint() {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen screen = mc.currentScreen;
        if (screen == null) {
            return null;
        }
        double x = Mouse.getEventX() * screen.width / (double)mc.displayWidth;
        double y = screen.height - Mouse.getEventY() * screen.height / (double)mc.displayHeight - 1;
        return new Point2D.Double(x, y);
    }
    
    public static Point2D.Double getMousePoint(double x, double y) {
        Point2D.Double p = getMousePoint();
        if (p != null) {
            return p;
        } else {
            return new Point2D.Double(x, y);
        }
    }
    
    public static void setClipboardString(String string) {
        GuiScreen.setClipboardString(string);
    }
    
    public static String getClipboardString() {
        return GuiScreen.getClipboardString();
    }
    
    public static int getWordPosition(String str, int n, int cursor, boolean skipSpaces) {
        GuiTextField field = new GuiTextField(null, 0, 0, Integer.MAX_VALUE, 0);
        field.setMaxStringLength(Integer.MAX_VALUE);
        field.setText(str);
        return field.func_146197_a(n, cursor, skipSpaces);
    }
    
    public static void showFloatingTooltip(IChatComponent line) {
        Minecraft.getMinecraft().ingameGUI.func_110326_a(line.getFormattedText(), false);
    }
}
