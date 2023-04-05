package com.vanym.paniclecraft.client.gui.config;

import java.util.ArrayList;
import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModConfig extends GuiConfig {
    
    public GuiModConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), DEF.MOD_ID, false, false,
              Core.instance.config.getConfigFile().getName());
    }
    
    protected static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        Configuration config = Core.instance.config;
        for (String category : config.getCategoryNames()) {
            ConfigCategory configCategory = config.getCategory(category);
            if (configCategory.isChild()) {
                continue;
            }
            ConfigElement configElement = new ConfigElement(configCategory);
            list.add(configElement);
        }
        return list;
    }
}
