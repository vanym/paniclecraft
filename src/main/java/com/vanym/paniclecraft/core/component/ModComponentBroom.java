package com.vanym.paniclecraft.core.component;

import java.util.Map;

import com.vanym.paniclecraft.item.ItemBroom;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModComponentBroom extends ModComponent {
    
    @ModComponentObject
    public ItemBroom itemBroom;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        serverBuilder.push(this.getName());
        ForgeConfigSpec.IntValue durability =
                serverBuilder.comment("\'0\' is infinite")
                             .defineInRange("durability", 3072, 0, Short.MAX_VALUE);
        ForgeConfigSpec.DoubleValue radius =
                serverBuilder.defineInRange("radius", 6.0D, 1.0D, 64.0D);
        this.itemBroom = new ItemBroom(durability::get, radius::get);
        serverBuilder.pop();
    }
    
    @Override
    public String getName() {
        return "broom";
    }
}
