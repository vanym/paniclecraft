package com.vanym.paniclecraft.client.renderer.tileentity;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityAdvSignRenderer extends TileEntitySpecialRenderer<TileEntityAdvSign> {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/entity/sign.png");
    
    protected final ModelSign modelSign = new ModelSign();
    
    public void render(
            TileEntityAdvSign tileAS,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            boolean statik,
            boolean inWorld,
            int selectLine) {
        GlStateManager.pushMatrix();
        float scale = 0.6666667F;
        GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        this.modelSign.signStick.showModel = tileAS.onStick();
        if (!statik) {
            float rotation = 0.0F;
            float yaxis = 1.0F;
            switch (tileAS.hasWorld() ? tileAS.getBlockMetadata() : 1) {
                case 0:
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    yaxis *= -1.0F;
                break;
                case 4:
                    rotation += 90.0F;
                case 2:
                    rotation += 90.0F;
                case 5:
                    rotation += 90.0F;
                case 3:
                    GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            }
            GlStateManager.rotate(-(float)tileAS.getDirection(), 0.0F, yaxis, 0.0F);
            if (!tileAS.onStick()) {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            }
        }
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            this.bindTexture(TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                                GlStateManager.SourceFactor.ONE,
                                                GlStateManager.DestFactor.ZERO);
        }
        if (inWorld) {
            GlStateManager.enableRescaleNormal();
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, -scale, -scale);
        if (destroyStage < 0) {
            Color color = tileAS.getStandColor();
            float[] colorf = color.getRGBComponents(null);
            GlStateManager.color(colorf[0], colorf[1], colorf[2], colorf[3]);
        }
        this.modelSign.renderSign();
        GlStateManager.popMatrix();
        if (destroyStage < 0) {
            FontRenderer fontRenderer = this.getFontRenderer();
            int size = tileAS.lines.size();
            float textScale = 0.016666668F * scale * 4.0F / Math.max(1, size);
            GlStateManager.translate(0.0F, 0.5F * scale, 0.07F * scale);
            GlStateManager.scale(textScale, -textScale, textScale);
            GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * textScale);
            GlStateManager.depthMask(false);
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
            GlStateManager.depthMask(true);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
    
    @Override
    public void render(
            TileEntityAdvSign tileAS,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        this.render(tileAS, x, y, z, partialTicks, destroyStage, false, true, -1);
    }
}
