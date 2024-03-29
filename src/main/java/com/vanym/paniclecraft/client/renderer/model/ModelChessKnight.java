package com.vanym.paniclecraft.client.renderer.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChessKnight extends Model {
    RendererModel body1;
    RendererModel body2;
    RendererModel body3;
    RendererModel body4;
    RendererModel body5;
    RendererModel body6;
    
    public ModelChessKnight() {
        textureWidth = 32;
        textureHeight = 16;
        
        body1 = new RendererModel(this, 0, 0);
        body1.addBox(-2.5F, 0F, -2.5F, 5, 1, 5);
        body1.setRotationPoint(0F, 0F, 0F);
        body1.setTextureSize(32, 16);
        body1.mirror = true;
        body2 = new RendererModel(this, 12, 6);
        body2.addBox(-1.5F, 0F, -1.5F, 3, 1, 3);
        body2.setRotationPoint(-9.992007E-15F, -1F, 0F);
        body2.setTextureSize(32, 16);
        body2.mirror = true;
        body3 = new RendererModel(this, 0, 6);
        body3.addBox(-0.5F, 0F, -0.5F, 1, 2, 1);
        body3.setRotationPoint(0F, -3F, 0F);
        body3.setTextureSize(32, 16);
        body3.mirror = true;
        body4 = new RendererModel(this, 4, 6);
        body4.addBox(-0.5F, 0F, 0.5F, 1, 3, 1);
        body4.setRotationPoint(0F, -6F, 0F);
        body4.setTextureSize(32, 16);
        body4.mirror = true;
        body5 = new RendererModel(this, 8, 6);
        body5.addBox(-0.5F, 0F, -0.5F, 1, 1, 1);
        body5.setRotationPoint(0F, -7F, 0F);
        body5.setTextureSize(32, 16);
        body5.mirror = true;
        body6 = new RendererModel(this, 8, 8);
        body6.addBox(-0.5F, 0F, -1.5F, 1, 1, 1);
        body6.setRotationPoint(0F, -6F, 0F);
        body6.setTextureSize(32, 16);
        body6.mirror = true;
    }
    
    public void render(float f5) {
        body1.render(f5);
        body2.render(f5);
        body3.render(f5);
        body4.render(f5);
        body5.render(f5);
        body6.render(f5);
    }
}
