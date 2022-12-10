package ee_man.mod3.client.renderer.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelSaverChest extends ModelBase{
	public ModelRenderer Shape1;
	public ModelRenderer Shape2;
	public ModelRenderer Shape3;
	public ModelRenderer Shape4;
	public ModelRenderer Shape5;
	public ModelRenderer Shape6;
	public ModelRenderer Shape7;
	
	public ModelSaverChest(){
		textureWidth = 128;
		textureHeight = 64;
		
		Shape1 = new ModelRenderer(this, 0, 0);
		Shape1.addBox(-7F, -1F, -7F, 14, 1, 14);
		Shape1.setRotationPoint(0F, 0F, 0F);
		Shape1.setTextureSize(128, 64);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
		Shape2 = new ModelRenderer(this, 56, 0);
		Shape2.addBox(-7F, -13F, -7F, 14, 12, 1);
		Shape2.setRotationPoint(0F, 0F, 0F);
		Shape2.setTextureSize(128, 64);
		Shape2.mirror = true;
		setRotation(Shape2, 0F, 0F, 0F);
		Shape3 = new ModelRenderer(this, 56, 13);
		Shape3.addBox(-7F, -13F, 6F, 14, 12, 1);
		Shape3.setRotationPoint(0F, 0F, 0F);
		Shape3.setTextureSize(128, 64);
		Shape3.mirror = true;
		setRotation(Shape3, 0F, 0F, 0F);
		Shape4 = new ModelRenderer(this, 26, 15);
		Shape4.addBox(-7F, -13F, -6F, 1, 12, 12);
		Shape4.setRotationPoint(0F, 0F, 0F);
		Shape4.setTextureSize(128, 64);
		Shape4.mirror = true;
		setRotation(Shape4, 0F, 0F, 0F);
		Shape5 = new ModelRenderer(this, 0, 15);
		Shape5.addBox(6F, -13F, -6F, 1, 12, 12);
		Shape5.setRotationPoint(0F, 0F, 0F);
		Shape5.setTextureSize(128, 64);
		Shape5.mirror = true;
		setRotation(Shape5, 0F, 0F, 0F);
		Shape6 = new ModelRenderer(this, 0, 39);
		Shape6.addBox(-7F, -1F, -14F, 14, 1, 14);
		Shape6.setRotationPoint(0F, -13F, 7F);
		Shape6.setTextureSize(128, 64);
		Shape6.mirror = true;
		setRotation(Shape6, 0F, 0F, 0F);
		Shape7 = new ModelRenderer(this, 86, 0);
		Shape7.addBox(-1.5F, -0.5F, -15F, 3, 2, 1);
		Shape7.setRotationPoint(0F, -13F, 7F);
		Shape7.setTextureSize(128, 64);
		Shape7.mirror = true;
		setRotation(Shape7, 0F, 0F, 0F);
	}
	
	public void render(float f5){
		Shape7.rotateAngleX = Shape6.rotateAngleX;
		Shape1.render(f5);
		Shape2.render(f5);
		Shape3.render(f5);
		Shape4.render(f5);
		Shape5.render(f5);
		Shape6.render(f5);
		Shape7.render(f5);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
