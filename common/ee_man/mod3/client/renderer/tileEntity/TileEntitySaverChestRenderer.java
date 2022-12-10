package ee_man.mod3.client.renderer.tileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.client.renderer.model.ModelSaverChest;
import ee_man.mod3.tileEntity.TileEntitySaverChest;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntitySaverChestRenderer extends TileEntitySpecialRenderer{
	
	final ResourceLocation[] Texture = new ResourceLocation[]{new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "saverChest_" + BlockLog.woodType[0] + ".png"), new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "saverChest_" + BlockLog.woodType[1] + ".png"), new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "saverChest_" + BlockLog.woodType[2] + ".png"), new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "saverChest_" + BlockLog.woodType[3] + ".png")};
	
	ModelSaverChest body = new ModelSaverChest();
	
	public void renderTileEntityAt(TileEntitySaverChest par1, double par2, double par4, double par6, float par8){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float var10 = 0.6666667F;
		GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * var10, (float)par6 + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(90.0F * (par1.getBlockMetadata() % 4), 0.0F, 1.0F, 0.0F);
		this.bindTexture(Texture[par1.getBlockMetadata() / 4]);
		if((par1.open ? 90 : 0) < par1.rotation)
			par1.rotation -= 9;
		if((par1.open ? 90 : 0) > par1.rotation)
			par1.rotation += 9;
		if(par1.rotation > 90)
			par1.rotation = 90;
		if(par1.rotation < 0)
			par1.rotation = 0;
		body.Shape6.rotateAngleX = -(float)(Math.toRadians(par1.rotation));
		body.render(0.0625F);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity par1, double par2, double par4, double par6, float par8){
		renderTileEntityAt((TileEntitySaverChest)par1, par2, par4, par6, par8);
	}
	
}
