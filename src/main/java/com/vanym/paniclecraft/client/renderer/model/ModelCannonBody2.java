package com.vanym.paniclecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCannonBody2 extends Model {
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    
    public ModelCannonBody2() {
        super(RenderType::entitySolid);
        texWidth = 128;
        texHeight = 64;
        
        Shape2 = new ModelRenderer(this, 0, 18);
        Shape2.addBox(-4F, 0F, -5F, 8, 1, 10);
        Shape2.setPos(0F, -2F, 0F);
        Shape2.setTexSize(128, 64);
        Shape2.mirror = true;
        Shape3 = new ModelRenderer(this, 0, 29);
        Shape3.addBox(4F, 0F, -4F, 1, 1, 8);
        Shape3.setPos(0F, -2F, 0F);
        Shape3.setTexSize(128, 64);
        Shape3.mirror = true;
        Shape4 = new ModelRenderer(this, 18, 29);
        Shape4.addBox(-5F, 0F, -4F, 1, 1, 8);
        Shape4.setPos(0F, -2F, 0F);
        Shape4.setTexSize(128, 64);
        Shape4.mirror = true;
        Shape5 = new ModelRenderer(this, 36, 18);
        Shape5.addBox(3F, -4F, -4F, 1, 4, 8);
        Shape5.setPos(0F, -2F, 0F);
        Shape5.setTexSize(128, 64);
        Shape5.mirror = true;
        Shape6 = new ModelRenderer(this, 36, 28);
        Shape6.addBox(-4F, -4F, -4F, 1, 4, 8);
        Shape6.setPos(0F, -2F, 0F);
        Shape6.setTexSize(128, 64);
        Shape6.mirror = true;
        Shape7 = new ModelRenderer(this, 0, 38);
        Shape7.addBox(3F, -5F, -3F, 1, 1, 6);
        Shape7.setPos(0F, -2F, 0F);
        Shape7.setTexSize(128, 64);
        Shape7.mirror = true;
        Shape8 = new ModelRenderer(this, 36, 38);
        Shape8.addBox(-4F, -5F, -3F, 1, 1, 6);
        Shape8.setPos(0F, -2F, 0F);
        Shape8.setTexSize(128, 64);
        Shape8.mirror = true;
    }
    
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Shape2.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape3.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape4.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape5.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape6.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape7.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        Shape8.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
