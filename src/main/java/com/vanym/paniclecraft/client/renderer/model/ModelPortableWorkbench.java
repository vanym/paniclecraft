package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPortableWorkbench extends Model {
    RendererModel Shape1;
    RendererModel Shape2;
    RendererModel Shape3;
    RendererModel Shape4;
    RendererModel Shape5;
    RendererModel Shape6;
    RendererModel Shape7;
    
    public ModelPortableWorkbench() {
        texWidth = 64;
        texHeight = 32;
        
        Shape1 = new RendererModel(this, 0, 0);
        Shape1.addBox(-8F, -2F, -8F, 16, 3, 16);
        Shape1.setPos(0F, 0F, 0F);
        Shape1.setTexSize(64, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new RendererModel(this, 0, 19);
        Shape2.addBox(-9F, -1F, 5F, 1, 1, 1);
        Shape2.setPos(0F, 0F, 0F);
        Shape2.setTexSize(64, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape3 = new RendererModel(this, 0, 21);
        Shape3.addBox(-9F, -1F, -5F, 1, 1, 1);
        Shape3.setPos(0F, 0F, 0F);
        Shape3.setTexSize(64, 32);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape4 = new RendererModel(this, 40, 19);
        Shape4.addBox(-10F, -1F, -5F, 1, 1, 11);
        Shape4.setPos(0F, 0F, 0F);
        Shape4.setTexSize(64, 32);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0F);
        Shape5 = new RendererModel(this, 0, 23);
        Shape5.addBox(8F, -1F, 5F, 1, 1, 1);
        Shape5.setPos(0F, 0F, 0F);
        Shape5.setTexSize(64, 32);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, 0F);
        Shape6 = new RendererModel(this, 0, 25);
        Shape6.addBox(8F, -1F, -5F, 1, 1, 1);
        Shape6.setPos(0F, 0F, 0F);
        Shape6.setTexSize(64, 32);
        Shape6.mirror = true;
        setRotation(Shape6, 0F, 0F, 0F);
        Shape7 = new RendererModel(this, 16, 19);
        Shape7.addBox(9F, -1F, -5F, 1, 1, 11);
        Shape7.setPos(0F, 0F, 0F);
        Shape7.setTexSize(64, 32);
        Shape7.mirror = true;
        setRotation(Shape7, 0F, 0F, 0F);
    }
    
    public void render(float f5) {
        Shape1.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
        Shape4.render(f5);
        Shape5.render(f5);
        Shape6.render(f5);
        Shape7.render(f5);
    }
    
    private void setRotation(RendererModel model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }
}
