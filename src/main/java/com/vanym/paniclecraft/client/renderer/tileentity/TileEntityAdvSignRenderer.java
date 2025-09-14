package com.vanym.paniclecraft.client.renderer.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.client.utils.AdvTextInput;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.block.WoodType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityAdvSignRenderer extends TileEntityRenderer<TileEntityAdvSign> {
    
    protected final SignTileEntityRenderer.SignModel modelSign =
            new SignTileEntityRenderer.SignModel();
    
    public TileEntityAdvSignRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    public void render(
            TileEntityAdvSign tileAS,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay,
            boolean statik,
            GuiEditAdvSign gui) {
        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        boolean onStick = tileAS.getForm() == AdvSignForm.STICK_DOWN;
        this.modelSign.stick.visible = onStick;
        if (!statik) {
            float rotation = 0.0F;
            Vector3f yaxis = Vector3f.YP;
            int facing = 1;
            if (tileAS.hasLevel()) {
                facing = tileAS.getBlockState().getValue(BlockAdvSign.FACING).get3DDataValue();
            }
            switch (facing) {
                case 0:
                    ms.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                    ms.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    yaxis = Vector3f.YN;
                break;
                case 4:
                    rotation += 90.0F;
                case 2:
                    rotation += 90.0F;
                case 5:
                    rotation += 90.0F;
                case 3:
                    ms.mulPose(Vector3f.YP.rotationDegrees(rotation));
                    ms.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                break;
            }
            ms.mulPose(yaxis.rotationDegrees(-(float)tileAS.getDirection()));
            if (!onStick) {
                ms.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                ms.translate(0.0F, -0.3125F, -0.4375F);
            }
        }
        IVertexBuilder vertexer =
                Atlases.signTexture(WoodType.OAK).buffer(buffer, this.modelSign::renderType);
        float scale = 0.6666667F;
        ms.pushPose();
        ms.scale(scale, -scale, -scale);
        Color color = tileAS.getStandColor();
        float[] colorf = color.getRGBComponents(null);
        this.modelSign.renderToBuffer(ms, vertexer, combinedLight, combinedOverlay, colorf[0],
                                      colorf[1], colorf[2], colorf[3]);
        ms.popPose();
        Stream.of(true, false)
              .forEach(side->this.renderSignText(tileAS.getSide(side), side,
                                                 ms, buffer, combinedLight, gui));
        ms.popPose();
    }
    
    protected void renderSignText(
            AdvSignText text,
            boolean front,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            GuiEditAdvSign gui) {
        float scale = 0.6666667F;
        FontRenderer font = this.renderer.getFont();
        if (font == null) {
            return;
        }
        List<ITextComponent> lines = text.getLines();
        int size = lines.size();
        float textScale = scale * 0.016666668F * 4.0F / Math.max(1, size);
        ms.pushPose();
        ms.mulPose(Vector3f.YP.rotationDegrees(front ? 0.0F : 180.0F));
        ms.translate(0.0F, 0.5F * scale, 0.07F * scale);
        ms.scale(textScale, -textScale, textScale);
        Color textColor = text.getTextColor();
        for (int i = 0; i < size; ++i) {
            AdvTextInput input = gui != null ? gui.getInput(front, i) : null;
            ITextComponent line = input != null ? input.getComponent() : lines.get(i);
            String colored = line.getColoredString();
            int width = font.width(colored);
            int x = -width / 2;
            int y = i * 10 - size * 5;
            font.drawInBatch(colored, x, y, textColor.getRGB(),
                             false, ms.last().pose(), buffer, false, 0, combinedLight);
            if (input == null) {
                continue;
            }
            int cursorOffset = font.width(FormattingUtils.substring(line, 0, input.getCursorPos())
                                                         .getColoredString());
            int cursorX = x + cursorOffset;
            if (gui.isBlink()) {
                if (input.getCursorPos() < line.getString().length()) {
                    Matrix4f mx = ms.last().pose().copy();
                    mx.translate(new Vector3f(0.0F, 0.0F, 0.002F));
                    GuiUtils.drawFillInBatch(mx, buffer,
                                             cursorX, y - 1,
                                             cursorX + 1, y + font.lineHeight,
                                             textColor.getRed(),
                                             textColor.getGreen(),
                                             textColor.getBlue());
                } else {
                    font.drawInBatch("_", cursorX, y, textColor.getRGB(),
                                     false, ms.last().pose(), buffer, false, 0, combinedLight);
                }
            }
            if (!input.isSelected()) {
                continue;
            }
            int selOffset = font.width(FormattingUtils.substring(line, 0, input.getSelectionPos())
                                                      .getColoredString());
            int selectionX = x + selOffset;
            Matrix4f mx = ms.last().pose().copy();
            mx.translate(new Vector3f(0.0F, 0.0F, 0.003F));
            // TODO: fix that inversion ignores sign model
            GuiUtils.drawHighlightInBatch(mx, buffer,
                                          cursorX, y - 1,
                                          selectionX, y + font.lineHeight);
        }
        ms.popPose();
    }
    
    @Override
    public void render(
            TileEntityAdvSign tileAS,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        this.render(tileAS, partialTicks, ms, buffer, combinedLight, combinedOverlay, false, null);
    }
}
