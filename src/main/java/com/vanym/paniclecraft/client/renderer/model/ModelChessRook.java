package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessRook extends Model {
    RendererModel body1;
    RendererModel body2;
    RendererModel body3;
    RendererModel pike1;
    RendererModel pike2;
    RendererModel pike3;
    RendererModel pike4;
    RendererModel pike5;
    RendererModel pike6;
    RendererModel pike7;
    RendererModel pike8;
    
    public ModelChessRook() {
        texWidth = 32;
        texHeight = 16;
        
        body1 = new RendererModel(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setPos(0F, 0F, 0F);
        body1.setTexSize(32, 16);
        body1.mirror = true;
        body2 = new RendererModel(this, 20, 0);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 4, 3);
        body2.setPos(-9.992007E-15F, -4F, 0F);
        body2.setTexSize(32, 16);
        body2.mirror = true;
        body3 = new RendererModel(this, 0, 6);
        body3.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body3.setPos(0F, -5F, 0F);
        body3.setTexSize(32, 16);
        body3.mirror = true;
        pike1 = new RendererModel(this, 20, 7);
        pike1.addBox(1.5F, 0F, 1.5F, 1, 1, 1);
        pike1.setPos(0F, -6F, 0F);
        pike1.setTexSize(32, 16);
        pike1.mirror = true;
        pike2 = new RendererModel(this, 20, 7);
        pike2.addBox(-0.5F, 0F, 1.5F, 1, 1, 1);
        pike2.setPos(0F, -6F, 0F);
        pike2.setTexSize(32, 16);
        pike2.mirror = true;
        pike3 = new RendererModel(this, 20, 7);
        pike3.addBox(-2.5F, 0F, 1.5F, 1, 1, 1);
        pike3.setPos(0F, -6F, 0F);
        pike3.setTexSize(32, 16);
        pike3.mirror = true;
        pike4 = new RendererModel(this, 20, 7);
        pike4.addBox(-2.5F, 0F, -0.5F, 1, 1, 1);
        pike4.setPos(0F, -6F, 0F);
        pike4.setTexSize(32, 16);
        pike4.mirror = true;
        pike5 = new RendererModel(this, 20, 7);
        pike5.addBox(-2.5F, 0F, -2.5F, 1, 1, 1);
        pike5.setPos(0F, -6F, 0F);
        pike5.setTexSize(32, 16);
        pike5.mirror = true;
        pike6 = new RendererModel(this, 20, 7);
        pike6.addBox(-0.5F, 0F, -2.5F, 1, 1, 1);
        pike6.setPos(0F, -6F, 0F);
        pike6.setTexSize(32, 16);
        pike6.mirror = true;
        pike7 = new RendererModel(this, 20, 7);
        pike7.addBox(1.5F, 0F, -0.5F, 1, 1, 1);
        pike7.setPos(0F, -6F, 0F);
        pike7.setTexSize(32, 16);
        pike7.mirror = true;
        pike8 = new RendererModel(this, 20, 7);
        pike8.addBox(1.5F, 0F, -2.5F, 1, 1, 1);
        pike8.setPos(0F, -6F, 0F);
        pike8.setTexSize(32, 16);
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
