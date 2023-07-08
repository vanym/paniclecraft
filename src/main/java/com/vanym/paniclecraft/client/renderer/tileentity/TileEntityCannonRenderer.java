package com.vanym.paniclecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody2;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody3;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityCannonRenderer extends TileEntityRenderer<TileEntityCannon> {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/cannon.png");
    
    protected final ModelCannonBody body = new ModelCannonBody();
    protected final ModelCannonBody2 body2 = new ModelCannonBody2();
    protected final ModelCannonBody3 body3 = new ModelCannonBody3();
    
    @Override
    public void render(
            TileEntityCannon tileCannon,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.0F, 0.5F, 0.0F);
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(8.0F, 4.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            this.bindTexture(TEXTURE);
        }
        this.body.render(0.0625F);
        GlStateManager.rotatef((float)tileCannon.getDirection(), 0.0F, 1.0F, 0.0F);
        this.body2.render(0.075F);
        GlStateManager.translatef(0.0F, -0.4F, 0.0F);
        GlStateManager.rotatef(90.0F - (float)tileCannon.getHeight(), 1.0F, 0.0F, 0.0F);
        this.body3.render(0.075F);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }
}
