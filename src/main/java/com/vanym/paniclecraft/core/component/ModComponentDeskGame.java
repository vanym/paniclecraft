package com.vanym.paniclecraft.core.component;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityChessDeskRenderer;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.network.NetworkUtils;
import com.vanym.paniclecraft.network.message.MessageChessMove;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

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

public class ModComponentDeskGame extends ModComponent {
    
    @ModComponentObject
    public BlockChessDesk blockChessDesk;
    @ModComponentObject
    public ItemChessDesk itemChessDesk;
    
    @ModComponentObject
    public TileEntityType<TileEntityChessDesk> tileEntityChessDesk;
    
    @OnlyIn(Dist.CLIENT)
    public TileEntityChessDeskRenderer tileChessDeskRenderer;
    
    @OnlyIn(Dist.CLIENT)
    protected Supplier<Boolean> renderTileChessDesk;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        
        this.blockChessDesk = new BlockChessDesk();
        this.itemChessDesk = new ItemChessDesk(this.blockChessDesk);
        this.tileEntityChessDesk = new TileEntityType<>(
                TileEntityChessDesk::new,
                Collections.singleton(this.blockChessDesk),
                null);
        this.tileEntityChessDesk.setRegistryName(TileEntityChessDesk.ID);
        
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> {
            ForgeConfigSpec.Builder clientBuilder = configBuilders.get(ModConfig.Type.CLIENT);
            clientBuilder.push(CLIENT_RENDER);
            this.renderTileChessDesk = clientBuilder.define("chessDeskTile", true)::get;
            clientBuilder.pop();
        });
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        Core.instance.network.registerMessage(40, MessageChessMove.class,
                                              MessageChessMove::encode,
                                              MessageChessMove::decode,
                                              NetworkUtils.handleInWorld(MessageChessMove::handleInWorld));
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void setupClient(FMLClientSetupEvent event) {
        this.tileChessDeskRenderer = new TileEntityChessDeskRenderer();
        this.tileChessDeskRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        if (this.renderTileChessDesk.get()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChessDesk.class,
                                                         this.tileChessDeskRenderer);
        }
    }
    
    @Override
    public String getName() {
        return "deskgame";
    }
}
