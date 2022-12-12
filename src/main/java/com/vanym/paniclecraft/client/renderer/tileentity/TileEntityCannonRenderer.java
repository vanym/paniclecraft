package com.vanym.paniclecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody2;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody3;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityCannonRenderer extends TileEntitySpecialRenderer {
    
    final ResourceLocation Texture = new ResourceLocation(DEF.MOD_ID, "textures/models/cannon.png");
    
    ModelCannonBody body = new ModelCannonBody();
    ModelCannonBody2 body2 = new ModelCannonBody2();
    ModelCannonBody3 body3 = new ModelCannonBody3();
    
    public void renderTileEntityAt(
            TileEntityCannon par1,
            double par2,
            double par4,
            double par6,
            float par8) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float var10 = 0.6666667F;
        GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        this.bindTexture(this.Texture);
        this.body.render(0.0625F);
        GL11.glRotatef((float)par1.direction, 0.0F, 1.0F, 0.0F);
        this.body2.render(0.075F);
        GL11.glTranslatef(0.0F, -0.4F, 0.0F);
        GL11.glRotatef(90F - (float)par1.height, 1.0F, 0.0F, 0.0F);
        this.body3.render(0.075F);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(
            TileEntity par1,
            double par2,
            double par4,
            double par6,
            float par8) {
        this.renderTileEntityAt((TileEntityCannon)par1, par2, par4, par6, par8);
    }
    
}
