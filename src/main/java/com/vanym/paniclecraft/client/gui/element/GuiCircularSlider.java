package com.vanym.paniclecraft.client.gui.element;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.GuiUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiCircularSlider extends GuiButton {
    
    protected static final ResourceLocation BUTTON_TEXTURES =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/buttonBackground.png");
    
    protected static final Color LINE_COLOR = new Color(0x6F, 0x6F, 0x6F);
    protected static final int SEGMENTS = 64;
    
    protected double offset = 0.0D;
    protected double max = 1.0D;
    
    protected Supplier<Double> getter;
    protected Consumer<Double> setter;
    
    protected boolean pressed = false;
    
    public GuiCircularSlider(int id,
            int x,
            int y,
            int width,
            int height) {
        super(id, x, y, width, height, "");
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
    public void drawButton(Minecraft mc, int x, int y) {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                                 GL11.GL_TRUE, GL11.GL_FALSE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_POLYGON);
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.xPosition + raduish, ycenter = this.yPosition + raduisv;
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
            tessellator.addVertexWithUV(vx, vy, this.zLevel, tx, ty);
        }
        tessellator.addVertexWithUV(xcenter, ycenter, this.zLevel, txc, tyc);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
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
    public boolean mousePressed(Minecraft mc, int x, int y) {
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.xPosition + raduish, ycenter = this.yPosition + raduisv;
        double dx = x - xcenter, dy = y - ycenter;
        this.pressed = Math.pow(dx, 2) / Math.pow(raduish, 2) +
                       Math.pow(dy, 2) / Math.pow(raduisv, 2) <= 1.0D
            && this.fromRadians(Math.atan2(dy, dx)) <= this.max;
        if (this.pressed) {
            this.mouseDragged(mc, x, y);
        }
        return this.pressed;
    }
    
    @Override
    public void mouseReleased(int x, int y) {
        this.mouseDragged(Minecraft.getMinecraft(), x, y);
        this.pressed = false;
    }
    
    @Override
    public void mouseDragged(Minecraft mc, int x, int y) {
        if (!this.pressed || this.setter == null) {
            return;
        }
        final double raduish = this.width / 2.0D, raduisv = this.height / 2.0D;
        final double xcenter = this.xPosition + raduish, ycenter = this.yPosition + raduisv;
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
    
    protected double toRadians(double value) {
        return (value + this.offset) * (2 * Math.PI);
    }
    
    protected double fromRadians(double rad) {
        double value = -this.offset + (rad / (2 * Math.PI));
        return (value + 1.0D) % 1.0D;
    }
}
