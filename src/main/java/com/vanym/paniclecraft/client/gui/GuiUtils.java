package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUtils {
    
    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                            GlStateManager.SourceFactor.ONE,
                                            GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        float[] f = color.getRGBComponents(null);
        GlStateManager.color(f[0], f[1], f[2], f[3]);
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
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
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
}
