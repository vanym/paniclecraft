package com.vanym.paniclecraft.core.component;

import java.util.Collections;
import java.util.Map;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityAdvSignRenderer;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.network.NetworkUtils;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.network.message.MessageAdvSignOpenGui;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModComponentAdvSign extends ModComponent {
    
    @ModComponentObject
    public ItemAdvSign itemAdvSign;
    @ModComponentObject
    public BlockAdvSign blockAdvSign;
    
    @ModComponentObject
    public TileEntityType<TileEntityAdvSign> tileEntityAdvSign;
    
    @OnlyIn(Dist.CLIENT)
    public TileEntityAdvSignRenderer tileAdvSignRenderer;
    
    @OnlyIn(Dist.CLIENT)
    protected ForgeConfigSpec.BooleanValue renderTileAdvSign;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        this.itemAdvSign = new ItemAdvSign();
        this.blockAdvSign = new BlockAdvSign();
        
        this.tileEntityAdvSign = new TileEntityType<>(
                TileEntityAdvSign::new,
                Collections.singleton(this.blockAdvSign),
                null);
        this.tileEntityAdvSign.setRegistryName(TileEntityAdvSign.ID);
        
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> {
            ForgeConfigSpec.Builder clientBuilder = configBuilders.get(ModConfig.Type.CLIENT);
            clientBuilder.push(CLIENT_RENDER);
            this.renderTileAdvSign = clientBuilder.define("advSignTile", true);
            clientBuilder.pop();
        });
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        Core.instance.network.registerMessage(20, MessageAdvSignChange.class,
                                              MessageAdvSignChange::encode,
                                              MessageAdvSignChange::decode,
                                              NetworkUtils.handleInWorld(MessageAdvSignChange::handleInWorld));
        Core.instance.network.registerMessage(21, MessageAdvSignOpenGui.class,
                                              MessageAdvSignOpenGui::encode,
                                              MessageAdvSignOpenGui::decode,
                                              NetworkUtils.handleInWorld(MessageAdvSignOpenGui::handleInWorld));
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void setupClient(FMLClientSetupEvent event) {
        this.tileAdvSignRenderer = new TileEntityAdvSignRenderer();
        this.tileAdvSignRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        if (this.renderTileAdvSign.get()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvSign.class,
                                                         this.tileAdvSignRenderer);
        }
    }
    
    @Override
    public String getName() {
        return "advsign";
    }
}
