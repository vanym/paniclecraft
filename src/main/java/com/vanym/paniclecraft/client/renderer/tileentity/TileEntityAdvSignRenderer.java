package com.vanym.paniclecraft.client.renderer.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.client.utils.AdvTextInput;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.SignModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityAdvSignRenderer extends TileEntityRenderer<TileEntityAdvSign> {
    
    protected static final ResourceLocation TEXTURE = SignTileEntityRenderer.field_217659_c;
    
    protected final SignModel modelSign = new SignModel();
    
    public void render(
            TileEntityAdvSign tileAS,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            boolean statik,
            boolean inWorld,
            GuiEditAdvSign gui) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        this.modelSign.getSignStick().showModel = tileAS.onStick();
        if (!statik) {
            float rotation = 0.0F;
            float yaxis = 1.0F;
            switch (tileAS.hasWorld() ? tileAS.getBlockState().get(BlockAdvSign.FACING).getIndex()
                                      : 1) {
                case 0:
                    GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    yaxis *= -1.0F;
                break;
                case 4:
                    rotation += 90.0F;
                case 2:
                    rotation += 90.0F;
                case 5:
                    rotation += 90.0F;
                case 3:
                    GlStateManager.rotatef(rotation, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            }
            GlStateManager.rotatef(-(float)tileAS.getDirection(), 0.0F, yaxis, 0.0F);
            if (!tileAS.onStick()) {
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
            }
        }
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            this.bindTexture(TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                             GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                             GlStateManager.SourceFactor.ONE,
                                             GlStateManager.DestFactor.ZERO);
        }
        if (inWorld) {
            GlStateManager.enableRescaleNormal();
        }
        float scale = 0.6666667F;
        GlStateManager.pushMatrix();
        GlStateManager.scalef(scale, -scale, -scale);
        if (destroyStage < 0) {
            Color color = tileAS.getStandColor();
            float[] colorf = color.getRGBComponents(null);
            GlStateManager.color4f(colorf[0], colorf[1], colorf[2], colorf[3]);
        }
        this.modelSign.renderSign();
        GlStateManager.popMatrix();
        if (destroyStage < 0) {
            Stream.of(true, false)
                  .forEach(side->this.renderSignText(tileAS.getSide(side), side, gui));
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
    
    protected void renderSignText(AdvSignText text, boolean front, GuiEditAdvSign gui) {
        float scale = 0.6666667F;
        FontRenderer font = this.getFontRenderer();
        if (font == null) {
            return;
        }
        List<ITextComponent> lines = text.getLines();
        int size = lines.size();
        float textScale = scale * 0.016666668F * 4.0F / Math.max(1, size);
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(front ? 0.0F : 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, 0.5F * scale, 0.07F * scale);
        GlStateManager.scalef(textScale, -textScale, textScale);
        GlStateManager.normal3f(0.0F, 0.0F, -1.0F * textScale);
        GlStateManager.depthMask(false);
        Color textColor = text.getTextColor();
        for (int i = 0; i < size; ++i) {
            AdvTextInput input = gui != null ? gui.getInput(front, i) : null;
            ITextComponent line = input != null ? input.getComponent() : lines.get(i);
            String colored = line.getFormattedText();
            int width = font.getStringWidth(colored);
            int x = -width / 2;
            int y = i * 10 - size * 5;
            font.drawString(colored, x, y, textColor.getRGB());
            if (input == null) {
                continue;
            }
            int cursorOffset =
                    font.getStringWidth(FormattingUtils.substring(line, 0, input.getCursorPos())
                                                       .getFormattedText());
            int cursorX = x + cursorOffset;
            if (gui.isBlink()) {
                if (input.getCursorPos() < line.getString().length()) {
                    AbstractGui.fill(cursorX, y - 1, cursorX + 1, y + font.FONT_HEIGHT,
                                     0xff000000 | textColor.getRGB());
                } else {
                    font.drawString("_", cursorX, y, textColor.getRGB());
                }
            }
            if (!input.isSelected()) {
                continue;
            }
            int selOffset =
                    font.getStringWidth(FormattingUtils.substring(line, 0, input.getSelectionPos())
                                                       .getFormattedText());
            int selectionX = x + selOffset;
            GuiUtils.drawHighlight(cursorX, y - 1, selectionX, y + font.FONT_HEIGHT);
        }
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
    
    @Override
    public void render(
            TileEntityAdvSign tileAS,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage) {
        this.render(tileAS, x, y, z, partialTicks, destroyStage, false, true, null);
    }
}
