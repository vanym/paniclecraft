package com.vanym.paniclecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityAdvSignRenderer extends TileEntitySpecialRenderer {
    private ModelSign modelSign = new ModelSign();
    
    private final ResourceLocation Texture = new ResourceLocation("textures/entity/sign.png");
    
    public void renderTileEntitySignAt(
            TileEntityAdvSign par1TileEntitySign,
            double par2,
            double par4,
            double par6,
            float par8) {
        BlockAdvSign var9 = (BlockAdvSign)par1TileEntitySign.getBlockType();
        GL11.glPushMatrix();
        // GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float var10 = 0.6666667F;
        float var12;
        
        if (var9 == Core.instance.advSign.blockAdvSignPost) {
            GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
            float var11 = (float)(par1TileEntitySign.getBlockMetadata() * 360) / 16.0F;
            GL11.glRotatef(-var11, 0.0F, 1.0F, 0.0F);
            this.modelSign.signStick.showModel = true;
        } else {
            int var16 = par1TileEntitySign.getBlockMetadata();
            var12 = 0.0F;
            
            if (var16 == 2) {
                var12 = 180.0F;
            }
            
            if (var16 == 4) {
                var12 = 90.0F;
            }
            
            if (var16 == 5) {
                var12 = -90.0F;
            }
            
            GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
            GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            this.modelSign.signStick.showModel = false;
        }
        
        this.bindTexture(this.Texture);
        GL11.glPushMatrix();
        GL11.glScalef(var10, -var10, -var10);
        GL11.glColor4f((float)(par1TileEntitySign.red + 128) / 255.0F,
                       ((float)par1TileEntitySign.green + 128) / 255.0F,
                       ((float)par1TileEntitySign.blue + 128) / 255.0F, 1.0F);
        this.modelSign.renderSign();
        GL11.glPopMatrix();
        FontRenderer var17 = this.func_147498_b();
        var12 = 0.016666668F * var10 * (4F / (float)par1TileEntitySign.getLines());
        GL11.glTranslatef(0.0F, 0.5F * var10, 0.07F * var10);
        GL11.glScalef(var12, -var12, var12);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * var12);
        GL11.glDepthMask(false);
        byte var13 = 0;
        
        if (par1TileEntitySign.signText != null) {
            String[] var20 = par1TileEntitySign.signText.split(TileEntityAdvSign.separator,
                                                               par1TileEntitySign.getLines());
            
            for (int var14 = 0; var14 < par1TileEntitySign.getLines(); ++var14) {
                String var15 = var20[var14];
                
                if (var14 == par1TileEntitySign.lineBeingEdited) {
                    var15 = "> " + var15 + " <";
                }
                if (var17 != null) {
                    var17.drawString(var15, -var17.getStringWidth(var15) / 2,
                                     var14 * 10 - par1TileEntitySign.getLines() * 5, var13);
                }
            }
        }
        
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(
            TileEntity par1TileEntity,
            double par2,
            double par4,
            double par6,
            float par8) {
        this.renderTileEntitySignAt((TileEntityAdvSign)par1TileEntity, par2, par4, par6, par8);
    }
}
