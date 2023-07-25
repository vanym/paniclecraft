package com.vanym.paniclecraft.core.component;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockCannon;
import com.vanym.paniclecraft.client.gui.container.GuiCannon;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererCannon;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityCannonRenderer;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.network.NetworkUtils;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModComponentCannon extends ModComponent {
    
    @ModComponentObject
    public BlockCannon blockCannon;
    @ModComponentObject
    public BlockItem itemCannon;
    
    @ModComponentObject
    public TileEntityType<TileEntityCannon> tileEntityCannon;
    
    @ModComponentObject
    public ContainerType<ContainerCannon> containerCannon;
    
    public Supplier<Double> maxStrength;
    public Supplier<Integer> pickupDelay;
    public Supplier<Integer> shootTimeout;
    
    @OnlyIn(Dist.CLIENT)
    public TileEntityCannonRenderer tileCannonRenderer;
    
    @OnlyIn(Dist.CLIENT)
    protected Supplier<Boolean> renderTileCannon;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        this.blockCannon = new BlockCannon();
        this.itemCannon = new BlockItem(
                this.blockCannon,
                new Item.Properties().group(Core.instance.tab)
                                     .setTEISR(()->ItemRendererCannon::createRegistered));
        this.itemCannon.setRegistryName(this.blockCannon.getRegistryName());
        this.tileEntityCannon = new TileEntityType<>(
                TileEntityCannon::new,
                Collections.singleton(this.blockCannon),
                null);
        this.tileEntityCannon.setRegistryName(TileEntityCannon.ID);
        this.containerCannon = IForgeContainerType.create(ContainerCannon::create);
        this.containerCannon.setRegistryName(TileEntityCannon.ID);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        serverBuilder.push(this.getName());
        this.maxStrength = serverBuilder.defineInRange("maxStrength", 5.0D, 0.0D, 16.0D)::get;
        this.pickupDelay = serverBuilder.comment("shooted items pickup delay in game ticks")
                                        .defineInRange("pickupDelay", 25, 0, Short.MAX_VALUE)::get;
        this.shootTimeout = serverBuilder.comment("shoot timeout in game ticks")
                                         .defineInRange("shootTimeout", 2, 0, Short.MAX_VALUE)::get;
        serverBuilder.pop();
        
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> {
            ForgeConfigSpec.Builder clientBuilder = configBuilders.get(ModConfig.Type.CLIENT);
            clientBuilder.push(CLIENT_RENDER);
            this.renderTileCannon = clientBuilder.define("cannonTile", true)::get;
            clientBuilder.pop();
        });
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        Core.instance.network.registerMessage(51, MessageCannonSet.class,
                                              MessageCannonSet::encode,
                                              MessageCannonSet::decode,
                                              NetworkUtils.handleInWorld(MessageCannonSet::handleInWorld));
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void setupClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(this.containerCannon, GuiCannon::new);
        this.tileCannonRenderer = new TileEntityCannonRenderer();
        this.tileCannonRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        if (this.renderTileCannon.get()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class,
                                                         this.tileCannonRenderer);
        }
    }
    
    @Override
    public String getName() {
        return "cannon";
    }
}
