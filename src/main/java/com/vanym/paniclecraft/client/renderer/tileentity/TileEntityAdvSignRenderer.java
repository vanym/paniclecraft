package com.vanym.paniclecraft.client.renderer.tileentity;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityAdvSignRenderer extends TileEntitySpecialRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/entity/sign.png");
    
    protected final ModelSign modelSign = new ModelSign();
    
    public void renderTileEntityAt(
            TileEntityAdvSign tileAS,
            double x,
            double y,
            double z,
            float f,
            boolean statik,
            boolean inWorld,
            int selectLine) {
        GL11.glPushMatrix();
        float scale = 0.6666667F;
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        this.modelSign.signStick.showModel = tileAS.onStick();
        if (!statik) {
            float rotation = 0.0F;
            float yaxis = 1.0F;
            switch (tileAS.getBlockMetadata()) {
                case 0:
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    yaxis *= -1.0F;
                break;
                case 4:
                    rotation += 90.0F;
                case 2:
                    rotation += 90.0F;
                case 5:
                    rotation += 90.0F;
                case 3:
                    GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            }
            GL11.glRotatef(-(float)tileAS.getDirection(), 0.0F, yaxis, 0.0F);
            if (!tileAS.onStick()) {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            }
        }
        this.bindTexture(TEXTURE);
        if (inWorld) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        GL11.glPushMatrix();
        GL11.glScalef(scale, -scale, -scale);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                                 GL11.GL_TRUE, GL11.GL_FALSE);
        Color color = tileAS.getStandColor();
        float[] colorf = color.getRGBComponents(null);
        GL11.glColor4f(colorf[0], colorf[1], colorf[2], colorf[3]);
        this.modelSign.renderSign();
        GL11.glPopMatrix();
        FontRenderer fontRenderer = this.func_147498_b();
        int size = tileAS.lines.size();
        float textScale = 0.016666668F * scale * 4.0F / Math.max(1, size);
        GL11.glTranslatef(0.0F, 0.5F * scale, 0.07F * scale);
        GL11.glScalef(textScale, -textScale, textScale);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * textScale);
        GL11.glDepthMask(false);
        Color textColor = tileAS.getTextColor();
        for (int i = 0; i < size; ++i) {
            String line = tileAS.lines.get(i);
            if (selectLine == i) {
                line = String.format("> %s <", line);
            }
            if (fontRenderer != null) {
                fontRenderer.drawString(line, -fontRenderer.getStringWidth(line) / 2,
                                        i * 10 - size * 5, textColor.getRGB());
            }
        }
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntityAt((TileEntityAdvSign)tile, x, y, z, f, false, true, -1);
    }
}
