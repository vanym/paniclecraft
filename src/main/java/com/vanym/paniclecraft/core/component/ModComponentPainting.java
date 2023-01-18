package com.vanym.paniclecraft.core.component;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.ModConfig;
import com.vanym.paniclecraft.client.renderer.PaintingSpecialSelectionBox;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.entity.EntityPaintOnBlockRenderer;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.command.CommandPaintOnBlock;
import com.vanym.paniclecraft.command.CommandPainting;
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
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.network.message.MessageOpenPaintingView;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.recipe.RecipeColorizeByDye;
import com.vanym.paniclecraft.recipe.RecipeColorizeByFiller;
import com.vanym.paniclecraft.recipe.RecipeDummy;
import com.vanym.paniclecraft.recipe.RecipePaintingCombine;
import com.vanym.paniclecraft.recipe.RecipePaintingFrame;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameAddPainting;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameRemovePainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModComponentPainting implements ModComponent {
    
    public final int MAX_WIDTH = 256;
    public final int MAX_HEIGHT = 256;
    public final Color DEFAULT_COLOR = new Color(200, 200, 200);
    
    public ItemPainting itemPainting;
    public ItemPaintingFrame itemPaintingFrame;
    public ItemPaintBrush itemPaintBrush;
    public ItemPaintRemover itemPaintRemover;
    public ItemPalette itemPalette;
    public BlockPainting blockPainting;
    public BlockPaintingFrame blockPaintingFrame;
    
    public ChangeableConfig config = new ChangeableConfig();
    
    @SideOnly(Side.CLIENT)
    protected TileEntityPaintingRenderer paintingTileRenderer;
    @SideOnly(Side.CLIENT)
    protected TileEntityPaintingFrameRenderer paintingFrameTileRenderer;
    @SideOnly(Side.CLIENT)
    protected PictureTextureCache textureCache;
    @SideOnly(Side.CLIENT)
    protected ItemRendererPainting paintingItemRenderer;
    @SideOnly(Side.CLIENT)
    protected ItemRendererPaintingFrame paintingFrameItemRenderer;
    @SideOnly(Side.CLIENT)
    protected PaintingSpecialSelectionBox paintingSpecialSelectionBox = null;
    @SideOnly(Side.CLIENT)
    protected EntityPaintOnBlockRenderer paintOnBlockRenderer;
    
    @SideOnly(Side.CLIENT)
    public ChangeableClientConfig clientConfig = new ChangeableClientConfig();
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        
        this.itemPainting = new ItemPainting();
        this.itemPaintBrush = new ItemPaintBrush();
        this.itemPaintRemover = new ItemPaintRemover();
        this.itemPalette = new ItemPalette();
        Core.instance.registerItem(this.itemPainting);
        Core.instance.registerItem(this.itemPaintBrush);
        Core.instance.registerItem(this.itemPaintRemover);
        Core.instance.registerItem(this.itemPalette);
        
        this.blockPainting = new BlockPainting();
        GameRegistry.registerBlock(this.blockPainting, null, this.blockPainting.getName());
        GameRegistry.registerTileEntity(TileEntityPainting.class, TileEntityPainting.ID);
        
        this.blockPaintingFrame = new BlockPaintingFrame();
        GameRegistry.registerBlock(this.blockPaintingFrame, ItemPaintingFrame.class,
                                   this.blockPaintingFrame.getName());
        this.itemPaintingFrame = (ItemPaintingFrame)Item.getItemFromBlock(this.blockPaintingFrame);
        GameRegistry.registerTileEntity(TileEntityPaintingFrame.class, TileEntityPaintingFrame.ID);
        
        EntityRegistry.registerModEntity(EntityPaintOnBlock.class,
                                         EntityPaintOnBlock.IN_MOD_ID, 33,
                                         Core.instance, 64, 1, true);
        
        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());
        MinecraftForge.EVENT_BUS.register(new PaintOnBlockEventHandler());
        
        Arrays.asList(new CommandPainting(), new CommandPaintOnBlock())
              .forEach(Core.instance.command::addSubCommand);
        
        Core.instance.network.registerMessage(MessagePaintingToolUse.class,
                                              MessagePaintingToolUse.class, 30,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessagePaletteSetColor.class,
                                              MessagePaletteSetColor.class, 32,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageOpenPaintingView.class,
                                              MessageOpenPaintingView.class, 33,
                                              Side.CLIENT);
        this.initRecipe(config);
        this.config = new ChangeableConfig().read(config);
        this.applyConfig();
    }
    
    @Override
    public void configChanged(ModConfig config) {
        this.config.read(config);
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
    
    protected void initRecipe(Configuration config) {
        if (config.getBoolean("craftingRecipePaintBrush", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getBrush(),
                    "w",
                    "s",
                    Character.valueOf('w'),
                    new ItemStack(Blocks.wool, 1, 0),
                    Character.valueOf('s'),
                    "stickWood");
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintBrush", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getSmallBrush(),
                    "f",
                    "s",
                    Character.valueOf('f'),
                    Items.feather,
                    Character.valueOf('s'),
                    "stickWood");
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipePaintFiller", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getFiller(),
                    "w",
                    "b",
                    Character.valueOf('w'),
                    "dyeWhite",
                    Character.valueOf('b'),
                    Items.bowl);
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipeColorPicker", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getColorPicker(),
                    false,
                    " b",
                    "b ",
                    Character.valueOf('b'),
                    Items.glass_bottle);
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipePaintRemoverFromStick", this.getName(), false, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getRemover(),
                    "i",
                    "s",
                    Character.valueOf('i'),
                    "ingotIron",
                    Character.valueOf('s'),
                    "stickWood");
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipePaintRemoverFromBrush", this.getName(), false, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getRemover(),
                    "i",
                    "b",
                    Character.valueOf('i'),
                    "ingotIron",
                    Character.valueOf('b'),
                    this.itemPaintBrush.getBrush());
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintRemoverFromStick", this.getName(), false,
                              "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getSmallRemover(),
                    "f",
                    "s",
                    Character.valueOf('f'),
                    Items.flint,
                    Character.valueOf('s'),
                    "stickWood");
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintRemoverFromBrush", this.getName(), false,
                              "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getSmallRemover(),
                    "f",
                    "b",
                    Character.valueOf('f'),
                    Items.flint,
                    Character.valueOf('b'),
                    this.itemPaintBrush.getSmallBrush());
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipeColorizeByDye", this.getName(), true, "")) {
            RecipeColorizeByDye recipe = new RecipeColorizeByDye();
            GameRegistry.addRecipe(recipe);
            RecipeDummy.getColorizeByDyeDummies().forEach(GameRegistry::addRecipe);
        }
        if (config.getBoolean("craftingRecipeColorizeByFiller", this.getName(), true, "")) {
            RecipeColorizeByFiller recipe = new RecipeColorizeByFiller();
            GameRegistry.addRecipe(recipe);
            FMLCommonHandler.instance().bus().register(recipe);
            RecipeDummy.getColorizeByFillerDummies().forEach(GameRegistry::addRecipe);
        }
        if (config.getBoolean("craftingRecipePalette", this.getName(), false, "")) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(
                    new ItemStack(this.itemPalette),
                    "slabWood",
                    "dyeRed",
                    "dyeGreen",
                    "dyeBlue");
            GameRegistry.addRecipe(recipe);
        }
        int paintingsAmount = config.getInt("craftingRecipePaintingAmount", this.getName(), 8,
                                            0, 64, "\'0\' to disable");
        if (paintingsAmount > 0) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    new ItemStack(this.itemPainting, paintingsAmount),
                    "wsw",
                    "scs",
                    "wsw",
                    Character.valueOf('w'),
                    "plankWood",
                    Character.valueOf('s'),
                    "stickWood",
                    Character.valueOf('c'),
                    Blocks.wool);
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipePaintingClear", this.getName(), true, "")) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemPainting), this.itemPainting);
        }
        String[] paintingCombines =
                config.getStringList("craftingRecipePaintingCombine", this.getName(),
                                     new String[]{"2x2"},
                                     "", new String[]{"2x1", "1x2", "2x2",
                                                      "3x1", "3x2", "1x3", "2x3", "3x3"});
        final Pattern combinePattern = Pattern.compile("(\\d+)x(\\d+)");
        Arrays.asList(paintingCombines)
              .stream()
              .map(combinePattern::matcher)
              .filter(Matcher::matches)
              .map(m-> {
                  int x = Integer.parseInt(m.group(1));
                  int y = Integer.parseInt(m.group(2));
                  return new RecipePaintingCombine(x, y);
              })
              .forEach(GameRegistry::addRecipe);
        if (config.getBoolean("craftingRecipePaintingFrame", this.getName(), true, "")) {
            RecipePaintingFrame recipe = new RecipePaintingFrame(
                    "sss",
                    "sps",
                    "sss",
                    Character.valueOf('p'),
                    this.itemPainting,
                    Character.valueOf('s'),
                    "stickWood");
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("craftingRecipePaintingFrameAdd", this.getName(), true, "")) {
            RecipePaintingFrameAddPainting.createAllVariants().forEach(GameRegistry::addRecipe);
        }
        if (config.getBoolean("craftingRecipePaintingFrameRemove", this.getName(), true, "")) {
            RecipePaintingFrameRemovePainting recipe = new RecipePaintingFrameRemovePainting();
            GameRegistry.addRecipe(recipe);
            FMLCommonHandler.instance().bus().register(recipe);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void preInitClient(ModConfig config) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        
        this.textureCache = new PictureTextureCache();
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingItemRenderer = new ItemRendererPainting(this.textureCache);
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameItemRenderer = new ItemRendererPaintingFrame(this.textureCache);
        this.paintOnBlockRenderer = new EntityPaintOnBlockRenderer();
        
        MinecraftForgeClient.registerItemRenderer(this.itemPainting, this.paintingItemRenderer);
        MinecraftForgeClient.registerItemRenderer(this.itemPaintingFrame,
                                                  this.paintingFrameItemRenderer);
        MinecraftForge.EVENT_BUS.register(this.textureCache);
        RenderingRegistry.registerEntityRenderingHandler(EntityPaintOnBlock.class,
                                                         this.paintOnBlockRenderer);
        
        this.clientConfig = new ChangeableClientConfig().read(config);
        this.applyConfigClient();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        this.clientConfig.read(config);
        this.applyConfigClient();
    }
    
    @SideOnly(Side.CLIENT)
    protected void applyConfigClient() {
        if (this.clientConfig.perFrameBrushUse) {
            MinecraftForge.EVENT_BUS.register(this.itemPaintBrush);
            MinecraftForge.EVENT_BUS.register(this.itemPaintRemover);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this.itemPaintBrush);
            MinecraftForge.EVENT_BUS.unregister(this.itemPaintRemover);
        }
        
        this.paintingTileRenderer.renderFrameType =
                this.clientConfig.renderPaintingTilePartFrameType;
        this.paintingTileRenderer.renderPictureType =
                this.clientConfig.renderPaintingTilePartPictureType;
        if (this.clientConfig.renderPaintingTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class,
                                                         this.paintingTileRenderer);
        } else {
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityPainting.class,
                                                                             this.paintingTileRenderer);
        }
        
        this.paintingFrameTileRenderer.renderFrameType =
                this.clientConfig.renderPaintingFrameTilePartFrameType;
        this.paintingFrameTileRenderer.renderPictureType =
                this.clientConfig.renderPaintingFrameTilePartPictureType;
        if (this.clientConfig.renderPaintingFrameTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintingFrame.class,
                                                         this.paintingFrameTileRenderer);
        } else {
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityPaintingFrame.class,
                                                                             this.paintingFrameTileRenderer);
        }
        
        this.paintOnBlockRenderer.renderPictureType =
                this.clientConfig.renderPaintOnBlockPartPictureType;
        
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
    
    @Override
    public String getName() {
        return "painting";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    protected static final String[] DEFAULT_BRUSH_RADIUSES = new String[]{"1: 1.5",
                                                                          "12: 2.5",
                                                                          "16: 3.5",
                                                                          "24: 5.2",
                                                                          "32: 6.2",
                                                                          "48: 7.5",
                                                                          "64: 10.5"};
    
    protected static final String[] DEFAULT_SMALL_BRUSH_RADIUSES = new String[]{"1: 0.1"};
    
    public class ChangeableConfig {
        
        public int paintingPlaceStack = 2;
        public final IPictureSize paintingDefaultSize = new DefaultPictureSize();
        public final IPictureSize paintOnBlockDefaultSize = new DefaultPaintOnBlockSize();
        
        public final SortedMap<Integer, Double> brushRadiuses;
        public final SortedMap<Integer, Double> smallBrushRadiuses;
        
        public final SortedMap<Integer, Double> removerRadiuses;
        public final SortedMap<Integer, Double> smallRemoverRadiuses;
        
        public boolean allowPaintOnBlock = false;
        public boolean anyBlockValidForPaint = false;
        
        public boolean copyOnAnvil = true;
        public int copyOnAnvilCost = 5;
        
        public int paintingMaxCraftableWidth = 64;
        public int paintingMaxCraftableHeight = 64;
        
        protected int paintingDefaultWidth = 16;
        protected int paintingDefaultHeight = 16;
        
        protected int paintOnBlockDefaultWidth = 16;
        protected int paintOnBlockDefaultHeight = 16;
        
        protected final SortedMap<Integer, Double> iBrushRadiuses = new TreeMap<>();
        protected final SortedMap<Integer, Double> iSmallBrushRadiuses = new TreeMap<>();
        
        protected final SortedMap<Integer, Double> iRemoverRadiuses = new TreeMap<>();
        protected final SortedMap<Integer, Double> iSmallRemoverRadiuses = new TreeMap<>();
        
        protected ChangeableConfig() {
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
        }
        
        public ChangeableConfig read(ModConfig config) {
            config.restartless();
            this.paintingPlaceStack =
                    config.getInt("paintingPlaceStack", ModComponentPainting.this.getName(),
                                  2, 0, 64, "");
            this.paintingDefaultWidth =
                    config.getInt("paintingDefaultWidth", ModComponentPainting.this.getName(),
                                  16, 1, ModComponentPainting.this.MAX_WIDTH, "");
            this.paintingDefaultHeight =
                    config.getInt("paintingDefaultHeight", ModComponentPainting.this.getName(),
                                  16, 1, ModComponentPainting.this.MAX_HEIGHT,
                                  "(recommended to equals width)");
            this.allowPaintOnBlock =
                    config.getBoolean("allowPaintOnBlock", ModComponentPainting.this.getName(),
                                      false, "");
            this.paintOnBlockDefaultWidth =
                    config.getInt("paintOnBlockDefaultWidth", ModComponentPainting.this.getName(),
                                  16, 1, ModComponentPainting.this.MAX_WIDTH, "");
            this.paintOnBlockDefaultHeight =
                    config.getInt("paintOnBlockDefaultHeight", ModComponentPainting.this.getName(),
                                  16, 1, ModComponentPainting.this.MAX_HEIGHT,
                                  "(highly recommended to equals width)");
            this.anyBlockValidForPaint =
                    config.getBoolean("anyBlockValidForPaint", ModComponentPainting.this.getName(),
                                      false, "");
            this.paintingMaxCraftableWidth =
                    config.getInt("paintingMaxCraftableWidth", ModComponentPainting.this.getName(),
                                  64, 1, ModComponentPainting.this.MAX_WIDTH, "");
            this.paintingMaxCraftableHeight =
                    config.getInt("paintingMaxCraftableHeight", ModComponentPainting.this.getName(),
                                  64, 1, ModComponentPainting.this.MAX_HEIGHT, "");
            this.copyOnAnvil =
                    config.getBoolean("copyOnAnvil", ModComponentPainting.this.getName(), true, "");
            this.copyOnAnvilCost =
                    config.getInt("copyOnAnvilCost", ModComponentPainting.this.getName(), 5, 0, 40,
                                  "");
            {
                String[] lines = config.getStringList("brushRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      DEFAULT_BRUSH_RADIUSES, "");
                this.iBrushRadiuses.clear();
                parseRadiuses(lines, this.iBrushRadiuses);
            }
            {
                String[] lines = config.getStringList("smallBrushRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      DEFAULT_SMALL_BRUSH_RADIUSES, "");
                this.iSmallBrushRadiuses.clear();
                parseRadiuses(lines, this.iSmallBrushRadiuses);
            }
            {
                String[] lines = config.getStringList("removerRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      DEFAULT_BRUSH_RADIUSES, "");
                this.iRemoverRadiuses.clear();
                parseRadiuses(lines, this.iRemoverRadiuses);
            }
            {
                String[] lines = config.getStringList("smallRemoverRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      DEFAULT_SMALL_BRUSH_RADIUSES, "");
                this.iSmallRemoverRadiuses.clear();
                parseRadiuses(lines, this.iSmallRemoverRadiuses);
            }
            config.restartlessReset();
            return this;
        }
        
        protected class DefaultPictureSize implements IPictureSize {
            
            @Override
            public int getWidth() {
                return ChangeableConfig.this.paintingDefaultWidth;
            }
            
            @Override
            public int getHeight() {
                return ChangeableConfig.this.paintingDefaultHeight;
            }
        }
        
        protected class DefaultPaintOnBlockSize implements IPictureSize {
            
            @Override
            public int getWidth() {
                return ChangeableConfig.this.paintOnBlockDefaultWidth;
            }
            
            @Override
            public int getHeight() {
                return ChangeableConfig.this.paintOnBlockDefaultHeight;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public class ChangeableClientConfig {
        public boolean perFrameBrushUse = true;
        
        public boolean forceUnhidePaintRemover = false;
        
        public boolean renderPaintingTile = true;
        public boolean renderPaintingItem = true;
        public int renderPaintingTilePartFrameType = 1;
        public int renderPaintingTilePartPictureType = 2;
        public double renderPaintingTileMaxRenderDistanceSquared = Math.pow(128.0D, 2);
        public boolean renderPaintingFrameTile = true;
        public boolean renderPaintingFrameItem = true;
        public int renderPaintingFrameTilePartFrameType = 0;
        public int renderPaintingFrameTilePartPictureType = 2;
        public double renderPaintingFrameTileMaxRenderDistanceSquared = Math.pow(128.0D, 2);
        public int renderPaintOnBlockPartPictureType = 2;
        public double renderPaintOnBlockMaxRenderDistanceSquared = Math.pow(256.0D, 2);
        public boolean renderProfiling = false;
        public boolean paintingSpecialSelectionBox = true;
        public Color paintingSpecialSelectionBoxColor = null;
        public boolean paintingNoneSelectionBox = false;
        
        public ChangeableClientConfig() {}
        
        public ChangeableClientConfig read(ModConfig config) {
            config.restartless();
            this.perFrameBrushUse =
                    config.getBoolean("perFrameBrushUse", ModComponentPainting.this.getName(),
                                      true, "");
            this.forceUnhidePaintRemover =
                    config.getBoolean("forceUnhidePaintRemover",
                                      ModComponentPainting.this.getName(),
                                      false, "");
            
            this.renderPaintingTile = config.getBoolean("paintingTile", CLIENT_RENDER, true, "");
            this.renderPaintingItem = config.getBoolean("paintingItem", CLIENT_RENDER, true, "");
            this.renderPaintingTilePartFrameType =
                    config.getInt("paintingTilePartFrameType", CLIENT_RENDER, 1, -1, 2, "");
            this.renderPaintingTilePartPictureType =
                    config.getInt("paintingTilePartPictureType", CLIENT_RENDER, 2, -1, 2, "");
            this.renderPaintingTileMaxRenderDistanceSquared =
                    Math.pow(config.getFloat("renderPaintingTileMaxRenderDistance", CLIENT_RENDER,
                                             128.0F, 0.0F, 1024.0F, ""),
                             2);
            this.renderPaintingFrameTile =
                    config.getBoolean("paintingFrameTile", CLIENT_RENDER, true, "");
            this.renderPaintingFrameItem =
                    config.getBoolean("paintingFrameItem", CLIENT_RENDER, true, "");
            this.renderPaintingFrameTilePartFrameType =
                    config.getInt("paintingFrameTilePartFrameType", CLIENT_RENDER, 0, -1, 2, "");
            this.renderPaintingFrameTilePartPictureType =
                    config.getInt("paintingFrameTilePartPictureType", CLIENT_RENDER, 2, -1, 2, "");
            this.renderPaintOnBlockMaxRenderDistanceSquared =
                    Math.pow(config.getFloat("renderPaintOnBlockMaxRenderDistance", CLIENT_RENDER,
                                             256.0F, 0.0F, 1024.0F, ""),
                             2);
            this.renderPaintOnBlockPartPictureType =
                    config.getInt("paintOnBlockPartPictureType", CLIENT_RENDER, 2, -1, 2, "");
            this.renderPaintingFrameTileMaxRenderDistanceSquared =
                    Math.pow(config.getFloat("renderPaintingFrameTileMaxRenderDistance",
                                             CLIENT_RENDER,
                                             128.0F, 0.0F, 1024.0F, ""),
                             2);
            this.renderProfiling =
                    config.getBoolean("paintingRenderProfiling", CLIENT_RENDER, false, "");
            this.paintingSpecialSelectionBox =
                    config.getBoolean("paintingSpecialSelectionBox", CLIENT_RENDER, true, "");
            String paintingSpecialSelectionBoxColorString =
                    config.getString("paintingSpecialSelectionBoxColor", CLIENT_RENDER, "",
                                     "Color of selection box. Example: #00ff00");
            try {
                this.paintingSpecialSelectionBoxColor =
                        Color.decode(paintingSpecialSelectionBoxColorString);
            } catch (NumberFormatException e) {
                this.paintingSpecialSelectionBoxColor = null;
            }
            this.paintingNoneSelectionBox =
                    config.getBoolean("paintingNoneSelectionBox", CLIENT_RENDER, false, "");
            config.restartlessReset();
            return this;
        }
    }
    
    protected static void parseRadiuses(String[] lines, SortedMap<Integer, Double> radiuses) {
        for (String line : lines) {
            parseRadius(line, radiuses);
        }
    }
    
    protected static void parseRadius(String line, SortedMap<Integer, Double> radiuses) {
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
}
