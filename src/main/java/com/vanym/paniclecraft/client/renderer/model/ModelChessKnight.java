package com.vanym.paniclecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessKnight extends Model {
    ModelRenderer body1;
    ModelRenderer body2;
    ModelRenderer body3;
    ModelRenderer body4;
    ModelRenderer body5;
    ModelRenderer body6;
    
    public ModelChessKnight() {
        super(ModelChessDesk.RENDER_TYPE);
        texWidth = 32;
        texHeight = 16;
        
        body1 = new ModelRenderer(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setPos(0F, 0F, 0F);
        body1.setTexSize(32, 16);
        body1.mirror = true;
        body2 = new ModelRenderer(this, 12, 6);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 1, 3);
        body2.setPos(-9.992007E-15F, -1F, 0F);
        body2.setTexSize(32, 16);
        body2.mirror = true;
        body3 = new ModelRenderer(this, 0, 6);
        body3.addBox(-0.5F, 0F, -0.5F, 1, 2, 1);
        body3.setPos(0F, -3F, 0F);
        body3.setTexSize(32, 16);
        body3.mirror = true;
        body4 = new ModelRenderer(this, 4, 6);
        body4.addBox(-0.5F, 0F, 0.5F, 1, 3, 1);
        body4.setPos(0F, -6F, 0F);
        body4.setTexSize(32, 16);
        body4.mirror = true;
        body5 = new ModelRenderer(this, 8, 6);
        body5.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
        body5.setPos(0F, -7F, 0F);
        body5.setTexSize(32, 16);
        body5.mirror = true;
        body6 = new ModelRenderer(this, 8, 8);
        body6.addBox(-0.5F, 0F, -1.5F, 1, 1, 1);
        body6.setPos(0F, -6F, 0F);
        body6.setTexSize(32, 16);
        body6.mirror = true;
    }
    
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        body1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body5.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body6.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
