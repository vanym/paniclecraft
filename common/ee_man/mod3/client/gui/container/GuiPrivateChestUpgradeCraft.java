package ee_man.mod3.client.gui.container;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.container.ContainerPrivateChest;

public class GuiPrivateChestUpgradeCraft extends GuiPrivateChest{
	
	public static final ResourceLocation GuiTexture = new ResourceLocation(DefaultProperties.TEXTURE_ID, DefaultProperties.TEXTURE_FOLDER + "privateChestCraftGui.png");
	
	public GuiPrivateChestUpgradeCraft(ContainerPrivateChest par1Container){
		super(par1Container);
		localGuiTexture = GuiTexture;
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 155, 7, 4210752);
	}
}
