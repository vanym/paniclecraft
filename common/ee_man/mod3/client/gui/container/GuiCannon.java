package ee_man.mod3.client.gui.container;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.tileEntity.TileEntityCannon;
import ee_man.mod3.utils.Localization;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiCannon extends GuiContainer{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "cannonGui.png");
	
	private IInventory upperChestInventory;
	private TileEntityCannon cannon;
	
	private GuiButton buttonMinusDirection;
	private GuiButton buttonPlusDirection;
	
	private GuiButton buttonMinusHeight;
	private GuiButton buttonPlusHeight;
	
	private GuiButton buttonMinusStrength;
	private GuiButton buttonPlusStrength;
	
	public GuiCannon(IInventory par1IInventory, TileEntityCannon par2Cannon){
		super(new ContainerCannon(par1IInventory, par2Cannon));
		this.upperChestInventory = par1IInventory;
		this.cannon = par2Cannon;
	}
	
	@SuppressWarnings("unchecked")
	public void initGui(){
		super.initGui();
		int var1 = this.width / 2;
		int var2 = this.height / 2;
		int var3 = var1 - 50;
		int var4 = var2 - 66;
		buttonMinusDirection = new GuiButton(0, var3 + 15, var4, 15, 15, "-");
		buttonPlusDirection = new GuiButton(1, var3, var4, 15, 15, "+");
		buttonMinusHeight = new GuiButton(2, var3 + 15, var4 + 15, 15, 15, "-");
		buttonPlusHeight = new GuiButton(3, var3, var4 + 15, 15, 15, "+");
		buttonMinusStrength = new GuiButton(4, var3 + 15, var4 + 30, 15, 15, "-");
		buttonPlusStrength = new GuiButton(5, var3, var4 + 30, 15, 15, "+");
		this.buttonList.clear();
		this.buttonList.add(buttonMinusDirection);
		this.buttonList.add(buttonPlusDirection);
		this.buttonList.add(buttonMinusHeight);
		this.buttonList.add(buttonPlusHeight);
		this.buttonList.add(buttonMinusStrength);
		this.buttonList.add(buttonPlusStrength);
		this.checkHeight();
	}
	
	public void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.enabled){
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream data = new DataOutputStream(bytes);
			try{
				data.writeByte(6);
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
			this.checkHeight();
		}
	}
	
	public void checkHeight(){
		if(cannon.height >= 90)
			buttonPlusHeight.enabled = false;
		else
			buttonPlusHeight.enabled = true;
		if(cannon.height <= 0)
			buttonMinusHeight.enabled = false;
		else
			buttonMinusHeight.enabled = true;
		
		if(cannon.strength >= cannon.maxStrength)
			buttonPlusStrength.enabled = false;
		else
			buttonPlusStrength.enabled = true;
		if(cannon.strength <= 0)
			buttonMinusStrength.enabled = false;
		else
			buttonMinusStrength.enabled = true;
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		this.fontRenderer.drawString(this.cannon.isInvNameLocalized() ? this.cannon.getInvName() : this.cannon.getInvName(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.upperChestInventory.isInvNameLocalized() ? this.upperChestInventory.getInvName() : StatCollector.translateToLocal(this.upperChestInventory.getInvName()), 8, this.ySize - 96 + 2, 4210752);
		
		this.fontRenderer.drawString(Localization.get("gui.cannon.direction") + " " + cannon.direction, 70, 22, 4210752);
		this.fontRenderer.drawString(Localization.get("gui.cannon.height") + " " + cannon.height, 70, 37, 4210752);
		this.fontRenderer.drawString(Localization.get("gui.cannon.strength") + " " + cannon.strength, 70, 52, 4210752);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int i, int j){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 3 * 18 + 17 + 96);
	}
	
}
