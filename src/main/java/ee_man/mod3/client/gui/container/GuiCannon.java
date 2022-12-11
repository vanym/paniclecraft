package ee_man.mod3.client.gui.container;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import ee_man.mod3.DEF;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.network.PacketHandler;
import ee_man.mod3.network.message.MessageCannonChange;
import ee_man.mod3.network.message.MessageCannonSet;
import ee_man.mod3.tileentity.TileEntityCannon;

public class GuiCannon extends GuiContainer{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DEF.MOD_ID, "textures/guis/cannonGui.png");
	
	public IInventory upperChestInventory;
	public TileEntityCannon cannon;
	
	private GuiButton buttonMinusDirection;
	private GuiButton buttonPlusDirection;
	
	private GuiButton buttonMinusHeight;
	private GuiButton buttonPlusHeight;
	
	private GuiButton buttonMinusStrength;
	private GuiButton buttonPlusStrength;
	
	private GuiTextField textDirection;
	private GuiTextField textHeight;
	private GuiTextField textStrength;
	
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
		int var3 = var1 - 60;
		int var4 = var2 - 66;
		buttonMinusDirection = new GuiButton(0, var3 + 10, var4, 10, 15, "-");
		buttonPlusDirection = new GuiButton(1, var3, var4, 10, 15, "+");
		buttonMinusHeight = new GuiButton(2, var3 + 10, var4 + 15, 10, 15, "-");
		buttonPlusHeight = new GuiButton(3, var3, var4 + 15, 10, 15, "+");
		buttonMinusStrength = new GuiButton(4, var3 + 10, var4 + 30, 10, 15, "-");
		buttonPlusStrength = new GuiButton(5, var3, var4 + 30, 10, 15, "+");
		textDirection = new GuiTextField(fontRendererObj, var3 + 105, var4, 35, 15);
		textHeight = new GuiTextField(fontRendererObj, var3 + 105, var4 + 15, 35, 15);
		textStrength = new GuiTextField(fontRendererObj, var3 + 105, var4 + 30, 35, 15);
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
			byte bt = (byte)par1GuiButton.id;
			if(GuiScreen.isCtrlKeyDown())
				bt += 12;
			else
				if(GuiScreen.isShiftKeyDown())
					bt += 6;
			PacketHandler.INSTANCE.sendToServer(new MessageCannonChange(bt));
			// this.checkHeight();
		}
	}
	
	public void checkHeight(){
		// if(true)
		// return;
		if(cannon.getHeight() >= 90)
			buttonPlusHeight.enabled = false;
		else
			buttonPlusHeight.enabled = true;
		if(cannon.getHeight() <= -90)
			buttonMinusHeight.enabled = false;
		else
			buttonMinusHeight.enabled = true;
		
		if(cannon.getStrength() >= cannon.maxStrength)
			buttonPlusStrength.enabled = false;
		else
			buttonPlusStrength.enabled = true;
		if(cannon.getStrength() <= 0)
			buttonMinusStrength.enabled = false;
		else
			buttonMinusStrength.enabled = true;
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		this.fontRendererObj.drawString(this.cannon.hasCustomInventoryName() ? this.cannon.getInventoryName() : StatCollector.translateToLocal(this.cannon.getInventoryName() + ".name").trim(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName() : I18n.format(this.upperChestInventory.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.direction").trim() + " " + cannon.getDirection(), 50, 22, 4210752);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.height").trim() + " " + cannon.getHeight(), 50, 37, 4210752);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.strength").trim() + " " + cannon.getStrength(), 50, 52, 4210752);
	}
	
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		this.textDirection.drawTextBox();
		this.textHeight.drawTextBox();
		this.textStrength.drawTextBox();
	}
	
	public void updateScreen(){
		super.updateScreen();
		this.textDirection.updateCursorCounter();
		this.textHeight.updateCursorCounter();
		this.textStrength.updateCursorCounter();
	}
	
	public void keyTyped(char par1, int par2){
		super.keyTyped(par1, par2);
		this.textDirection.textboxKeyTyped(par1, par2);
		this.textHeight.textboxKeyTyped(par1, par2);
		this.textStrength.textboxKeyTyped(par1, par2);
		if(par2 == 28){
			String text = null;
			byte bt = -1;
			if(this.textDirection.isFocused()){
				text = textDirection.getText();
				bt = 0;
			}
			if(this.textHeight.isFocused()){
				text = textHeight.getText();
				bt = 1;
			}
			if(this.textStrength.isFocused()){
				text = textStrength.getText();
				bt = 2;
			}
			if(bt >= 0){
				double amount = 0;
				try{
					amount = Double.parseDouble(text);
				} catch(NumberFormatException e){
				} finally{
					PacketHandler.INSTANCE.sendToServer(new MessageCannonSet(bt, amount));
				}
			}
		}
	}
	
	public void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		this.textDirection.mouseClicked(par1, par2, par3);
		this.textHeight.mouseClicked(par1, par2, par3);
		this.textStrength.mouseClicked(par1, par2, par3);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int i, int j){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 3 * 18 + 17 + 96);
	}
	
}
