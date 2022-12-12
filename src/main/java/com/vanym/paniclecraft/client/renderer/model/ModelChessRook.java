package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChessRook extends ModelBase {
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
    
    public ModelChessRook() {
        textureWidth = 32;
        textureHeight = 16;
        
        body1 = new ModelRenderer(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setRotationPoint(0F, 0F, 0F);
        body1.setTextureSize(32, 16);
        body1.mirror = true;
        body2 = new ModelRenderer(this, 20, 0);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 4, 3);
        body2.setRotationPoint(-9.992007E-15F, -4F, 0F);
        body2.setTextureSize(32, 16);
        body2.mirror = true;
        body3 = new ModelRenderer(this, 0, 6);
        body3.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body3.setRotationPoint(0F, -5F, 0F);
        body3.setTextureSize(32, 16);
        body3.mirror = true;
        pike1 = new ModelRenderer(this, 20, 7);
        pike1.addBox(1.5F, 0F, 1.5F, 1, 1, 1);
        pike1.setRotationPoint(0F, -6F, 0F);
        pike1.setTextureSize(32, 16);
        pike1.mirror = true;
        pike2 = new ModelRenderer(this, 20, 7);
        pike2.addBox(-0.5F, 0F, 1.5F, 1, 1, 1);
        pike2.setRotationPoint(0F, -6F, 0F);
        pike2.setTextureSize(32, 16);
        pike2.mirror = true;
        pike3 = new ModelRenderer(this, 20, 7);
        pike3.addBox(-2.5F, 0F, 1.5F, 1, 1, 1);
        pike3.setRotationPoint(0F, -6F, 0F);
        pike3.setTextureSize(32, 16);
        pike3.mirror = true;
        pike4 = new ModelRenderer(this, 20, 7);
        pike4.addBox(-2.5F, 0F, -0.5F, 1, 1, 1);
        pike4.setRotationPoint(0F, -6F, 0F);
        pike4.setTextureSize(32, 16);
        pike4.mirror = true;
        pike5 = new ModelRenderer(this, 20, 7);
        pike5.addBox(-2.5F, 0F, -2.5F, 1, 1, 1);
        pike5.setRotationPoint(0F, -6F, 0F);
        pike5.setTextureSize(32, 16);
        pike5.mirror = true;
        pike6 = new ModelRenderer(this, 20, 7);
        pike6.addBox(-0.5F, 0F, -2.5F, 1, 1, 1);
        pike6.setRotationPoint(0F, -6F, 0F);
        pike6.setTextureSize(32, 16);
        pike6.mirror = true;
        pike7 = new ModelRenderer(this, 20, 7);
        pike7.addBox(1.5F, 0F, -0.5F, 1, 1, 1);
        pike7.setRotationPoint(0F, -6F, 0F);
        pike7.setTextureSize(32, 16);
        pike7.mirror = true;
        pike8 = new ModelRenderer(this, 20, 7);
        pike8.addBox(1.5F, 0F, -2.5F, 1, 1, 1);
        pike8.setRotationPoint(0F, -6F, 0F);
        pike8.setTextureSize(32, 16);
        pike8.mirror = true;
    }
    
    public void render(float f5) {
        body1.render(f5);
        body2.render(f5);
        body3.render(f5);
        pike1.render(f5);
        pike2.render(f5);
        pike3.render(f5);
        pike4.render(f5);
        pike5.render(f5);
        pike6.render(f5);
        pike7.render(f5);
        pike8.render(f5);
    }
}
