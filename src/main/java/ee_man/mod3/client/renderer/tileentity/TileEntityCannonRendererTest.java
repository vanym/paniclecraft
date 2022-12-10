package ee_man.mod3.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DEF;
import ee_man.mod3.client.renderer.model.ModelCannonBody;
import ee_man.mod3.client.renderer.model.ModelCannonBody2;
import ee_man.mod3.client.renderer.model.ModelCannonBody3;
import ee_man.mod3.tileentity.TileEntityCannon;

@SideOnly(Side.CLIENT)
public class TileEntityCannonRendererTest extends TileEntitySpecialRenderer{
	
	final ResourceLocation Texture = new ResourceLocation(DEF.MOD_ID, "textures/models/cannon.png");
	
	ModelCannonBody body = new ModelCannonBody();
	ModelCannonBody2 body2 = new ModelCannonBody2();
	ModelCannonBody3 body3 = new ModelCannonBody3();
	
	public void renderTileEntityAt(TileEntityCannon par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		float updown = 90F;
		float round = 0.0F;
		float b2 = 0.0F;
		float b3 = 0.0F;
		//
		b2 = (float)par1.direction;
		b3 = (float)(90F - par1.height);
		//
		int md = par1.getBlockMetadata();
		if(md == 0){
			updown = 0F;
//			b2 = (float)(180F + par1.direction);
//			b3 = (float)(90F + par1.height);
		}
		if(md == 1){
			updown = 180F;
			b2 = (float)par1.direction;
			b3 = (float)(90F - par1.height);
		}
		if(md == 2){
			
		}
		if(md == 3){
			round = 180.0F;
//			b2 = (float)(180F - par1.height - par1.direction);
//			b3 = (float)(par1.direction + par1.height);
		}
		
		if(md == 4){
			round = 90.0F;
		}
		
		if(md == 5){
			round = -90.0F;
		}
		GL11.glRotatef(round, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(updown, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		this.bindTexture(Texture);
		body.render(0.0625F);
		GL11.glRotatef(b2, 0.0F, 1.0F, 0.0F);
		body2.render(0.075F);
		GL11.glTranslatef(0.0F, -0.4F, 0.0F);
		GL11.glRotatef(b3, 1.0F, 0.0F, 0.0F);
		body3.render(0.075F);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity par1, double par2, double par4, double par6, float par8){
		renderTileEntityAt((TileEntityCannon)par1, par2, par4, par6, par8);
	}
	
}
