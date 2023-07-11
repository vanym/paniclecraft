package com.vanym.paniclecraft.client.gui.element;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCircularSlider extends Widget {
    
    protected static final ResourceLocation BUTTON_TEXTURES =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/button_background.png");
    
    protected static final Color LINE_COLOR = new Color(0x6F, 0x6F, 0x6F);
    protected static final int SEGMENTS = 64;
    
    protected double offset = 0.0D;
    protected double max = 1.0D;
    
    protected Supplier<Double> getter;
    protected Consumer<Double> setter;
    
    protected boolean pressed = false;
    
    public GuiCircularSlider(
            int x,
            int y,
            int width,
            int height) {
        super(x, y, width, height, "");
    }
    
    public void setGetter(Supplier<Double> getter) {
        this.getter = getter;
    }
    
    public void setSetter(Consumer<Double> setter) {
        this.setter = setter;
    }
    
    public double getOffset() {
        return this.offset;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public void setOffset(double offset) {
        this.offset = offset % 1.0D;
    }
    
    public void setMax(double max) {
        this.max = Math.max(0.0D, Math.min(1.0D, max));
    }
    
    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.x + raduish, ycenter = this.y + raduisv;
        final int txc = 99, tyc = 128;
        final double step = this.max / SEGMENTS;
        for (double current = this.max; current >= 0.0D; current -= step) {
            double crad = (current + this.offset) * (2 * Math.PI);
            final double ox = Math.cos(crad) * raduish;
            final double oy = Math.sin(crad) * raduisv;
            final double vx = xcenter + ox;
            final double vy = ycenter + oy;
            final double tx = (txc + ox) / 256.0D;
            final double ty = (tyc + oy) / 256.0D;
            buf.pos(vx, vy, this.blitOffset).tex(tx, ty).endVertex();
        }
        buf.pos(xcenter, ycenter, this.blitOffset).tex(txc, tyc).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        if (this.getter == null) {
            return;
        }
        Double value = this.getter.get();
        if (value == null) {
            return;
        }
        double vrad = this.toRadians(value);
        double xLineCenter = xcenter - 0.5D, yLineCenter = ycenter - 0.5D;
        GuiUtils.drawLine(xLineCenter, yLineCenter,
                          xLineCenter + Math.cos(vrad) * (raduish - 0.5D),
                          yLineCenter + Math.sin(vrad) * (raduisv - 0.5D),
                          LINE_COLOR);
    }
    
    @Override
    public boolean clicked(double x, double y) {
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.x + raduish, ycenter = this.y + raduisv;
        double dx = x - xcenter, dy = y - ycenter;
        return Math.pow(dx, 2) / Math.pow(raduish, 2) +
               Math.pow(dy, 2) / Math.pow(raduisv, 2) <= 1.0D
            && this.fromRadians(Math.atan2(dy, dx)) <= this.max;
    }
    
    @Override
    public void onClick(double x, double y) {
        this.pressed = true;
        this.onDrag(x, y, x, y);
    }
    
    @Override
    public void onRelease(double x, double y) {
        this.onDrag(x, y, x, y);
        this.pressed = false;
    }
    
    @Override
    public void onDrag(double x, double y, double dragX, double dragY) {
        if (!this.pressed || this.setter == null) {
            return;
        }
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.x + raduish, ycenter = this.y + raduisv;
        double value = this.fromRadians(Math.atan2(y - ycenter, x - xcenter));
        if (value > this.max) {
            double left = 1.0D - this.max;
            if (value - this.max > left / 2.0D) {
                value = 0.0D;
            } else {
                value = this.max;
            }
        }
        this.setter.accept(value);
    }
    
    public boolean isPressed() {
        return this.pressed;
    }
    
    protected double toRadians(double value) {
        return (value + this.offset) * (2 * Math.PI);
    }
    
    protected double fromRadians(double rad) {
        double value = -this.offset + (rad / (2 * Math.PI));
        return (value + 1.0D) % 1.0D;
    }
}
