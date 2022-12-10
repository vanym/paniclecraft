package ee_man.mod3.client.renderer.tileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.client.renderer.model.ModelSaverChest;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntityPrivateChestRenderer extends TileEntitySpecialRenderer{
	
	final ResourceLocation Texture = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "privateChest.png");
	
	ModelSaverChest body = new ModelSaverChest();
	
	public void renderTileEntityAt(TileEntityPrivateChest par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		if(!par1.specialRender()){
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.5F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(90.0F * (par1.getBlockMetadata() % 4), 0.0F, 1.0F, 0.0F);
			this.bindTexture(Texture);
			body.render(0.0625F);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f){
		this.renderTileEntityAt((TileEntityPrivateChest)tileentity, d0, d1, d2, f);
	}
	
}
