package ee_man.mod3.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.client.gui.container.GuiPortableCrafting;
import ee_man.mod3.client.gui.container.GuiPrivateChestUpgradeCraft;

public class NEIMod3Config implements IConfigureNEI{
	
	@Override
	public void loadConfig(){
		API.registerGuiOverlay(GuiPortableCrafting.class, "crafting");
		API.registerGuiOverlayHandler(GuiPortableCrafting.class, new DefaultOverlayHandler(), "crafting");
		
		API.registerGuiOverlay(GuiPrivateChestUpgradeCraft.class, "crafting", 132, 12);
		API.registerGuiOverlayHandler(GuiPrivateChestUpgradeCraft.class, new ChestCraftOverlayHandler(132, 12), "crafting");
	}
	
	@Override
	public String getName(){
		return DefaultProperties.MOD_NAME + " NEI Plugin";
	}
	
	@Override
	public String getVersion(){
		return "0.3";
	}
	
}
