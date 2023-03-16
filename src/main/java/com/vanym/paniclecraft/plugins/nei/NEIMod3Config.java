package com.vanym.paniclecraft.plugins.nei;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.container.GuiPortableCrafting;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEIMod3Config implements IConfigureNEI {
    
    @Override
    public void loadConfig() {
        API.registerGuiOverlay(GuiPortableCrafting.class, "crafting");
        API.registerGuiOverlayHandler(GuiPortableCrafting.class, new DefaultOverlayHandler(),
                                      "crafting");
    }
    
    @Override
    public String getName() {
        return DEF.MOD_NAME + " NEI Plugin";
    }
    
    @Override
    public String getVersion() {
        return DEF.VERSION;
    }
}
