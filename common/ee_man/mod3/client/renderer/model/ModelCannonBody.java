package ee_man.mod3.client.renderer.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelCannonBody extends ModelBase{
	ModelRenderer Shape1;
	
	public ModelCannonBody(){
		textureWidth = 128;
		textureHeight = 64;
		
		Shape1 = new ModelRenderer(this, 0, 0);
		Shape1.addBox(-8F, -1F, -8F, 16, 1, 16);
		Shape1.setRotationPoint(0F, 0F, 0F);
		Shape1.setTextureSize(128, 64);
		Shape1.mirror = true;
	}
	
	public void render(float f5){
		Shape1.render(f5);
	}
}
