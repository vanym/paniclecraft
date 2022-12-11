package ee_man.mod3.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DEF;
import ee_man.mod3.client.renderer.model.ModelChessBishop;
import ee_man.mod3.client.renderer.model.ModelChessDesk;
import ee_man.mod3.client.renderer.model.ModelChessKing;
import ee_man.mod3.client.renderer.model.ModelChessKnight;
import ee_man.mod3.client.renderer.model.ModelChessPawn;
import ee_man.mod3.client.renderer.model.ModelChessQueen;
import ee_man.mod3.client.renderer.model.ModelChessRook;
import ee_man.mod3.tileentity.TileEntityChessDesk;

@SideOnly(Side.CLIENT)
public class TileEntityChessDeskRenderer extends TileEntitySpecialRenderer{
	
	private final ResourceLocation Texture = new ResourceLocation(DEF.MOD_ID, "textures/models/chessDesk.png");
	private final ResourceLocation TextureW = new ResourceLocation(DEF.MOD_ID, "textures/models/chessW.png");
	private final ResourceLocation TextureB = new ResourceLocation(DEF.MOD_ID, "textures/models/chessB.png");
	
	private ModelChessPawn pawn = new ModelChessPawn();
	private ModelChessBishop bishop = new ModelChessBishop();
	private ModelChessKnight knight = new ModelChessKnight();
	private ModelChessRook rook = new ModelChessRook();
	private ModelChessQueen queen = new ModelChessQueen();
	private ModelChessKing king = new ModelChessKing();
	private ModelChessDesk desk = new ModelChessDesk();
	
	public void renderTileEntityAt(TileEntityChessDesk par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glRotatef(90.0F * par1.getBlockMetadata(), 0.0F, 1.0F, 0.0F);
		float var11 = 0.0625F;
		this.bindTexture(Texture);
		desk.render(var11);
		GL11.glTranslatef(0.5F - 0.0625F, -0.25F + 0.05F, 0.5F - 0.0625F);
		float var9 = .25F;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(par1.desk.desk[i * 8 + j] < 0)
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				switch(par1.desk.desk[i * 8 + j]){
					case 1:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						pawn.render(var9);
						GL11.glPopMatrix();
					break;
					case 2:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						bishop.render(var9);
						GL11.glPopMatrix();
					break;
					case 3:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						knight.render(var9);
						GL11.glPopMatrix();
					break;
					case 7:
					case 4:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						rook.render(var9);
						GL11.glPopMatrix();
					break;
					case 5:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						queen.render(var9);
						GL11.glPopMatrix();
					break;
					case 9:
					case 6:
						this.bindTexture(TextureW);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						king.render(var9);
						GL11.glPopMatrix();
					break;
					case -1:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						pawn.render(var9);
						GL11.glPopMatrix();
					break;
					case -2:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						bishop.render(var9);
						GL11.glPopMatrix();
					break;
					case -3:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						knight.render(var9);
						GL11.glPopMatrix();
					break;
					case -7:
					case -4:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						rook.render(var9);
						GL11.glPopMatrix();
					break;
					case -5:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						queen.render(var9);
						GL11.glPopMatrix();
					break;
					case -9:
					case -6:
						this.bindTexture(TextureB);
						GL11.glPushMatrix();
						GL11.glScalef(var11, var11, var11);
						king.render(var9);
						GL11.glPopMatrix();
					break;
				}
				if(par1.desk.desk[i * 8 + j] < 0)
					GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-0.125F, 0.0F, 0.0F);
			}
			GL11.glTranslatef(0.125F * 8, 0.0F, -0.125F);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8){
		renderTileEntityAt((TileEntityChessDesk)var1, var2, var4, var6, var8);
	}
	
}
