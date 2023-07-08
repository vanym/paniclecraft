package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessPawn extends Model {
    RendererModel body1;
    RendererModel body2;
    RendererModel body3;
    RendererModel body4;
    
    public ModelChessPawn() {
        textureWidth = 64;
        textureHeight = 32;
        
        body1 = new RendererModel(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setRotationPoint(0F, 0F, 0F);
        body1.setTextureSize(64, 32);
        body1.mirror = true;
        body2 = new RendererModel(this, 20, 0);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 1, 3);
        body2.setRotationPoint(-9.992007E-15F, -1F, 0F);
        body2.setTextureSize(64, 32);
        body2.mirror = true;
        body3 = new RendererModel(this, 0, 6);
        body3.addBox(-0.5F, -3F, -0.5F, 1, 3, 1);
        body3.setRotationPoint(0F, -1F, 0F);
        body3.setTextureSize(64, 32);
        body3.mirror = true;
        body4 = new RendererModel(this, 4, 6);
        body4.addBox(-1F, -1F, -1.033333F, 2, 2, 2);
        body4.setRotationPoint(0F, -5F, 0F);
        body4.setTextureSize(64, 32);
        body4.mirror = true;
        
    }
    
    public void render(float f5) {
        body1.render(f5);
        body2.render(f5);
        body3.render(f5);
        body4.render(f5);
    }
}
