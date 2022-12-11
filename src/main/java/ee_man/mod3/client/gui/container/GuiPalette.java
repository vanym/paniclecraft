package ee_man.mod3.client.gui.container;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DEF;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.network.PacketHandler;
import ee_man.mod3.network.message.MessagePaletteChange;
import ee_man.mod3.utils.MainUtils;

@SideOnly(Side.CLIENT)
public class GuiPalette extends GuiContainer{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DEF.MOD_ID, "textures/guis/paletteGui.png");
	
	private GuiButton buttonMinusRed;
	private GuiButton buttonPlusRed;
	
	private GuiButton buttonMinusGreen;
	private GuiButton buttonPlusGreen;
	
	private GuiButton buttonMinusBlue;
	private GuiButton buttonPlusBlue;
	
	private ContainerPalette container;
	
	public GuiPalette(ContainerPalette par1Container){
		super(par1Container);
		container = par1Container;
	}
	
	@SuppressWarnings("unchecked")
	public void initGui(){
		super.initGui();
		int var1 = this.width / 2;
		int var2 = this.height / 2;
		int var3 = var1 - 50;
		int var4 = var2 - 66;
		buttonMinusRed = new GuiButton(0, var3 + 15, var4, 15, 15, "-");
		buttonPlusRed = new GuiButton(1, var3, var4, 15, 15, "+");
		buttonMinusGreen = new GuiButton(2, var3 + 15, var4 + 15, 15, 15, "-");
		buttonPlusGreen = new GuiButton(3, var3, var4 + 15, 15, 15, "+");
		buttonMinusBlue = new GuiButton(4, var3 + 15, var4 + 30, 15, 15, "-");
		buttonPlusBlue = new GuiButton(5, var3, var4 + 30, 15, 15, "+");
		this.buttonList.clear();
		this.buttonList.add(buttonMinusRed);
		this.buttonList.add(buttonPlusRed);
		this.buttonList.add(buttonMinusGreen);
		this.buttonList.add(buttonPlusGreen);
		this.buttonList.add(buttonMinusBlue);
		this.buttonList.add(buttonPlusBlue);
	}
	
	public void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.enabled){
			byte bt = (byte)par1GuiButton.id;
			if(GuiScreen.isCtrlKeyDown())
				bt += 12;
			else
				if(GuiScreen.isShiftKeyDown())
					bt += 6;
			PacketHandler.INSTANCE.sendToServer(new MessagePaletteChange(bt));
		}
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		this.fontRendererObj.drawString(this.container.inventoryPalette.hasCustomInventoryName() ? this.container.inventoryPalette.getInventoryName() : StatCollector.translateToLocal(this.container.inventoryPalette.getInventoryName() + ".name").trim(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.container.inventoryPlayer.hasCustomInventoryName() ? this.container.inventoryPlayer.getInventoryName() : I18n.format(this.container.inventoryPlayer.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		// \u00a7c,\u00a7a,\u00a79
		Color color = MainUtils.getColorFromInt(this.container.inventoryPalette.getRGB());
		this.fontRendererObj.drawString("R: " + color.getRed(), 70, 22, 4210752);
		this.fontRendererObj.drawString("G: " + color.getGreen(), 70, 37, 4210752);
		this.fontRendererObj.drawString("B: " + color.getBlue(), 70, 52, 4210752);
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 3 * 18 + 17 + 96);
		int rgb = this.container.inventoryPalette.getRGB();
		float f7 = (float)(rgb >> 16 & 255) / 255.0F;
		float f6 = (float)(rgb >> 8 & 255) / 255.0F;
		float f5 = (float)(rgb & 255) / 255.0F;
		GL11.glColor4f(1.0F * f7, 1.0F * f6, 1.0F * f5, 1.0F);
		this.drawTexturedModalRect(k + 8, l + 38, 0, 167, 16, 16);
	}
	
}
