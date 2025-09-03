package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCannonBody3 extends Model {
    RendererModel Shape1;
    RendererModel Shape2;
    RendererModel Shape3;
    RendererModel Shape4;
    RendererModel Shape5;
    RendererModel Shape6;
    RendererModel Shape7;
    RendererModel Shape8;
    RendererModel Shape9;
    RendererModel Shape10;
    
    public ModelCannonBody3() {
        texWidth = 128;
        texHeight = 64;
        
        Shape1 = new RendererModel(this, 102, 59);
        Shape1.addBox(-2F, 2F, -2F, 4, 1, 4);
        Shape1.setPos(0F, 0F, 0F);
        Shape1.setTexSize(128, 64);
        Shape1.mirror = true;
        Shape2 = new RendererModel(this, 92, 59);
        Shape2.addBox(-2F, -2F, -3F, 4, 4, 1);
        Shape2.setPos(0F, 0F, 0F);
        Shape2.setTexSize(128, 64);
        Shape2.mirror = true;
        Shape3 = new RendererModel(this, 118, 59);
        Shape3.addBox(-2F, -2F, 2F, 4, 4, 1);
        Shape3.setPos(0F, 0F, 0F);
        Shape3.setTexSize(128, 64);
        Shape3.mirror = true;
        Shape4 = new RendererModel(this, 118, 50);
        Shape4.addBox(-3F, -2F, -2F, 1, 4, 4);
        Shape4.setPos(0F, 0F, 0F);
        Shape4.setTexSize(128, 64);
        Shape4.mirror = true;
        Shape5 = new RendererModel(this, 108, 50);
        Shape5.addBox(2F, -2F, -2F, 1, 4, 4);
        Shape5.setPos(0F, 0F, 0F);
        Shape5.setTexSize(128, 64);
        Shape5.mirror = true;
        Shape6 = new RendererModel(this, 112, 45);
        Shape6.addBox(-2F, -3F, -2F, 4, 1, 4);
        Shape6.setPos(0F, 0F, 0F);
        Shape6.setTexSize(128, 64);
        Shape6.mirror = true;
        Shape7 = new RendererModel(this, 100, 50);
        Shape7.addBox(-1F, -9F, 1F, 2, 6, 1);
        Shape7.setPos(0F, 0F, 0F);
        Shape7.setTexSize(128, 64);
        Shape7.mirror = true;
        Shape8 = new RendererModel(this, 100, 50);
        Shape8.addBox(1F, -9F, -1F, 1, 6, 2);
        Shape8.setPos(0F, 0F, 0F);
        Shape8.setTexSize(128, 64);
        Shape8.mirror = true;
        Shape9 = new RendererModel(this, 100, 50);
        Shape9.addBox(-1F, -9F, -2F, 2, 6, 1);
        Shape9.setPos(0F, 0F, 0F);
        Shape9.setTexSize(128, 64);
        Shape9.mirror = true;
        Shape10 = new RendererModel(this, 100, 50);
        Shape10.addBox(-2F, -9F, -1F, 1, 6, 2);
        Shape10.setPos(0F, 0F, 0F);
        Shape10.setTexSize(128, 64);
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
