package com.vanym.paniclecraft.core.component;

import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface IModComponent {
    
    public static final String ENABLE_FLAG = "enable";
    
    @OnlyIn(Dist.CLIENT)
    public static final String CLIENT_RENDER = "render";
    
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders);
    
    public String getName();
    
    default public boolean isEnabled() {
        return true;
    }
    
    default public List<Item> getItems() {
        return null;
    }
}
