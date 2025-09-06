package com.vanym.paniclecraft.client.renderer.model;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCannonBody extends Model {
    public static final Function<ResourceLocation, RenderType> RENDER_TYPE = RenderType::entityCutout;
    
    ModelRenderer Shape1;
    
    public ModelCannonBody() {
        super(RENDER_TYPE);
        texWidth = 128;
        texHeight = 64;
        
        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(-8F, -1F, -8F, 16, 1, 16);
        Shape1.setPos(0F, 0F, 0F);
        Shape1.setTexSize(128, 64);
        Shape1.mirror = true;
    }
    
    public void renderToBuffer(MatrixStack pMatrixStack, IVertexBuilder pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Shape1.render(pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
