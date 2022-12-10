package ee_man.mod3.client.renderer.tileentity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.tileentity.TileEntityPainting;
import ee_man.mod3.utils.Painting;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingRenderer extends TileEntitySpecialRenderer{
	
	private static final ResourceLocation Texture = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
	
	// private static int texId = 0;
	
	public void renderTileEntityAt(TileEntityPainting par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		// GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		int md = par1.getBlockMetadata();
		float var12 = 0.0F;
		float var13 = 0.0F;
		
		switch(md){
			case 0:
				var13 = -90.0F;
			break;
			case 1:
				var13 = 90.0F;
			break;
			case 2:
				var12 = 0.0F;
			break;
			case 3:
				var12 = 180.0F;
			break;
			case 4:
				var12 = 90.0F;
			break;
			case 5:
				var12 = -90.0F;
			break;
		}
		
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.0F, 0.46875F);
		GL11.glPushMatrix();
		float var11 = 0.0625F;
		GL11.glScalef(var11, var11, var11);
		
		Painting picture = par1.getPainting(md);
		if(picture.texID <= 0){
			picture.texID = GL11.glGenTextures();
			picture.getPic();
		}
		if(picture.hasPic()){
			ByteBuffer textureBuffer = ByteBuffer.allocateDirect(par1.getPainting(md).getPic().length);
			textureBuffer.order(ByteOrder.nativeOrder());
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			textureBuffer.clear();
			textureBuffer.put(picture.getPic());
			textureBuffer.flip();
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, par1.getPainting(md).getRow(), par1.getPainting(md).getRow(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffer);
			picture.delPic();
		}
		else
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
		draw();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity par1, double par2, double par4, double par6, float par8){
		renderTileEntityAt((TileEntityPainting)par1, par2, par4, par6, par8);
	}
	
	public void draw(){
		int i = 16;
		int j = 16;
		int k = 0;
		int l = 0;
		float f = (float)(-i) / 2.0F;
		float f1 = (float)(-j) / 2.0F;
		float f2 = -0.5F;
		float f3 = 0.5F;
		int i1 = 0;
		int j1 = 0;
		
		float f4 = f + (float)((i1 + 1) * 16);
		float f5 = f + (float)(i1 * 16);
		float f6 = f1 + (float)((j1 + 1) * 16);
		float f7 = f1 + (float)(j1 * 16);
		
		float f8 = (float)((k + i) - i1 * 16) / 16F;
		float f9 = (float)((k + i) - (i1 + 1) * 16) / 16F;
		float f10 = (float)((l + j) - j1 * 16) / 16F;
		float f11 = (float)((l + j) - (j1 + 1) * 16) / 16F;
		
		float f12 = 0.75F;
		float f13 = 0.8125F;
		float f14 = 0.0F;
		float f15 = 0.0625F;
		float f16 = 0.75F;
		float f17 = 0.8125F;
		float f18 = 0.001953125F;
		float f19 = 0.001953125F;
		float f20 = 0.7519531F;
		float f21 = 0.7519531F;
		float f22 = 0.0F;
		float f23 = 0.0625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		tessellator.addVertexWithUV(f4, f7, f2, f9, f10);
		tessellator.addVertexWithUV(f5, f7, f2, f8, f10);
		tessellator.addVertexWithUV(f5, f6, f2, f8, f11);
		tessellator.addVertexWithUV(f4, f6, f2, f9, f11);
		tessellator.draw();
		this.bindTexture(Texture);
		tessellator.startDrawingQuads();
		// Зад
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(f4, f6, f3, f12, f14);
		tessellator.addVertexWithUV(f5, f6, f3, f13, f14);
		tessellator.addVertexWithUV(f5, f7, f3, f13, f15);
		tessellator.addVertexWithUV(f4, f7, f3, f12, f15);
		// Верх
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(f4, f6, f2, f16, f18);
		tessellator.addVertexWithUV(f5, f6, f2, f17, f18);
		tessellator.addVertexWithUV(f5, f6, f3, f17, f19);
		tessellator.addVertexWithUV(f4, f6, f3, f16, f19);
		// Низ
		tessellator.setNormal(0.0F, -1F, 0.0F);
		tessellator.addVertexWithUV(f4, f7, f3, f16, f18);
		tessellator.addVertexWithUV(f5, f7, f3, f17, f18);
		tessellator.addVertexWithUV(f5, f7, f2, f17, f19);
		tessellator.addVertexWithUV(f4, f7, f2, f16, f19);
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(f4, f6, f3, f21, f22);
		tessellator.addVertexWithUV(f4, f7, f3, f21, f23);
		tessellator.addVertexWithUV(f4, f7, f2, f20, f23);
		tessellator.addVertexWithUV(f4, f6, f2, f20, f22);
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(f5, f6, f2, f21, f22);
		tessellator.addVertexWithUV(f5, f7, f2, f21, f23);
		tessellator.addVertexWithUV(f5, f7, f3, f20, f23);
		tessellator.addVertexWithUV(f5, f6, f3, f20, f22);
		tessellator.draw();
	}
}
