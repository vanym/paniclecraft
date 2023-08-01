package com.vanym.paniclecraft.client.renderer.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.client.utils.AdvTextInput;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
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
            GuiEditAdvSign gui) {
        GL11.glPushMatrix();
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
        float scale = 0.6666667F;
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
        Stream.of(true, false).forEach(side->this.renderSignText(tileAS.getSide(side), side, gui));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
    
    protected void renderSignText(AdvSignText text, boolean front, GuiEditAdvSign gui) {
        float scale = 0.6666667F;
        FontRenderer font = this.func_147498_b();
        if (font == null) {
            return;
        }
        List<IChatComponent> lines = text.getLines();
        int size = lines.size();
        float textScale = scale * 0.016666668F * 4.0F / Math.max(1, size);
        GL11.glPushMatrix();
        GL11.glRotatef(front ? 0.0F : 180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.5F * scale, 0.07F * scale);
        GL11.glScalef(textScale, -textScale, textScale);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * textScale);
        GL11.glDepthMask(false);
        Color textColor = text.getTextColor();
        for (int i = 0; i < size; ++i) {
            AdvTextInput input = gui != null ? gui.getInput(front, i) : null;
            IChatComponent line = input != null ? input.getComponent() : lines.get(i);
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
                if (input.getCursorPos() < line.getUnformattedText().length()) {
                    Gui.drawRect(cursorX, y - 1, cursorX + 1, y + font.FONT_HEIGHT,
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
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntityAt((TileEntityAdvSign)tile, x, y, z, f, false, true, null);
    }
}
