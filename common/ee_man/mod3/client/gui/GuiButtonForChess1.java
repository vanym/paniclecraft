package ee_man.mod3.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.utils.ChessDesk;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class GuiButtonForChess1 extends GuiButton{
	
	public byte mode = 0;
	
	public ChessDesk desk;
	
	public boolean cbs = true;
	
	public GuiButtonForChess1(int par1, int par2, int par3, ChessDesk par4){
		super(par1, par2, par3, 20, 20, "");
		desk = par4;
	}
	
	public void drawButton(Minecraft par1Minecraft, int par2, int par3){
		if(this.drawButton){
			par1Minecraft.renderEngine.bindTexture(GuiChess.ButtonsTexture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			int var5 = 0 + this.width * (int)this.mode;
			if(this.id == desk.lastFrom)
				var5 = this.width * 4;
			if(this.id == desk.lastTo)
				var5 = this.width * 5;
			if(this.id == desk.lastFrom && mode == 2)
				var5 = this.width * 6;
			if(this.id == desk.lastTo && mode == 2)
				var5 = this.width * 7;
			if(var4 && cbs)
				var5 = this.width;
			int var6 = Math.abs((int)desk.desk[this.id]);
			if(var6 > 6)
				var6 -= 3;
			this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, 0, this.width, this.height);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, var6 * this.width, (desk.desk[this.id] > 0 ? this.height : this.height * 2), this.width, this.height);
		}
	}
}
