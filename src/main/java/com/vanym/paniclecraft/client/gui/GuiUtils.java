package com.vanym.paniclecraft.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiUtils {
    
    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        float[] f = color.getRGBComponents(null);
        GlStateManager.color4f(f[0], f[1], f[2], f[3]);
        Tessellator tessellator = Tessellator.getInstance();
        double dx = x2 - x1, dy = y2 - y1, steps = Math.max(Math.abs(dx), Math.abs(dy));
        double x = x1, y = y1, mx = (double)dx / steps, my = (double)dy / steps;
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (int i = 0; i <= steps; ++i, x += mx, y += my) {
            buf.pos(x + 1, y + 0, 0.0D).endVertex();
            buf.pos(x + 0, y + 0, 0.0D).endVertex();
            buf.pos(x + 0, y + 1, 0.0D).endVertex();
            buf.pos(x + 1, y + 1, 0.0D).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
    
    public static void drawHighlight(int x1, int y1, int x2, int y2) {
        new TextFieldWidget(null, 0, 0, Integer.MAX_VALUE, 0, "").drawSelectionBox(x1, y1, x2, y2);
    }
    
    public static void setClipboardString(String string) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.keyboardListener.setClipboardString(string);
    }
    
    public static String getClipboardString() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.keyboardListener.getClipboardString();
    }
}
