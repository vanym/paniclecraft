package com.vanym.paniclecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessPawn extends Model {
    ModelRenderer body1;
    ModelRenderer body2;
    ModelRenderer body3;
    ModelRenderer body4;
    
    public ModelChessPawn() {
        super(ModelChessDesk.RENDER_TYPE);
        texWidth = 64;
        texHeight = 32;
        
        body1 = new ModelRenderer(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setPos(0F, 0F, 0F);
        body1.setTexSize(64, 32);
        body1.mirror = true;
        body2 = new ModelRenderer(this, 20, 0);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 1, 3);
        body2.setPos(-9.992007E-15F, -1F, 0F);
        body2.setTexSize(64, 32);
        body2.mirror = true;
        body3 = new ModelRenderer(this, 0, 6);
        body3.addBox(-0.5F, -3F, -0.5F, 1, 3, 1);
        body3.setPos(0F, -1F, 0F);
        body3.setTexSize(64, 32);
        body3.mirror = true;
        body4 = new ModelRenderer(this, 4, 6);
        body4.addBox(-1F, -1F, -1.033333F, 2, 2, 2);
        body4.setPos(0F, -5F, 0F);
        body4.setTexSize(64, 32);
        body4.mirror = true;
        
    }
    
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        body1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        body4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
