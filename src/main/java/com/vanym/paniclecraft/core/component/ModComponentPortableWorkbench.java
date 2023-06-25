package com.vanym.paniclecraft.core.component;

import java.util.Map;

import com.vanym.paniclecraft.container.ContainerPortableWorkbench;
import com.vanym.paniclecraft.item.ItemWorkbench;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModComponentPortableWorkbench extends ModComponent {
    
    @ModComponentObject
    public ItemWorkbench itemWorkbench;
    
    @ModComponentObject
    public ContainerType<ContainerPortableWorkbench> containerPortableWorkbench;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        serverBuilder.push(this.getName());
        ForgeConfigSpec.IntValue durability =
                serverBuilder.comment("\'0\' is infinite")
                             .defineInRange("durability", 3072, 0, Short.MAX_VALUE);
        this.itemWorkbench = new ItemWorkbench(()->durability.get());
        this.containerPortableWorkbench =
                new ContainerType<>(ContainerPortableWorkbench::new);
        this.containerPortableWorkbench.setRegistryName(this.itemWorkbench.getRegistryName());
        serverBuilder.pop();
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    protected void setupClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(this.containerPortableWorkbench, CraftingScreen::new);
    }
    
    @Override
    public String getName() {
        return "portableworkbench";
    }
}
