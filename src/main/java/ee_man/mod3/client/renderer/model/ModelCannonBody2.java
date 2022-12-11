package ee_man.mod3.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCannonBody2 extends ModelBase{
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;
	ModelRenderer Shape5;
	ModelRenderer Shape6;
	ModelRenderer Shape7;
	ModelRenderer Shape8;
	
	public ModelCannonBody2(){
		textureWidth = 128;
		textureHeight = 64;
		
		Shape2 = new ModelRenderer(this, 0, 18);
		Shape2.addBox(-4F, 0F, -5F, 8, 1, 10);
		Shape2.setRotationPoint(0F, -2F, 0F);
		Shape2.setTextureSize(128, 64);
		Shape2.mirror = true;
		Shape3 = new ModelRenderer(this, 0, 29);
		Shape3.addBox(4F, 0F, -4F, 1, 1, 8);
		Shape3.setRotationPoint(0F, -2F, 0F);
		Shape3.setTextureSize(128, 64);
		Shape3.mirror = true;
		Shape4 = new ModelRenderer(this, 18, 29);
		Shape4.addBox(-5F, 0F, -4F, 1, 1, 8);
		Shape4.setRotationPoint(0F, -2F, 0F);
		Shape4.setTextureSize(128, 64);
		Shape4.mirror = true;
		Shape5 = new ModelRenderer(this, 36, 18);
		Shape5.addBox(3F, -4F, -4F, 1, 4, 8);
		Shape5.setRotationPoint(0F, -2F, 0F);
		Shape5.setTextureSize(128, 64);
		Shape5.mirror = true;
		Shape6 = new ModelRenderer(this, 36, 28);
		Shape6.addBox(-4F, -4F, -4F, 1, 4, 8);
		Shape6.setRotationPoint(0F, -2F, 0F);
		Shape6.setTextureSize(128, 64);
		Shape6.mirror = true;
		Shape7 = new ModelRenderer(this, 0, 38);
		Shape7.addBox(3F, -5F, -3F, 1, 1, 6);
		Shape7.setRotationPoint(0F, -2F, 0F);
		Shape7.setTextureSize(128, 64);
		Shape7.mirror = true;
		Shape8 = new ModelRenderer(this, 36, 38);
		Shape8.addBox(-4F, -5F, -3F, 1, 1, 6);
		Shape8.setRotationPoint(0F, -2F, 0F);
		Shape8.setTextureSize(128, 64);
		Shape8.mirror = true;
	}
	
	public void render(float f5){
		Shape2.render(f5);
		Shape3.render(f5);
		Shape4.render(f5);
		Shape5.render(f5);
		Shape6.render(f5);
		Shape7.render(f5);
		Shape8.render(f5);
	}
	
}
