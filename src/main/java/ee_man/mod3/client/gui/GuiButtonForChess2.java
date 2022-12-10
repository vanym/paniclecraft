package ee_man.mod3.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonForChess2 extends GuiButton{
	
	public GuiButtonForChess2(int par1, int par2, int par3){
		super(par1, par2, par3, 20, 20, "");
		
	}
	
	public void drawButton(Minecraft par1Minecraft, int par2, int par3){
		par1Minecraft.renderEngine.bindTexture(GuiChess.ButtonsTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
		int var5 = 0 + this.width * (Math.abs(this.id) - 98);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, this.width * (var4 ? 1 : 2), 0, this.width, this.height);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, this.height * (this.id > 0 ? 1 : 2), this.width, this.height);
	}
}
