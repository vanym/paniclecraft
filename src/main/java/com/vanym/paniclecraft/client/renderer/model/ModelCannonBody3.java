package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCannonBody3 extends ModelBase {
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    
    public ModelCannonBody3() {
        textureWidth = 128;
        textureHeight = 64;
        
        Shape1 = new ModelRenderer(this, 102, 59);
        Shape1.addBox(-2F, 2F, -2F, 4, 1, 4);
        Shape1.setRotationPoint(0F, 0F, 0F);
        Shape1.setTextureSize(128, 64);
        Shape1.mirror = true;
        Shape2 = new ModelRenderer(this, 92, 59);
        Shape2.addBox(-2F, -2F, -3F, 4, 4, 1);
        Shape2.setRotationPoint(0F, 0F, 0F);
        Shape2.setTextureSize(128, 64);
        Shape2.mirror = true;
        Shape3 = new ModelRenderer(this, 118, 59);
        Shape3.addBox(-2F, -2F, 2F, 4, 4, 1);
        Shape3.setRotationPoint(0F, 0F, 0F);
        Shape3.setTextureSize(128, 64);
        Shape3.mirror = true;
        Shape4 = new ModelRenderer(this, 118, 50);
        Shape4.addBox(-3F, -2F, -2F, 1, 4, 4);
        Shape4.setRotationPoint(0F, 0F, 0F);
        Shape4.setTextureSize(128, 64);
        Shape4.mirror = true;
        Shape5 = new ModelRenderer(this, 108, 50);
        Shape5.addBox(2F, -2F, -2F, 1, 4, 4);
        Shape5.setRotationPoint(0F, 0F, 0F);
        Shape5.setTextureSize(128, 64);
        Shape5.mirror = true;
        Shape6 = new ModelRenderer(this, 112, 45);
        Shape6.addBox(-2F, -3F, -2F, 4, 1, 4);
        Shape6.setRotationPoint(0F, 0F, 0F);
        Shape6.setTextureSize(128, 64);
        Shape6.mirror = true;
        Shape7 = new ModelRenderer(this, 100, 50);
        Shape7.addBox(-1F, -9F, 1F, 2, 6, 1);
        Shape7.setRotationPoint(0F, 0F, 0F);
        Shape7.setTextureSize(128, 64);
        Shape7.mirror = true;
        Shape8 = new ModelRenderer(this, 100, 50);
        Shape8.addBox(1F, -9F, -1F, 1, 6, 2);
        Shape8.setRotationPoint(0F, 0F, 0F);
        Shape8.setTextureSize(128, 64);
        Shape8.mirror = true;
        Shape9 = new ModelRenderer(this, 100, 50);
        Shape9.addBox(-1F, -9F, -2F, 2, 6, 1);
        Shape9.setRotationPoint(0F, 0F, 0F);
        Shape9.setTextureSize(128, 64);
        Shape9.mirror = true;
        Shape10 = new ModelRenderer(this, 100, 50);
        Shape10.addBox(-2F, -9F, -1F, 1, 6, 2);
        Shape10.setRotationPoint(0F, 0F, 0F);
        Shape10.setTextureSize(128, 64);
        Shape10.mirror = true;
    }
    
    public void render(float f5) {
        Shape1.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
        Shape4.render(f5);
        Shape5.render(f5);
        Shape6.render(f5);
        Shape7.render(f5);
        Shape8.render(f5);
        Shape9.render(f5);
        Shape10.render(f5);
    }
    
}
