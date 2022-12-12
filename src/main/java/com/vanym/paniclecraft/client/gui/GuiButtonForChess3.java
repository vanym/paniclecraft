package com.vanym.paniclecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonForChess3 extends GuiButton{
	
	FontRenderer fontRenderer;
	
	public GuiButtonForChess3(int par1, int par2, int par3, FontRenderer par4, int par5, String par6){
		super(par1, par2, par3, 5 + (par1 == par5 ? 15 : 0), 20, par6);
		fontRenderer = par4;
	}
	
	public void drawButton(Minecraft par1Minecraft, int par2, int par3){
		par1Minecraft.renderEngine.bindTexture(GuiChess.ButtonsTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
		this.drawTexturedModalRect(this.xPosition, this.yPosition, (this.width == 5 ? this.width * 4 : 0), 4 * this.height, this.width, this.height);
		if(var4)
			this.drawString(this.fontRenderer, displayString, par2, par3 - 7, 16777215);
	}
}
