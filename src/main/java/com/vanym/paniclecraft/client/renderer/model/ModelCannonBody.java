package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCannonBody extends Model {
    RendererModel Shape1;
    
    public ModelCannonBody() {
        textureWidth = 128;
        textureHeight = 64;
        
        Shape1 = new RendererModel(this, 0, 0);
        Shape1.addBox(-8F, -1F, -8F, 16, 1, 16);
        Shape1.setRotationPoint(0F, 0F, 0F);
        Shape1.setTextureSize(128, 64);
        Shape1.mirror = true;
    }
    
    public void render(float f5) {
        Shape1.render(f5);
    }
}
