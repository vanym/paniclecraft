package com.vanym.paniclecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessQueen extends Model {
    ModelRenderer body1;
    ModelRenderer body2;
    ModelRenderer body3;
    ModelRenderer pike1;
    ModelRenderer pike2;
    ModelRenderer pike3;
    ModelRenderer pike4;
    ModelRenderer pike5;
    ModelRenderer pike6;
    ModelRenderer pike7;
    ModelRenderer pike8;
    ModelRenderer body4;
    ModelRenderer pike9;
    
    public ModelChessQueen() {
        super(RenderType::entityCutoutNoCull);
        texWidth = 32;
        texHeight = 16;
        
        body1 = new ModelRenderer(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setPos(0F, 0F, 0F);
        body1.setTexSize(64, 32);
        body1.mirror = true;
        body2 = new ModelRenderer(this, 20, 0);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 6, 3);
        body2.setPos(-9.992007E-15F, -6F, 0F);
        body2.setTexSize(64, 32);
        body2.mirror = true;
        body3 = new ModelRenderer(this, 0, 6);
        body3.addBox(-2.5F, -1F, -2.5F, 5, 1, 5);
        body3.setPos(0F, -6F, 0F);
        body3.setTexSize(64, 32);
        body3.mirror = true;
        pike1 = new ModelRenderer(this, 20, 7);
        pike1.addBox(1.5F, 0F, 1.5F, 1, 1, 1);
        pike1.setPos(0F, -8F, 0F);
        pike1.setTexSize(64, 32);
        pike1.mirror = true;
        pike2 = new ModelRenderer(this, 20, 7);
        pike2.addBox(-0.5F, 0F, 1.5F, 1, 1, 1);
        pike2.setPos(0F, -8F, 0F);
        pike2.setTexSize(64, 32);
        pike2.mirror = true;
        pike3 = new ModelRenderer(this, 20, 7);
        pike3.addBox(-2.5F, 0F, 1.5F, 1, 1, 1);
        pike3.setPos(0F, -8F, 0F);
        pike3.setTexSize(64, 32);
        pike3.mirror = true;
        pike4 = new ModelRenderer(this, 20, 7);
        pike4.addBox(-2.5F, 0F, -0.5F, 1, 1, 1);
        pike4.setPos(0F, -8F, 0F);
        pike4.setTexSize(64, 32);
        pike4.mirror = true;
        pike5 = new ModelRenderer(this, 20, 7);
        pike5.addBox(-2.5F, 0F, -2.5F, 1, 1, 1);
        pike5.setPos(0F, -8F, 0F);
        pike5.setTexSize(64, 32);
        pike5.mirror = true;
        pike6 = new ModelRenderer(this, 20, 7);
        pike6.addBox(-0.5F, 0F, -2.5F, 1, 1, 1);
        pike6.setPos(0F, -8F, 0F);
        pike6.setTexSize(64, 32);
        pike6.mirror = true;
        pike7 = new ModelRenderer(this, 20, 7);
        pike7.addBox(1.5F, 0F, -0.5F, 1, 1, 1);
        pike7.setPos(0F, -8F, 0F);
        pike7.setTexSize(64, 32);
        pike7.mirror = true;
        pike8 = new ModelRenderer(this, 20, 7);
        pike8.addBox(1.5F, 0F, -2.5F, 1, 1, 1);
        pike8.setPos(0F, -8F, 0F);
        pike8.setTexSize(64, 32);
        pike8.mirror = true;
        body4 = new ModelRenderer(this, 24, 9);
        body4.addBox(-1F, 0F, -1F, 2, 2, 2);
        body4.setPos(0F, -9F, 0F);
        body4.setTexSize(32, 16);
        body4.mirror = true;
        pike9 = new ModelRenderer(this, 20, 9);
        pike9.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
        pike9.setPos(0F, -10F, 0F);
        pike9.setTexSize(32, 16);
        pike9.mirror = true;
    }
    
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        body1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike5.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike6.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike7.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike8.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        pike9.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
