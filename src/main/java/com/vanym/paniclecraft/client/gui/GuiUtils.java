package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiUtils {
    
    public static void drawLine(double x1, double y1, double x2, double y2, Color color) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                       GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SourceFactor.ONE,
                                       GlStateManager.DestFactor.ZERO);
        RenderSystem.disableTexture();
        float[] f = color.getRGBComponents(null);
        RenderSystem.color4f(f[0], f[1], f[2], f[3]);
        Tessellator tessellator = Tessellator.getInstance();
        double dx = x2 - x1, dy = y2 - y1, steps = Math.max(Math.abs(dx), Math.abs(dy));
        double x = x1, y = y1, mx = (double)dx / steps, my = (double)dy / steps;
        BufferBuilder buf = tessellator.getBuilder();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        for (int i = 0; i <= steps; ++i, x += mx, y += my) {
            buf.vertex(x + 1, y + 0, 0.0D).endVertex();
            buf.vertex(x + 0, y + 0, 0.0D).endVertex();
            buf.vertex(x + 0, y + 1, 0.0D).endVertex();
            buf.vertex(x + 1, y + 1, 0.0D).endVertex();
        }
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    
    public static void drawHighlight(int x1, int y1, int x2, int y2) {
        new TextFieldWidget(null, 0, 0, Integer.MAX_VALUE, 0, "").renderHighlight(x1, y1, x2, y2);
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
        Stream<Float> offsets =
                IntStream.range(0, line.length())
                         .mapToObj(line::charAt)
                         .filter(c->c != ' ')
                         .map(font.fonts::getGlyphInfo)
                         .map(IGlyph::getShadowOffset);
        float max = 0.0F, min = Float.POSITIVE_INFINITY;
        for (float offset : (Iterable<Float>)offsets::iterator) {
            max = Math.max(max, offset);
            min = Math.min(min, offset);
        }
        final int offsetSize = Math.min(3, Math.round(max / min));
        for (int py = -offsetSize; py <= offsetSize; ++py) {
            for (int px = -offsetSize; px <= offsetSize; ++px) {
                if (px == 0 && py == 0) {
                    continue;
                }
                RenderSystem.pushMatrix();
                RenderSystem.translatef(px * min, py * min, 0.0F);
                font.draw(line, x, y, outlineColor);
                RenderSystem.popMatrix();
            }
        }
        font.draw(line, x, y, textColor);
    }
    
    public static boolean isKeyDown(int key) {
        Minecraft minecraft = Minecraft.getInstance();
        return InputMappings.isKeyDown(minecraft.getWindow().getWindow(), key);
    }
    
    public static void setClipboardString(String string) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.keyboardHandler.setClipboard(string);
    }
    
    public static String getClipboardString() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.keyboardHandler.getClipboard();
    }
    
    public static int getWordPosition(String str, int n, int cursor, boolean skipSpaces) {
        TextFieldWidget field = new TextFieldWidget(null, 0, 0, Integer.MAX_VALUE, 0, "");
        field.setMaxLength(Integer.MAX_VALUE);
        field.setValue(str);
        return field.getWordPosition(n, cursor, skipSpaces);
    }
    
    public static void showFloatingTooltip(ITextComponent line) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.setOverlayMessage(line.getColoredString(), false);
    }
}
