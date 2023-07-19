package com.vanym.paniclecraft.core.component;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.gui.container.GuiPaintingEditView;
import com.vanym.paniclecraft.client.gui.container.GuiPalette;
import com.vanym.paniclecraft.client.renderer.PaintingSpecialSelectionBox;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.entity.EntityPaintOnBlockRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandPaintOnBlock;
import com.vanym.paniclecraft.command.CommandPainting;
import com.vanym.paniclecraft.container.ContainerPaintingViewBase;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.core.component.painting.AnvilCopyEventHandler;
import com.vanym.paniclecraft.core.component.painting.AnyBlockValidForPaintEventHandler;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.PaintOnBlockEventHandler;
import com.vanym.paniclecraft.core.component.painting.WorldUnloadEventHandler;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.item.ItemPaintRemover;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.item.ItemPaintingTool;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.network.NetworkUtils;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.network.message.MessagePaintingViewAddPicture;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.recipe.RecipeColorizeByDye;
import com.vanym.paniclecraft.recipe.RecipeColorizeByFiller;
import com.vanym.paniclecraft.recipe.RecipePaintingCombine;
import com.vanym.paniclecraft.recipe.RecipePaintingFrame;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameAddPainting;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameRemovePainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModComponentPainting extends ModComponent {
    
    public final int MAX_WIDTH = 256;
    public final int MAX_HEIGHT = 256;
    public final Color DEFAULT_COLOR = new Color(200, 200, 200);
    
    @ModComponentObject
    public ItemPainting itemPainting;
    @ModComponentObject
    public ItemPaintingFrame itemPaintingFrame;
    @ModComponentObject
    public ItemPaintBrush itemPaintBrush;
    @ModComponentObject
    public ItemPaintBrush itemPaintBrushSmall;
    @ModComponentObject
    public ItemPaintBrush itemPaintFiller;
    @ModComponentObject
    public ItemPaintBrush itemPaintColorPicker;
    @ModComponentObject
    public ItemPaintRemover itemPaintRemover;
    @ModComponentObject
    public ItemPaintRemover itemPaintRemoverSmall;
    @ModComponentObject
    public ItemPalette itemPalette;
    @ModComponentObject
    public BlockPainting blockPainting;
    @ModComponentObject
    public BlockPaintingFrame blockPaintingFrame;
    
    @ModComponentObject
    public TileEntityType<TileEntityPainting> tileEntityPainting;
    @ModComponentObject
    public TileEntityType<TileEntityPaintingFrame> tileEntityPaintingFrame;
    
    @ModComponentObject
    public ContainerType<ContainerPalette> containerPalette;
    @ModComponentObject
    public ContainerType<ContainerPaintingViewBase> containerPaintingView;
    
    @ModComponentObject
    public EntityType<EntityPaintOnBlock> entityTypePaintOnBlock;
    
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypeColorizeByDye;
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypeColorizeByFiller;
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypePaintingCombine;
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypePaintingFrame;
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypePaintingFrameAdd;
    @ModComponentObject
    public IRecipeSerializer<?> recipeTypePaintingFrameRemove;
    
    public Config config;
    public ServerConfig server;
    
    @OnlyIn(Dist.CLIENT)
    public PictureTextureCache textureCache;
    
    @OnlyIn(Dist.CLIENT)
    protected TileEntityPaintingRenderer paintingTileRenderer;
    @OnlyIn(Dist.CLIENT)
    protected TileEntityPaintingFrameRenderer paintingFrameTileRenderer;
    @OnlyIn(Dist.CLIENT)
    protected PaintingSpecialSelectionBox paintingSpecialSelectionBox;
    
    @OnlyIn(Dist.CLIENT)
    public ClientConfig clientConfig;
    
    @Override
    public void init(Map<ModConfig.Type, ForgeConfigSpec.Builder> configBuilders) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        
        ForgeConfigSpec.Builder serverBuilder = configBuilders.get(ModConfig.Type.SERVER);
        this.config = new Config(serverBuilder);
        this.server = new ServerConfig(serverBuilder);
        
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> {
            this.textureCache = new PictureTextureCache();
            this.paintingSpecialSelectionBox = null;
            ForgeConfigSpec.Builder clientBuilder = configBuilders.get(ModConfig.Type.CLIENT);
            this.clientConfig = new ClientConfig(clientBuilder);
        });
        
        Arrays.asList(new CommandPainting(), new CommandPaintOnBlock())
              .forEach(Core.instance.command::addSubCommand);
        
        this.blockPainting = new BlockPainting();
        this.blockPaintingFrame = new BlockPaintingFrame();
        
        this.itemPainting = new ItemPainting(this.blockPainting);
        this.itemPaintingFrame = new ItemPaintingFrame(this.blockPaintingFrame);
        this.itemPaintBrush = new ItemPaintBrush(ItemPaintBrush.Type.BRUSH);
        this.itemPaintBrushSmall = new ItemPaintBrush(ItemPaintBrush.Type.SMALLBRUSH);
        this.itemPaintFiller = new ItemPaintBrush(ItemPaintBrush.Type.FILLER);
        this.itemPaintColorPicker = new ItemPaintBrush(ItemPaintBrush.Type.COLORPICKER);
        this.itemPaintRemover = new ItemPaintRemover(ItemPaintRemover.Type.REMOVER);
        this.itemPaintRemoverSmall = new ItemPaintRemover(ItemPaintRemover.Type.SMALLREMOVER);
        this.itemPalette = new ItemPalette();
        
        this.tileEntityPainting = new TileEntityType<>(
                TileEntityPainting::new,
                Collections.singleton(this.blockPainting),
                null);
        this.tileEntityPainting.setRegistryName(TileEntityPainting.ID);
        this.tileEntityPaintingFrame = new TileEntityType<>(
                TileEntityPaintingFrame::new,
                Collections.singleton(this.blockPaintingFrame),
                null);
        this.tileEntityPaintingFrame.setRegistryName(TileEntityPaintingFrame.ID);
        
        this.containerPalette = new ContainerType<>(ContainerPalette::new);
        this.containerPalette.setRegistryName(this.itemPalette.getRegistryName());
        this.containerPaintingView =
                IForgeContainerType.create(ContainerPaintingViewClient::create);
        this.containerPaintingView.setRegistryName("paintingview");
        
        this.entityTypePaintOnBlock = EntityPaintOnBlock.createType();
        this.entityTypePaintOnBlock.setRegistryName(EntityPaintOnBlock.ID);
        
        this.recipeTypeColorizeByDye = new SpecialRecipeSerializer<>(
                RecipeColorizeByDye::new).setRegistryName("colorize_by_dye");
        this.recipeTypeColorizeByFiller = new SpecialRecipeSerializer<>(
                RecipeColorizeByFiller::new).setRegistryName("colorize_by_filler");
        this.recipeTypePaintingCombine =
                new RecipePaintingCombine.Serializer().setRegistryName("painting_combine");
        this.recipeTypePaintingFrame =
                new RecipePaintingFrame.Serializer().setRegistryName("paintingframe");
        this.recipeTypePaintingFrameAdd =
                new RecipePaintingFrameAddPainting.Serializer().setRegistryName("paintingframe_add_painting");
        this.recipeTypePaintingFrameRemove = new SpecialRecipeSerializer<>(
                RecipePaintingFrameRemovePainting::new).setRegistryName("paintingframe_remove_painting");
    }
    
    @SubscribeEvent
    protected void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());
        MinecraftForge.EVENT_BUS.register(new PaintOnBlockEventHandler());
        
        Core.instance.network.registerMessage(30, MessagePaintingToolUse.class,
                                              MessagePaintingToolUse::encode,
                                              MessagePaintingToolUse::decode,
                                              NetworkUtils.handleInWorld(MessagePaintingToolUse::handleInWorld));
        Core.instance.network.registerMessage(32, MessagePaletteSetColor.class,
                                              MessagePaletteSetColor::encode,
                                              MessagePaletteSetColor::decode,
                                              NetworkUtils.handleInWorld(MessagePaletteSetColor::handleInWorld));
        Core.instance.network.registerMessage(34, MessagePaintingViewAddPicture.class,
                                              MessagePaintingViewAddPicture::encode,
                                              MessagePaintingViewAddPicture::decode,
                                              NetworkUtils.handleInWorld(MessagePaintingViewAddPicture::handleInWorld));
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.NORMAL, this::configChanged);
        this.applyConfig();
    }
    
    // Subscribes in setup
    protected void configChanged(ModConfig.ConfigReloading event) {
        if (event.getConfig().getType() != ModConfig.Type.SERVER
            || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
            return;
        }
        this.applyConfig();
    }
    
    protected void applyConfig() {
        if (this.config.anyBlockValidForPaint) {
            MinecraftForge.EVENT_BUS.register(AnyBlockValidForPaintEventHandler.instance);
        } else {
            MinecraftForge.EVENT_BUS.unregister(AnyBlockValidForPaintEventHandler.instance);
        }
        if (this.config.copyOnAnvil) {
            MinecraftForge.EVENT_BUS.register(AnvilCopyEventHandler.instance);
        } else {
            MinecraftForge.EVENT_BUS.unregister(AnvilCopyEventHandler.instance);
        }
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    protected void setupClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(this.containerPalette, GuiPalette::new);
        ScreenManager.registerFactory(this.containerPaintingView, GuiPaintingEditView::create);
        
        MinecraftForge.EVENT_BUS.register(this.textureCache);
        
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameTileRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityPaintOnBlock.class,
                                                         EntityPaintOnBlockRenderer::new);
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(EventPriority.NORMAL, this::configChangedClient);
        this.applyConfigClient();
    }
    
    // Subscribes in setupClient
    @OnlyIn(Dist.CLIENT)
    protected void configChangedClient(ModConfig.ConfigReloading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT
            || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
            return;
        }
        this.applyConfigClient();
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void applyConfigClient() {
        if (this.clientConfig.perFrameBrushUse) {
            this.getItems()
                .stream()
                .filter(ItemPaintingTool.class::isInstance)
                .forEach(MinecraftForge.EVENT_BUS::register);
        } else {
            this.getItems()
                .stream()
                .filter(ItemPaintingTool.class::isInstance)
                .forEach(MinecraftForge.EVENT_BUS::unregister);
        }
        
        this.paintingTileRenderer.renderFrameType =
                this.clientConfig.renderPaintingTilePartFrameType;
        this.paintingTileRenderer.renderPictureType =
                this.clientConfig.renderPaintingTilePartPictureType;
        if (this.clientConfig.renderPaintingTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class,
                                                         this.paintingTileRenderer);
        } else {
            TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
            synchronized (dispatcher) {
                dispatcher.renderers.remove(TileEntityPainting.class,
                                            this.paintingTileRenderer);
            }
        }
        
        this.paintingFrameTileRenderer.renderFrameType =
                this.clientConfig.renderPaintingFrameTilePartFrameType;
        this.paintingFrameTileRenderer.renderPictureType =
                this.clientConfig.renderPaintingFrameTilePartPictureType;
        if (this.clientConfig.renderPaintingFrameTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintingFrame.class,
                                                         this.paintingFrameTileRenderer);
        } else {
            TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
            synchronized (dispatcher) {
                dispatcher.renderers.remove(TileEntityPaintingFrame.class,
                                            this.paintingFrameTileRenderer);
            }
        }
        
        if (this.paintingSpecialSelectionBox != null) {
            MinecraftForge.EVENT_BUS.unregister(this.paintingSpecialSelectionBox);
            this.paintingSpecialSelectionBox = null;
        }
        if (this.clientConfig.paintingSpecialSelectionBox
            || this.clientConfig.paintingNoneSelectionBox) {
            this.paintingSpecialSelectionBox = new PaintingSpecialSelectionBox(
                    this.clientConfig.paintingNoneSelectionBox,
                    this.clientConfig.paintingSpecialSelectionBoxColor);
            MinecraftForge.EVENT_BUS.register(this.paintingSpecialSelectionBox);
        }
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void registerItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        this.getItems()
            .stream()
            .filter(ItemPaintBrush.class::isInstance)
            .map(ItemPaintBrush.class::cast)
            .forEach(i->colors.register(i.color(), i));
    }
    
    @Override
    public String getName() {
        return "painting";
    }
    
    public class Config {
        
        public final IPictureSize paintingDefaultSize = new DefaultPictureSize();
        public final IPictureSize paintOnBlockDefaultSize = new DefaultPaintOnBlockSize();
        
        public final SortedMap<Integer, Double> brushRadiuses;
        public final SortedMap<Integer, Double> smallBrushRadiuses;
        
        public final SortedMap<Integer, Double> removerRadiuses;
        public final SortedMap<Integer, Double> smallRemoverRadiuses;
        
        public int paintingPlaceStack = 2;
        protected final ForgeConfigSpec.IntValue paintingPlaceStackSpec;
        protected int paintingDefaultWidth = 16;
        protected final ForgeConfigSpec.IntValue paintingDefaultWidthSpec;
        protected int paintingDefaultHeight = 16;
        protected final ForgeConfigSpec.IntValue paintingDefaultHeightSpec;
        
        public boolean allowPaintOnBlock = false;
        protected final ForgeConfigSpec.BooleanValue allowPaintOnBlockSpec;
        public boolean anyBlockValidForPaint = false;
        protected final ForgeConfigSpec.BooleanValue anyBlockValidForPaintSpec;
        protected int paintOnBlockDefaultWidth = 16;
        protected final ForgeConfigSpec.IntValue paintOnBlockDefaultWidthSpec;
        protected int paintOnBlockDefaultHeight = 16;
        protected final ForgeConfigSpec.IntValue paintOnBlockDefaultHeightSpec;
        
        public int paintingMaxCraftableWidth = 64;
        protected final ForgeConfigSpec.IntValue paintingMaxCraftableWidthSpec;
        public int paintingMaxCraftableHeight = 64;
        protected final ForgeConfigSpec.IntValue paintingMaxCraftableHeightSpec;
        
        public boolean copyOnAnvil = true;
        protected final ForgeConfigSpec.BooleanValue copyOnAnvilSpec;
        public int copyOnAnvilCost = 5;
        protected final ForgeConfigSpec.IntValue copyOnAnvilCostSpec;
        
        protected final SortedMap<Integer, Double> iBrushRadiuses = new TreeMap<>();
        protected final ForgeConfigSpec.ConfigValue<
            List<? extends String>> brushRadiusesSpecSpecial;
        protected final SortedMap<Integer, Double> iSmallBrushRadiuses = new TreeMap<>();
        protected final ForgeConfigSpec.ConfigValue<
            List<? extends String>> smallBrushRadiusesSpecSpecial;
        
        protected final SortedMap<Integer, Double> iRemoverRadiuses = new TreeMap<>();
        protected final ForgeConfigSpec.ConfigValue<
            List<? extends String>> removerRadiusesSpecSpecial;
        protected final SortedMap<Integer, Double> iSmallRemoverRadiuses = new TreeMap<>();
        protected final ForgeConfigSpec.ConfigValue<
            List<? extends String>> smallRemoverRadiusesSpecSpecial;
        
        protected Config(ForgeConfigSpec.Builder serverBuilder) {
            this.brushRadiuses = Collections.unmodifiableSortedMap(this.iBrushRadiuses);
            this.smallBrushRadiuses = Collections.unmodifiableSortedMap(this.iSmallBrushRadiuses);
            
            this.removerRadiuses = Collections.unmodifiableSortedMap(this.iRemoverRadiuses);
            this.smallRemoverRadiuses =
                    Collections.unmodifiableSortedMap(this.iSmallRemoverRadiuses);
            
            this.iBrushRadiuses.put(16, 3.5D);
            this.iBrushRadiuses.put(32, 6.2D);
            
            this.iSmallBrushRadiuses.put(0, 0.1D);
            
            this.iRemoverRadiuses.putAll(this.iBrushRadiuses);
            this.iSmallRemoverRadiuses.putAll(this.iSmallBrushRadiuses);
            
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            
            serverBuilder.push(ModComponentPainting.this.getName());
            this.paintingPlaceStackSpec =
                    serverBuilder.defineInRange("paintingPlaceStack", 2, 0, 64);
            this.paintingDefaultWidthSpec =
                    serverBuilder.defineInRange("paintingDefaultWidth", 16, 1,
                                                ModComponentPainting.this.MAX_WIDTH);
            this.paintingDefaultHeightSpec =
                    serverBuilder.comment("(recommended to equals width)")
                                 .defineInRange("paintingDefaultHeight", 16, 1,
                                                ModComponentPainting.this.MAX_HEIGHT);
            this.allowPaintOnBlockSpec = serverBuilder.define("allowPaintOnBlock", false);
            this.anyBlockValidForPaintSpec = serverBuilder.define("anyBlockValidForPaint", false);
            this.paintOnBlockDefaultWidthSpec =
                    serverBuilder.defineInRange("paintOnBlockDefaultWidth", 16, 1,
                                                ModComponentPainting.this.MAX_WIDTH);
            this.paintOnBlockDefaultHeightSpec =
                    serverBuilder.comment("(highly recommended to equals width)")
                                 .defineInRange("paintOnBlockDefaultHeight", 16, 1,
                                                ModComponentPainting.this.MAX_HEIGHT);
            this.paintingMaxCraftableWidthSpec =
                    serverBuilder.defineInRange("paintingMaxCraftableWidth", 64, 1,
                                                ModComponentPainting.this.MAX_WIDTH);
            this.paintingMaxCraftableHeightSpec =
                    serverBuilder.defineInRange("paintingMaxCraftableHeight", 64, 1,
                                                ModComponentPainting.this.MAX_HEIGHT);
            this.copyOnAnvilSpec = serverBuilder.define("copyOnAnvil", true);
            this.copyOnAnvilCostSpec = serverBuilder.defineInRange("copyOnAnvilCost", 5, 0, 40);
            
            final String RADIUSES_COMMENT = "radius depending on picture size\n";
            this.brushRadiusesSpecSpecial =
                    serverBuilder.comment(RADIUSES_COMMENT)
                                 .defineList("brushRadiuses",
                                             Arrays.asList(DEFAULT_BRUSH_RADIUSES),
                                             ModComponentPainting::validateRadius);
            this.smallBrushRadiusesSpecSpecial =
                    serverBuilder.comment(RADIUSES_COMMENT)
                                 .defineList("smallBrushRadiuses",
                                             Arrays.asList(DEFAULT_SMALL_BRUSH_RADIUSES),
                                             ModComponentPainting::validateRadius);
            this.removerRadiusesSpecSpecial =
                    serverBuilder.comment(RADIUSES_COMMENT)
                                 .defineList("removerRadiuses",
                                             Arrays.asList(DEFAULT_BRUSH_RADIUSES),
                                             ModComponentPainting::validateRadius);
            this.smallRemoverRadiusesSpecSpecial =
                    serverBuilder.comment(RADIUSES_COMMENT)
                                 .defineList("smallRemoverRadiuses",
                                             Arrays.asList(DEFAULT_SMALL_BRUSH_RADIUSES),
                                             ModComponentPainting::validateRadius);
            serverBuilder.pop();
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        protected void configChanged(ModConfig.ModConfigEvent event) {
            if (event.getConfig().getType() != ModConfig.Type.SERVER
                || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
                return;
            }
            this.configChanged();
        }
        
        protected void configChanged() {
            putValues(this);
            parseRadiuses(this.brushRadiusesSpecSpecial.get(), this.iBrushRadiuses);
            parseRadiuses(this.smallBrushRadiusesSpecSpecial.get(), this.iSmallBrushRadiuses);
            parseRadiuses(this.removerRadiusesSpecSpecial.get(), this.iRemoverRadiuses);
            parseRadiuses(this.smallRemoverRadiusesSpecSpecial.get(), this.iSmallRemoverRadiuses);
        }
        
        protected class DefaultPictureSize implements IPictureSize {
            
            @Override
            public int getWidth() {
                return Config.this.paintingDefaultWidth;
            }
            
            @Override
            public int getHeight() {
                return Config.this.paintingDefaultHeight;
            }
        }
        
        protected class DefaultPaintOnBlockSize implements IPictureSize {
            
            @Override
            public int getWidth() {
                return Config.this.paintOnBlockDefaultWidth;
            }
            
            @Override
            public int getHeight() {
                return Config.this.paintOnBlockDefaultHeight;
            }
        }
    }
    
    protected static final String[] DEFAULT_BRUSH_RADIUSES = new String[]{"1: 1.5",
                                                                          "12: 2.5",
                                                                          "16: 3.5",
                                                                          "24: 5.2",
                                                                          "32: 6.2",
                                                                          "48: 7.5",
                                                                          "64: 10.5"};
    
    protected static final String[] DEFAULT_SMALL_BRUSH_RADIUSES = new String[]{"1: 0.1"};
    
    public class ServerConfig {
        
        public boolean freePaintingView = true;
        protected final ForgeConfigSpec.BooleanValue freePaintingViewSpec;
        public boolean freePaintingEditView = false;
        protected final ForgeConfigSpec.BooleanValue freePaintingEditViewSpec;
        public boolean freePaintingViewTo = false;
        protected final ForgeConfigSpec.BooleanValue freePaintingViewToSpec;
        public boolean freePaintingEditViewTo = false;
        protected final ForgeConfigSpec.BooleanValue freePaintingEditViewToSpec;
        public boolean freePaintOnBlockView = true;
        protected final ForgeConfigSpec.BooleanValue freePaintOnBlockViewSpec;
        public boolean freePaintOnBlockEditView = false;
        protected final ForgeConfigSpec.BooleanValue freePaintOnBlockEditViewSpec;
        public boolean freePaintOnBlockViewTo = false;
        protected final ForgeConfigSpec.BooleanValue freePaintOnBlockViewToSpec;
        public boolean freePaintOnBlockEditViewTo = false;
        protected final ForgeConfigSpec.BooleanValue freePaintOnBlockEditViewToSpec;
        
        protected ServerConfig(ForgeConfigSpec.Builder serverBuilder) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            
            serverBuilder.push(ModComponentPainting.this.getName());
            final String COMMENT_PREFIX =
                    String.format("allow any player use command:\n/%s ", CommandMod3.NAME);
            this.freePaintingViewSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "painting view" + "\n")
                                 .define("freePaintingView", true);
            this.freePaintingEditViewSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "painting editview" + "\n")
                                 .define("freePaintingEditView", false);
            this.freePaintingViewToSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "painting viewto" + "\n")
                                 .define("freePaintingViewTo", false);
            this.freePaintingEditViewToSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "painting editviewto" + "\n")
                                 .define("freePaintingEditViewTo", false);
            this.freePaintOnBlockViewSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "paintonblock view" + "\n")
                                 .define("freePaintOnBlockView", true);
            this.freePaintOnBlockEditViewSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "paintonblock editview" + "\n")
                                 .define("freePaintOnBlockEditView", false);
            this.freePaintOnBlockViewToSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "paintonblock viewto" + "\n")
                                 .define("freePaintOnBlockViewTo", false);
            this.freePaintOnBlockEditViewToSpec =
                    serverBuilder.comment(COMMENT_PREFIX + "paintonblock editviewto" + "\n")
                                 .define("freePaintOnBlockEditViewTo", false);
            serverBuilder.pop();
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        protected void configChanged(ModConfig.ModConfigEvent event) {
            if (event.getConfig().getType() != ModConfig.Type.SERVER
                || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
                return;
            }
            this.configChanged();
        }
        
        protected void configChanged() {
            putValues(this);
        }
    }
    
    public class ClientConfig {
        
        public boolean perFrameBrushUse = true;
        protected final ForgeConfigSpec.BooleanValue perFrameBrushUseSpec;
        
        public boolean forceUnhidePaintRemover = false;
        protected final ForgeConfigSpec.BooleanValue forceUnhidePaintRemoverSpec;
        
        public boolean paintingFrameInfoSideLetters = false;
        protected final ForgeConfigSpec.BooleanValue paintingFrameInfoSideLettersSpec;
        
        public boolean renderPaintingTile = true;
        protected final ForgeConfigSpec.BooleanValue renderPaintingTileSpec;
        public int renderPaintingTilePartFrameType = 1;
        protected final ForgeConfigSpec.IntValue renderPaintingTilePartFrameTypeSpec;
        public int renderPaintingTilePartPictureType = 2;
        protected final ForgeConfigSpec.IntValue renderPaintingTilePartPictureTypeSpec;
        public double renderPaintingTileMaxRenderDistanceSquared = Math.pow(128.0D, 2);
        protected final ForgeConfigSpec.DoubleValue renderPaintingTileMaxRenderDistanceSpec;
        public boolean renderPaintingFrameTile = true;
        protected final ForgeConfigSpec.BooleanValue renderPaintingFrameTileSpec;
        public int renderPaintingFrameTilePartFrameType = 0;
        protected final ForgeConfigSpec.IntValue renderPaintingFrameTilePartFrameTypeSpec;
        public int renderPaintingFrameTilePartPictureType = 2;
        protected final ForgeConfigSpec.IntValue renderPaintingFrameTilePartPictureTypeSpec;
        public double renderPaintingFrameTileMaxRenderDistanceSquared = Math.pow(128.0D, 2);
        protected final ForgeConfigSpec.DoubleValue renderPaintingFrameTileMaxRenderDistanceSpec;
        public int renderPaintOnBlockPartPictureType = 2;
        protected final ForgeConfigSpec.IntValue renderPaintOnBlockPartPictureTypeSpec;
        public double renderPaintOnBlockMaxRenderDistanceSquared = Math.pow(256.0D, 2);
        protected final ForgeConfigSpec.DoubleValue renderPaintOnBlockMaxRenderDistanceSpec;
        public boolean renderProfiling = false;
        protected final ForgeConfigSpec.BooleanValue renderProfilingSpec;
        public boolean paintingSpecialSelectionBox = true;
        protected final ForgeConfigSpec.BooleanValue paintingSpecialSelectionBoxSpec;
        public Color paintingSpecialSelectionBoxColor = null;
        protected final ForgeConfigSpec.ConfigValue<
            String> paintingSpecialSelectionBoxColorSpecSpecial;
        public boolean paintingNoneSelectionBox = false;
        protected final ForgeConfigSpec.BooleanValue paintingNoneSelectionBoxSpec;
        
        protected ClientConfig(ForgeConfigSpec.Builder clientBuilder) {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            
            clientBuilder.push(ModComponentPainting.this.getName());
            this.perFrameBrushUseSpec = clientBuilder.define("perFrameBrushUse", true);
            this.forceUnhidePaintRemoverSpec =
                    clientBuilder.comment("show paint remover in creative tab even\n if paint on block is not allowed")
                                 .define("forceUnhidePaintRemover", true);
            this.paintingFrameInfoSideLettersSpec =
                    clientBuilder.define("paintingFrameInfoSideLetters", false);
            clientBuilder.pop();
            
            clientBuilder.push(CLIENT_RENDER);
            final String PART_RENDER_TYPE = String.join("\n", "render type of specific part",
                                                        "-1: disable", "0: smooth lighting off",
                                                        "1: smooth lighting minimum",
                                                        "2: smooth lighting maximum", "");
            this.renderPaintingTileSpec = clientBuilder.define("paintingTile", true);
            this.renderPaintingTilePartFrameTypeSpec =
                    clientBuilder.comment(PART_RENDER_TYPE)
                                 .defineInRange("paintingTilePartFrameType", 1, -1, 2);
            this.renderPaintingTilePartPictureTypeSpec =
                    clientBuilder.comment(PART_RENDER_TYPE)
                                 .defineInRange("paintingTilePartPictureType", 2, -1, 2);
            this.renderPaintingTileMaxRenderDistanceSpec =
                    clientBuilder.defineInRange("renderPaintingTileMaxRenderDistance",
                                                128.0D, 0.0D, 1024.0D);
            this.renderPaintingFrameTileSpec = clientBuilder.define("paintingFrameTile", true);
            this.renderPaintingFrameTilePartFrameTypeSpec =
                    clientBuilder.comment(PART_RENDER_TYPE)
                                 .defineInRange("paintingFrameTilePartFrameType", 0, -1, 2);
            this.renderPaintingFrameTilePartPictureTypeSpec =
                    clientBuilder.comment(PART_RENDER_TYPE)
                                 .defineInRange("paintingFrameTilePartPictureType", 2, -1, 2);
            this.renderPaintingFrameTileMaxRenderDistanceSpec =
                    clientBuilder.defineInRange("renderPaintingFrameTileMaxRenderDistance",
                                                128.0D, 0.0D, 1024.0D);
            this.renderPaintOnBlockPartPictureTypeSpec =
                    clientBuilder.comment(PART_RENDER_TYPE)
                                 .defineInRange("paintOnBlockPartPictureType", 2, -1, 2);
            this.renderPaintOnBlockMaxRenderDistanceSpec =
                    clientBuilder.defineInRange("renderPaintOnBlockMaxRenderDistance",
                                                256.0D, 0.0D, 1024.0D);
            this.renderProfilingSpec = clientBuilder.define("paintingRenderProfiling", false);
            this.paintingSpecialSelectionBoxSpec =
                    clientBuilder.define("paintingSpecialSelectionBox", true);
            this.paintingSpecialSelectionBoxColorSpecSpecial =
                    clientBuilder.comment("Color of selection box. Example: #00ff00")
                                 .define("paintingSpecialSelectionBoxColor", "");
            this.paintingNoneSelectionBoxSpec =
                    clientBuilder.define("paintingNoneSelectionBox", false);
            clientBuilder.pop();
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        protected void configChanged(ModConfig.ModConfigEvent event) {
            if (event.getConfig().getType() != ModConfig.Type.CLIENT
                || !event.getConfig().getModId().equals(DEF.MOD_ID)) {
                return;
            }
            this.configChanged();
        }
        
        protected void configChanged() {
            putValues(this);
            this.renderPaintingTileMaxRenderDistanceSquared =
                    Math.pow(this.renderPaintingTileMaxRenderDistanceSpec.get(), 2.0D);
            this.renderPaintingFrameTileMaxRenderDistanceSquared =
                    Math.pow(this.renderPaintingFrameTileMaxRenderDistanceSpec.get(), 2.0D);
            this.renderPaintOnBlockMaxRenderDistanceSquared =
                    Math.pow(this.renderPaintOnBlockMaxRenderDistanceSpec.get(), 2.0D);
            try {
                this.paintingSpecialSelectionBoxColor =
                        Color.decode(this.paintingSpecialSelectionBoxColorSpecSpecial.get());
            } catch (NumberFormatException e) {
                this.paintingSpecialSelectionBoxColor = null;
            }
        }
    }
    
    protected static void putValues(Object obj) {
        Arrays.stream(obj.getClass().getDeclaredFields())
              .filter(f->ForgeConfigSpec.ConfigValue.class.isAssignableFrom(f.getType()))
              .filter(f->f.getName().endsWith("Spec"))
              .forEach(f-> {
                  try {
                      ForgeConfigSpec.ConfigValue<?> v = (ForgeConfigSpec.ConfigValue<?>)f.get(obj);
                      String specName = f.getName();
                      String name = specName.substring(0, specName.length() - 4);
                      Field fv = obj.getClass().getDeclaredField(name);
                      if (fv != null) {
                          fv.set(obj, v.get());
                      }
                  } catch (NoSuchFieldException
                           | SecurityException
                           | IllegalArgumentException
                           | IllegalAccessException e) {
                  }
              });
    }
    
    protected static void parseRadiuses(
            Collection<? extends String> list,
            Map<Integer, Double> radiuses) {
        radiuses.clear();
        for (String line : list) {
            parseRadius(line, radiuses);
        }
    }
    
    protected static void parseRadius(String line, Map<Integer, Double> radiuses) {
        int cut = line.indexOf(':');
        if (cut == -1) {
            return;
        }
        try {
            radiuses.put(Integer.parseInt(line.substring(0, cut)),
                         Double.parseDouble(line.substring(cut + 1)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    protected static boolean validateRadius(Object line) {
        return line instanceof String && validateRadius((String)line);
    }
    
    protected static boolean validateRadius(String line) {
        int cut = line.indexOf(':');
        if (cut == -1) {
            return false;
        }
        try {
            Integer.parseInt(line.substring(0, cut));
            Double.parseDouble(line.substring(cut + 1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
