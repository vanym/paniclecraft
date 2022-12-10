package ee_man.mod3.client.gui.container;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerPalette;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiPalette extends GuiContainer{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "paletteGui.png");
	
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
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream data = new DataOutputStream(bytes);
			try{
				data.writeByte(8);
				if(GuiScreen.isShiftKeyDown())
					data.writeByte(par1GuiButton.id + 6);
				else
					if(GuiScreen.isCtrlKeyDown())
						data.writeByte(par1GuiButton.id + 12);
					else
						data.writeByte(par1GuiButton.id);
			} catch(IOException e){
				e.printStackTrace();
			}
			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = DefaultProperties.MOD_ID;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			packet.isChunkDataPacket = false;
			NetClientHandler var5 = this.mc.getNetHandler();
			if(var5 != null){
				var5.addToSendQueue(packet);
			}
		}
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		this.fontRenderer.drawString(this.container.inventoryPalette.isInvNameLocalized() ? this.container.inventoryPalette.getInvName() : this.container.inventoryPalette.getInvName(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.container.inventoryPlayer.isInvNameLocalized() ? this.container.inventoryPlayer.getInvName() : StatCollector.translateToLocal(this.container.inventoryPlayer.getInvName()), 8, this.ySize - 96 + 2, 4210752);
		// \u00a7c,\u00a7a,\u00a79
		this.fontRenderer.drawString("R: " + this.container.inventoryPalette.getRed(), 70, 22, 4210752);
		this.fontRenderer.drawString("G: " + this.container.inventoryPalette.getGreen(), 70, 37, 4210752);
		this.fontRenderer.drawString("B: " + this.container.inventoryPalette.getBlue(), 70, 52, 4210752);
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
