package ee_man.mod3.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import ee_man.mod3.DEF;
import ee_man.mod3.client.gui.container.GuiPortableCrafting;

public class NEIMod3Config implements IConfigureNEI{
	
	@Override
	public void loadConfig(){
		API.registerGuiOverlay(GuiPortableCrafting.class, "crafting");
		API.registerGuiOverlayHandler(GuiPortableCrafting.class, new DefaultOverlayHandler(), "crafting");
	}
	
	@Override
	public String getName(){
		return DEF.MOD_NAME + " NEI Plugin";
	}
	
	@Override
	public String getVersion(){
		return "0.4";
	}
}
