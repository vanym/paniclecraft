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
import ee_man.mod3.tileentity.TileEntityPaintingFrame;
import ee_man.mod3.utils.Painting;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingFrameRenderer extends TileEntitySpecialRenderer{
	
	private static final ResourceLocation Texture = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
	
	// private static int texId = 0;
	
	public void renderTileEntityAt(TileEntityPaintingFrame par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		// float var12 = 0.0F;
		// float var13 = 0.0F;
		
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		// GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
		// GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
		// GL11.glTranslatef(0.0F, 0.0F, 0.468F);
		GL11.glPushMatrix();
		float var11 = 0.0625F;
		// GL11.glScalef(var11, var11, var11);
		
		for(int i = 0; i < par1.getPaintings().length; i++){
			GL11.glPushMatrix();
			float var12 = 0.0F;
			float var13 = 0.0F;
			Painting picture = par1.getPainting(i);
			switch(i){
				case 0:
					var13 = 90.0F;
					var12 = picture == null ? 0.0F : 180.0F;
				break;
				case 1:
					var13 = -90.0F;
					var12 = picture == null ? 0.0F : 180.0F;
				break;
				case 2:
					var12 = 180.0F;
				break;
				case 3:
					var12 = 0.0F;
				break;
				case 4:
					var12 = -90.0F;
				break;
				case 5:
					var12 = 90.0F;
				break;
			}
			GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
			// GL11.glTranslatef(0.0F, 0.0F, 0.46875F);
			GL11.glTranslatef(0.0F, 0.0F, 0.5F);
			GL11.glScalef(var11, var11, var11);
			if(picture != null){
				if(picture.texID <= 0){
					picture.texID = GL11.glGenTextures();
					picture.getPic();
				}
				if(picture.hasPic()){
					ByteBuffer textureBuffer = ByteBuffer.allocateDirect(picture.getPic().length);
					textureBuffer.order(ByteOrder.nativeOrder());
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
					textureBuffer.clear();
					textureBuffer.put(picture.getPic());
					textureBuffer.flip();
					GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, picture.getRow(), picture.getRow(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffer);
					picture.delPic();
				}
				else
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
				draw();
			}
			else{
				this.bindTexture(Texture);
				drawBa();
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity par1, double par2, double par4, double par6, float par8){
		renderTileEntityAt((TileEntityPaintingFrame)par1, par2, par4, par6, par8);
	}
	
	public void draw(){
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(8F, 8F, 0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-8F, 8F, 0.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(-8F, -8F, 0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(8F, -8F, 0.0F, 1.0F, 1.0F);
		tessellator.draw();
	}
	
	public void drawBa(){
		// 0
		float onePixel = 1F / 256F;
		float xOffset = onePixel * 16 * 12;
		float yOffset = onePixel * 16 * 0;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		
		tessellator.addVertexWithUV(8F - 1F, 8F - 1F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 1);
		tessellator.addVertexWithUV(8F - 15F, 8F - 1F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 1);
		tessellator.addVertexWithUV(8F - 15F, 8F - 15F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 15);
		tessellator.addVertexWithUV(8F - 1F, 8F - 15F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 15);
		
		tessellator.addVertexWithUV(8F - 0F, 8F - 0F, 0.0F, xOffset + onePixel * 0, yOffset + onePixel * 0);
		tessellator.addVertexWithUV(8F - 16F, 8F - 0F, 0.0F, xOffset + onePixel * 16, yOffset + onePixel * 0);
		tessellator.addVertexWithUV(8F - 15F, 8F - 1F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 1);
		tessellator.addVertexWithUV(8F - 1F, 8F - 1F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 1);
		
		tessellator.addVertexWithUV(8F - 1F, 8F - 15F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 15);
		tessellator.addVertexWithUV(8F - 15F, 8F - 15F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 15);
		tessellator.addVertexWithUV(8F - 16F, 8F - 16F, 0.0F, xOffset + onePixel * 16, yOffset + onePixel * 16);
		tessellator.addVertexWithUV(8F - 0F, 8F - 16F, 0.0F, xOffset + onePixel * 0, yOffset + onePixel * 16);
		
		tessellator.addVertexWithUV(8F - 0F, 8F - 0F, 0.0F, xOffset + onePixel * 0, yOffset + onePixel * 0);
		tessellator.addVertexWithUV(8F - 1F, 8F - 1F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 1);
		tessellator.addVertexWithUV(8F - 1F, 8F - 15F, -1.0F, xOffset + onePixel * 1, yOffset + onePixel * 15);
		tessellator.addVertexWithUV(8F - 0F, 8F - 16F, 0.0F, xOffset + onePixel * 0, yOffset + onePixel * 16);
		
		tessellator.addVertexWithUV(8F - 15F, 8F - 1F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 1);
		tessellator.addVertexWithUV(8F - 16F, 8F - 0F, 0.0F, xOffset + onePixel * 16, yOffset + onePixel * 0);
		tessellator.addVertexWithUV(8F - 16F, 8F - 16F, 0.0F, xOffset + onePixel * 16, yOffset + onePixel * 16);
		tessellator.addVertexWithUV(8F - 15F, 8F - 15F, -1.0F, xOffset + onePixel * 15, yOffset + onePixel * 15);
		tessellator.draw();
	}
}
