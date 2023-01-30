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
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/cannon.png");
    
    protected final ModelCannonBody body = new ModelCannonBody();
    protected final ModelCannonBody2 body2 = new ModelCannonBody2();
    protected final ModelCannonBody3 body3 = new ModelCannonBody3();
    
    public void renderTileEntityAt(
            TileEntityCannon tileCannon,
            double x,
            double y,
            double z,
            float f) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        this.bindTexture(TEXTURE);
        this.body.render(0.0625F);
        GL11.glRotatef((float)tileCannon.getDirection(), 0.0F, 1.0F, 0.0F);
        this.body2.render(0.075F);
        GL11.glTranslatef(0.0F, -0.4F, 0.0F);
        GL11.glRotatef(90.0F - (float)tileCannon.getHeight(), 1.0F, 0.0F, 0.0F);
        this.body3.render(0.075F);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(
            TileEntity tile,
            double x,
            double y,
            double z,
            float f) {
        this.renderTileEntityAt((TileEntityCannon)tile, x, y, z, f);
    }
}
