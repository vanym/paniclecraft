package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.utils.RenderTypeImpl.RS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiUtils {
    
    protected static final RenderType RENDER_TYPE_FILL =
            RenderType.create(DEF.MOD_ID + ":fill",
                              DefaultVertexFormats.POSITION_COLOR,
                              GL11.GL_QUADS, 256,
                              false, true,
                              RenderType.State.builder()
                                              .setAlphaState(RS.DEFAULT_ALPHA)
                                              .setTransparencyState(RS.TRANSLUCENT_TRANSPARENCY)
                                              .createCompositeState(false));
    
    protected static final RenderState.TexturingState RENDER_TEXTURING_STATE_HIGHLIGHT =
            new RenderState.TexturingState(
                    "highlight_texturing",
                    ()-> {
                        RenderSystem.enableColorLogicOp();
                        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    },
                    ()-> {
                        RenderSystem.disableColorLogicOp();
                    });
    
    protected static final RenderType RENDER_TYPE_HIGHLIGHT =
            RenderType.create(DEF.MOD_ID + ":highlight",
                              DefaultVertexFormats.POSITION_COLOR,
                              GL11.GL_QUADS, 256,
                              true, false,
                              RenderType.State.builder()
                                              .setTexturingState(RENDER_TEXTURING_STATE_HIGHLIGHT)
                                              .setAlphaState(RS.DEFAULT_ALPHA)
                                              .setTransparencyState(RS.TRANSLUCENT_TRANSPARENCY)
                                              .createCompositeState(false));
    
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
            MatrixStack ms,
            FontRenderer font,
            String line,
            int x,
            int y,
            int textColor) {
        drawString8xOutline(ms, font, line, x, y, textColor, ~textColor & 0xffffff);
    }
    
    public static void drawString8xOutline(
            MatrixStack ms,
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
                         .map(font.fonts.apply(Style.DEFAULT_FONT)::getGlyphInfo)
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
                ms.pushPose();
                ms.translate(px * min, py * min, 0.0D);
                font.draw(ms, line, x, y, outlineColor);
                ms.popPose();
            }
        }
        font.draw(ms, line, x, y, textColor);
    }
    
    public static void drawFillInBatch(
            Matrix4f mx,
            IRenderTypeBuffer buffer,
            float x1,
            float y1,
            float x2,
            float y2,
            int red,
            int green,
            int blue) {
        if (x1 > x2) {
            float xt = x2;
            x2 = x1;
            x1 = xt;
        }
        if (y1 > y2) {
            float yt = y2;
            y2 = y1;
            y1 = yt;
        }
        IVertexBuilder vx = buffer.getBuffer(RENDER_TYPE_FILL);
        vx.vertex(mx, x1, y2, 0.0F).color(red, green, blue, 255).endVertex();
        vx.vertex(mx, x2, y2, 0.0F).color(red, green, blue, 255).endVertex();
        vx.vertex(mx, x2, y1, 0.0F).color(red, green, blue, 255).endVertex();
        vx.vertex(mx, x1, y1, 0.0F).color(red, green, blue, 255).endVertex();
    }
    
    public static void drawHighlightInBatch(
            Matrix4f mx,
            IRenderTypeBuffer buffer,
            float x1,
            float y1,
            float x2,
            float y2) {
        if (x1 > x2) {
            float xt = x2;
            x2 = x1;
            x1 = xt;
        }
        if (y1 > y2) {
            float yt = y2;
            y2 = y1;
            y1 = yt;
        }
        IVertexBuilder vx = buffer.getBuffer(RENDER_TYPE_HIGHLIGHT);
        vx.vertex(mx, x1, y2, 0.0F).color(0, 0, 255, 255).endVertex();
        vx.vertex(mx, x2, y2, 0.0F).color(0, 0, 255, 255).endVertex();
        vx.vertex(mx, x2, y1, 0.0F).color(0, 0, 255, 255).endVertex();
        vx.vertex(mx, x1, y1, 0.0F).color(0, 0, 255, 255).endVertex();
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
        mc.gui.setOverlayMessage(line, false);
    }
}
