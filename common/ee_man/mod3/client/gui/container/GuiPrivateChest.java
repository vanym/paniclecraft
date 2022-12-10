package ee_man.mod3.client.gui.container;

import org.lwjgl.opengl.GL11;

import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerPrivateChest;
import ee_man.mod3.utils.Localization;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiPrivateChest extends GuiContainer{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "privateChestGui.png");
	
	public ResourceLocation localGuiTexture = GuiTexture;
	
	public ContainerPrivateChest container;
	
	public GuiPrivateChest(ContainerPrivateChest par1Container){
		super(par1Container);
		container = par1Container;
		this.xSize = 256;
		this.ySize = 256;
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		this.fontRenderer.drawString(StatCollector.translateToLocal("stat.itemsButton"), 8, 6, 4210752);
		this.fontRenderer.drawString(this.container.playerInventory.isInvNameLocalized() ? this.container.playerInventory.getInvName() : StatCollector.translateToLocal(this.container.playerInventory.getInvName()), 8, this.ySize - 94, 4210752);
		this.fontRenderer.drawString(Localization.get("gui.privateChest.nameUpgrades") + ": " + (this.container.tile.select + 1), 176, this.ySize - 94, 4210752);
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(localGuiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
	
}
