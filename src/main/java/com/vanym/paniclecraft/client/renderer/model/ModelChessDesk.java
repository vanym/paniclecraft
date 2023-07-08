package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessDesk extends Model {
    RendererModel Shape1;
    
    public ModelChessDesk() {
        textureWidth = 64;
        textureHeight = 32;
        
        Shape1 = new RendererModel(this, 0, 0);
        Shape1.addBox(-8F, -3F, -8F, 16, 3, 16);
        Shape1.setRotationPoint(0F, 0F, 0F);
        Shape1.setTextureSize(64, 32);
        Shape1.mirror = true;
    }
    
    public void render(float f5) {
        Shape1.render(f5);
    }
}
