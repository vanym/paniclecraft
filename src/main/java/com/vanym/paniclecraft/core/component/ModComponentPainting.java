package com.vanym.paniclecraft.core.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.PaintingSpecialSelectionBox;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.entity.EntityPaintOnBlockRenderer;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.command.CommandMod3;
import com.vanym.paniclecraft.command.CommandPaintOnBlock;
import com.vanym.paniclecraft.command.CommandPainting;
import com.vanym.paniclecraft.core.ModConfig;
import com.vanym.paniclecraft.core.component.painting.AnvilCopyEventHandler;
import com.vanym.paniclecraft.core.component.painting.AnyBlockPaintableEventHandler;
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
import com.vanym.paniclecraft.network.message.MessageOpenPaintingView;
import com.vanym.paniclecraft.network.message.MessagePaintingToolUse;
import com.vanym.paniclecraft.network.message.MessagePaintingViewAddPicture;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.recipe.RecipeColorizeByDye;
import com.vanym.paniclecraft.recipe.RecipeColorizeByFiller;
import com.vanym.paniclecraft.recipe.RecipeDummy;
import com.vanym.paniclecraft.recipe.RecipePaintingCombine;
import com.vanym.paniclecraft.recipe.RecipePaintingFrame;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameAddPainting;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameRemovePainting;
import com.vanym.paniclecraft.recipe.RecipeRegister;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapedOreRecipe;
import com.vanym.paniclecraft.recipe.RecipeRegister.ShapelessOreRecipe;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public ItemPaintRemover itemPaintRemover;
    @ModComponentObject
    public ItemPalette itemPalette;
    @ModComponentObject
    public BlockPainting blockPainting;
    @ModComponentObject
    public BlockPaintingFrame blockPaintingFrame;
    
    protected List<IRecipe> recipes = new ArrayList<>();
    
    protected ChangeableConfig myServerConfig = new ChangeableConfig();
    public ChangeableConfig config = this.myServerConfig;
    
    public ChangeableServerConfig server = new ChangeableServerConfig();
    
    @SideOnly(Side.CLIENT)
    protected ItemPaintingTool.PerFrameEventHandler perFrameUse;
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
    protected PaintingSpecialSelectionBox paintingSpecialSelectionBox;
    @SideOnly(Side.CLIENT)
    protected EntityPaintOnBlockRenderer paintOnBlockRenderer;
    
    @SideOnly(Side.CLIENT)
    public ChangeableClientConfig clientConfig;
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(ModConfig config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        
        this.itemPaintBrush = new ItemPaintBrush();
        this.itemPaintRemover = new ItemPaintRemover();
        this.itemPalette = new ItemPalette();
        
        this.blockPainting = new BlockPainting();
        this.itemPainting = new ItemPainting(this.blockPainting);
        GameRegistry.registerTileEntity(TileEntityPainting.class, TileEntityPainting.ID);
        
        this.blockPaintingFrame = new BlockPaintingFrame();
        this.itemPaintingFrame = new ItemPaintingFrame(this.blockPaintingFrame);
        GameRegistry.registerTileEntity(TileEntityPaintingFrame.class, TileEntityPaintingFrame.ID);
        
        EntityRegistry.registerModEntity(EntityPaintOnBlock.ID, EntityPaintOnBlock.class,
                                         EntityPaintOnBlock.IN_MOD_ID, 33,
                                         Core.instance, 64, 1, true);
        
        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());
        MinecraftForge.EVENT_BUS.register(new PaintOnBlockEventHandler());
        
        Arrays.asList(new CommandPainting(), new CommandPaintOnBlock())
              .forEach(Core.instance.command::addSubCommand);
        
        Core.instance.network.registerMessage(MessagePaintingToolUse.Handler.class,
                                              MessagePaintingToolUse.class, 30,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessagePaletteSetColor.Handler.class,
                                              MessagePaletteSetColor.class, 32,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessageOpenPaintingView.Handler.class,
                                              MessageOpenPaintingView.class, 33,
                                              Side.CLIENT);
        Core.instance.network.registerMessage(MessagePaintingViewAddPicture.Handler.class,
                                              MessagePaintingViewAddPicture.class, 34,
                                              Side.SERVER);
        this.initRecipe(config);
        this.configChanged(config);
    }
    
    @Override
    public void configChanged(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.myServerConfig.read(config);
        this.server.read(config);
        this.applyConfig();
    }
    
    protected void applyConfig() {
        if (this.config.anyBlockValidForPaint) {
            MinecraftForge.EVENT_BUS.register(AnyBlockPaintableEventHandler.instance);
        } else {
            MinecraftForge.EVENT_BUS.unregister(AnyBlockPaintableEventHandler.instance);
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
                    "woolWhite",
                    Character.valueOf('s'),
                    "stickWood").name();
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintBrush", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getSmallBrush(),
                    "f",
                    "s",
                    Character.valueOf('f'),
                    "feather",
                    Character.valueOf('s'),
                    "stickWood").name();
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipePaintFiller", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getFiller(),
                    "w",
                    "b",
                    Character.valueOf('w'),
                    "dyeWhite",
                    Character.valueOf('b'),
                    Items.BOWL).name();
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipeColorPicker", this.getName(), true, "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintBrush.getColorPicker(),
                    false,
                    " b",
                    "b ",
                    Character.valueOf('b'),
                    Items.GLASS_BOTTLE).name();
            this.recipes.add(recipe);
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
            RecipeRegister.useName(recipe, "%s_stick");
            this.recipes.add(recipe);
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
            RecipeRegister.useName(recipe, "%s_brush");
            ForgeRegistries.RECIPES.register(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintRemoverFromStick", this.getName(), false,
                              "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getSmallRemover(),
                    "f",
                    "s",
                    Character.valueOf('f'),
                    Items.FLINT,
                    Character.valueOf('s'),
                    "stickWood");
            RecipeRegister.useName(recipe, "%s_stick");
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipeSmallPaintRemoverFromBrush", this.getName(), false,
                              "")) {
            ShapedOreRecipe recipe = new ShapedOreRecipe(
                    this.itemPaintRemover.getSmallRemover(),
                    "f",
                    "b",
                    Character.valueOf('f'),
                    Items.FLINT,
                    Character.valueOf('b'),
                    this.itemPaintBrush.getSmallBrush());
            RecipeRegister.useName(recipe, "%s_brush");
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipeColorizeByDye", this.getName(), true, "")) {
            RecipeColorizeByDye recipe = new RecipeColorizeByDye();
            recipe.setRegistryName(DEF.MOD_ID, "colorize_by_dye");
            this.recipes.add(recipe);
            this.recipes.addAll(RecipeDummy.getColorizeByDyeDummies());
        }
        if (config.getBoolean("craftingRecipeColorizeByFiller", this.getName(), true, "")) {
            RecipeColorizeByFiller recipe = new RecipeColorizeByFiller();
            recipe.setRegistryName(DEF.MOD_ID, "colorize_by_filler");
            this.recipes.add(recipe);
            this.recipes.addAll(RecipeDummy.getColorizeByFillerDummies());
        }
        if (config.getBoolean("craftingRecipePalette", this.getName(), false, "")) {
            ShapelessOreRecipe recipe = new ShapelessOreRecipe(
                    new ItemStack(this.itemPalette),
                    "slabWood",
                    "dyeRed",
                    "dyeGreen",
                    "dyeBlue").flow();
            this.recipes.add(recipe);
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
                    "wool").flow();
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipePaintingClear", this.getName(), true, "")) {
            ShapelessOreRecipe recipe =
                    new ShapelessOreRecipe(this.itemPainting, this.itemPainting);
            RecipeRegister.flowRegistryName(recipe, "%s_clear");
            this.recipes.add(recipe);
        }
        {
            final Pattern combinePattern =
                    Pattern.compile("^(?:(0*([2-3])x0*([2-3]))|(0*(1)x0*([2-3]))|(0*([2-3])x0*(1)))$");
            Property prop = config.get(this.getName(), "craftingRecipePaintingCombine",
                                       new String[]{"2x2"}, null, combinePattern);
            prop.setValidValues(new String[]{"2x1", "1x2", "2x2",
                                             "3x1", "3x2", "1x3", "2x3", "3x3"});
            prop.setComment("" + prop.getDefault());
            String[] paintingCombines = prop.getStringList();
            Arrays.stream(paintingCombines)
                  .map(combinePattern::matcher)
                  .filter(Matcher::matches)
                  .map(m-> {
                      int i = 7;
                      for (; i > 3 && m.group(i) == null; i -= 3) {
                      } // formatter does not understand ';' beauty :(
                      int x = Integer.parseInt(m.group(i + 1));
                      int y = Integer.parseInt(m.group(i + 2));
                      RecipePaintingCombine r = new RecipePaintingCombine(x, y);
                      r.setRegistryName(DEF.MOD_ID, String.format("painting_combine_%dx%d", x, y));
                      return r;
                  })
                  .forEach(this.recipes::add);
        }
        if (config.getBoolean("craftingRecipePaintingFrame", this.getName(), true, "")) {
            RecipePaintingFrame recipe = new RecipePaintingFrame(
                    "sss",
                    "sps",
                    "sss",
                    Character.valueOf('p'),
                    this.itemPainting,
                    Character.valueOf('s'),
                    "stickWood");
            RecipeRegister.flowRegistryName(recipe);
            this.recipes.add(recipe);
        }
        if (config.getBoolean("craftingRecipePaintingFrameAdd", this.getName(), true, "")) {
            RecipePaintingFrameAddPainting.createAllVariants()
                                          .forEach(ForgeRegistries.RECIPES::register);
        }
        if (config.getBoolean("craftingRecipePaintingFrameRemove", this.getName(), true, "")) {
            RecipePaintingFrameRemovePainting recipe = new RecipePaintingFrameRemovePainting();
            RecipeRegister.flowRegistryName(recipe, "%s_remove_painting");
            this.recipes.add(recipe);
        }
    }
    
    @Override
    public List<IRecipe> getRecipes() {
        return this.recipes;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void preInitClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.paintingSpecialSelectionBox = null;
        this.clientConfig = new ChangeableClientConfig();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void initClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.perFrameUse = new ItemPaintingTool.PerFrameEventHandler();
        this.textureCache = new PictureTextureCache();
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingItemRenderer = new ItemRendererPainting(this.textureCache);
        this.itemPainting.setTileEntityItemStackRenderer(this.paintingItemRenderer);
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameItemRenderer = new ItemRendererPaintingFrame(this.textureCache);
        this.itemPaintingFrame.setTileEntityItemStackRenderer(this.paintingFrameItemRenderer);
        this.paintOnBlockRenderer = new EntityPaintOnBlockRenderer();
        
        MinecraftForge.EVENT_BUS.register(this.textureCache);
        RenderingRegistry.registerEntityRenderingHandler(EntityPaintOnBlock.class,
                                                         this.paintOnBlockRenderer);
        
        this.clientConfig = new ChangeableClientConfig().read(config);
        this.applyConfigClient();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(ModConfig config) {
        if (!this.isEnabled()) {
            return;
        }
        this.clientConfig.read(config);
        this.applyConfigClient();
    }
    
    @SideOnly(Side.CLIENT)
    protected void applyConfigClient() {
        if (this.clientConfig.perFrameBrushUse) {
            MinecraftForge.EVENT_BUS.register(this.perFrameUse);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this.perFrameUse);
        }
        
        this.paintingTileRenderer.renderFrameType =
                this.clientConfig.renderPaintingTilePartFrameType;
        this.paintingTileRenderer.renderPictureType =
                this.clientConfig.renderPaintingTilePartPictureType;
        if (this.clientConfig.renderPaintingTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class,
                                                         this.paintingTileRenderer);
        } else {
            TileEntityRendererDispatcher.instance.renderers.remove(TileEntityPainting.class,
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
            TileEntityRendererDispatcher.instance.renderers.remove(TileEntityPaintingFrame.class,
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
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors()
             .registerItemColorHandler(this.itemPaintBrush.color(), this.itemPaintBrush);
    }
    
    @Override
    public String getName() {
        return "painting";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public void setServerSideConfig(IServerSideConfig config) {
        this.config = (ChangeableConfig)config;
    }
    
    @Override
    public IServerSideConfig getServerSideConfig() {
        return this.myServerConfig;
    }
    
    protected static final String[] DEFAULT_BRUSH_RADIUSES = new String[]{"1: 1.5",
                                                                          "12: 2.5",
                                                                          "16: 3.5",
                                                                          "24: 5.2",
                                                                          "32: 6.2",
                                                                          "48: 7.5",
                                                                          "64: 10.5"};
    
    protected static final String[] DEFAULT_SMALL_BRUSH_RADIUSES = new String[]{"1: 0.1"};
    
    public class ChangeableConfig implements IServerSideConfig {
        
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
        
        protected ChangeableConfig(ChangeableConfig config) {
            this();
            this.paintingPlaceStack = config.paintingPlaceStack;
            this.allowPaintOnBlock = config.allowPaintOnBlock;
            this.anyBlockValidForPaint = config.anyBlockValidForPaint;
            this.copyOnAnvil = config.copyOnAnvil;
            this.copyOnAnvilCost = config.copyOnAnvilCost;
            this.paintingMaxCraftableWidth = config.paintingMaxCraftableWidth;
            this.paintingMaxCraftableHeight = config.paintingMaxCraftableHeight;
            this.paintingDefaultWidth = config.paintingDefaultWidth;
            this.paintingDefaultHeight = config.paintingDefaultHeight;
            this.paintOnBlockDefaultWidth = config.paintOnBlockDefaultWidth;
            this.paintOnBlockDefaultHeight = config.paintOnBlockDefaultHeight;
            
            this.iBrushRadiuses.clear();
            this.iBrushRadiuses.putAll(config.iBrushRadiuses);
            this.iSmallBrushRadiuses.clear();
            this.iSmallBrushRadiuses.putAll(config.iSmallBrushRadiuses);
            
            this.iRemoverRadiuses.clear();
            this.iRemoverRadiuses.putAll(config.iRemoverRadiuses);
            this.iSmallRemoverRadiuses.clear();
            this.iSmallRemoverRadiuses.putAll(config.iSmallRemoverRadiuses);
        }
        
        public ChangeableConfig read(ModConfig config) {
            final String category = ModComponentPainting.this.getName();
            config.restartless();
            this.paintingPlaceStack = config.getInt("paintingPlaceStack", category, 2, 0, 64, "");
            this.paintingDefaultWidth =
                    config.getInt("paintingDefaultWidth", category, 16, 1,
                                  ModComponentPainting.this.MAX_WIDTH, "");
            this.paintingDefaultHeight =
                    config.getInt("paintingDefaultHeight", category, 16, 1,
                                  ModComponentPainting.this.MAX_HEIGHT,
                                  "(recommended to equals width)");
            this.allowPaintOnBlock = config.getBoolean("allowPaintOnBlock", category, false, "");
            this.paintOnBlockDefaultWidth =
                    config.getInt("paintOnBlockDefaultWidth", category, 16, 1,
                                  ModComponentPainting.this.MAX_WIDTH, "");
            this.paintOnBlockDefaultHeight =
                    config.getInt("paintOnBlockDefaultHeight", category, 16, 1,
                                  ModComponentPainting.this.MAX_HEIGHT,
                                  "(highly recommended to equals width)");
            this.anyBlockValidForPaint =
                    config.getBoolean("anyBlockValidForPaint", category, false, "");
            this.paintingMaxCraftableWidth =
                    config.getInt("paintingMaxCraftableWidth", category, 64, 1,
                                  ModComponentPainting.this.MAX_WIDTH, "");
            this.paintingMaxCraftableHeight =
                    config.getInt("paintingMaxCraftableHeight", category, 64, 1,
                                  ModComponentPainting.this.MAX_HEIGHT, "");
            this.copyOnAnvil = config.getBoolean("copyOnAnvil", category, true, "");
            this.copyOnAnvilCost = config.getInt("copyOnAnvilCost", category, 5, 0, 40, "");
            
            final String RADIUSES_COMMENT = "radius depending on picture size\n";
            
            this.iBrushRadiuses.clear();
            this.iBrushRadiuses.putAll(getRadiuses(config, "brushRadiuses", category,
                                                   DEFAULT_BRUSH_RADIUSES, RADIUSES_COMMENT));
            this.iSmallBrushRadiuses.clear();
            this.iSmallBrushRadiuses.putAll(getRadiuses(config, "smallBrushRadiuses", category,
                                                        DEFAULT_SMALL_BRUSH_RADIUSES,
                                                        RADIUSES_COMMENT));
            this.iRemoverRadiuses.clear();
            this.iRemoverRadiuses.putAll(getRadiuses(config, "removerRadiuses", category,
                                                     DEFAULT_BRUSH_RADIUSES, RADIUSES_COMMENT));
            this.iSmallRemoverRadiuses.clear();
            this.iSmallRemoverRadiuses.putAll(getRadiuses(config, "smallRemoverRadiuses", category,
                                                          DEFAULT_SMALL_BRUSH_RADIUSES,
                                                          RADIUSES_COMMENT));
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
        
        @Override
        public void fromBytes(ByteBuf buf) {
            this.paintingPlaceStack = buf.readInt();
            this.allowPaintOnBlock = buf.readBoolean();
            this.anyBlockValidForPaint = buf.readBoolean();
            this.copyOnAnvil = buf.readBoolean();
            this.copyOnAnvilCost = buf.readInt();
            this.paintingMaxCraftableWidth = buf.readInt();
            this.paintingMaxCraftableHeight = buf.readInt();
            this.paintingDefaultWidth = buf.readInt();
            this.paintingDefaultHeight = buf.readInt();
            this.paintOnBlockDefaultWidth = buf.readInt();
            this.paintOnBlockDefaultHeight = buf.readInt();
            readMap(buf, this.iBrushRadiuses);
            readMap(buf, this.iSmallBrushRadiuses);
            readMap(buf, this.iRemoverRadiuses);
            readMap(buf, this.iSmallRemoverRadiuses);
        }
        
        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.paintingPlaceStack);
            buf.writeBoolean(this.allowPaintOnBlock);
            buf.writeBoolean(this.anyBlockValidForPaint);
            buf.writeBoolean(this.copyOnAnvil);
            buf.writeInt(this.copyOnAnvilCost);
            buf.writeInt(this.paintingMaxCraftableWidth);
            buf.writeInt(this.paintingMaxCraftableHeight);
            buf.writeInt(this.paintingDefaultWidth);
            buf.writeInt(this.paintingDefaultHeight);
            buf.writeInt(this.paintOnBlockDefaultWidth);
            buf.writeInt(this.paintOnBlockDefaultHeight);
            writeMap(buf, this.iBrushRadiuses);
            writeMap(buf, this.iSmallBrushRadiuses);
            writeMap(buf, this.iRemoverRadiuses);
            writeMap(buf, this.iSmallRemoverRadiuses);
        }
        
        @Override
        public IServerSideConfig copy() {
            return new ChangeableConfig(this);
        }
    }
    
    public class ChangeableServerConfig {
        
        public boolean allowPaintingView = true;
        public boolean allowPaintingEditView = false;
        public boolean allowPaintingViewTo = false;
        public boolean allowPaintingEditViewTo = false;
        public boolean allowPaintOnBlockView = true;
        public boolean allowPaintOnBlockEditView = false;
        public boolean allowPaintOnBlockViewTo = false;
        public boolean allowPaintOnBlockEditViewTo = false;
        
        protected ChangeableServerConfig() {}
        
        public ChangeableServerConfig read(ModConfig config) {
            final String category = ModComponentPainting.this.getName();
            config.restartless();
            final String COMMENT_PREFIX =
                    String.format("allow any player use command:\n/%s ", CommandMod3.NAME);
            this.allowPaintingView =
                    config.getBoolean("allowPaintingView", category, true,
                                      COMMENT_PREFIX + "painting view" + "\n");
            this.allowPaintingEditView =
                    config.getBoolean("allowPaintingEditView", category, false,
                                      COMMENT_PREFIX + "painting editview" + "\n");
            this.allowPaintingViewTo =
                    config.getBoolean("allowPaintingViewTo", category, false,
                                      COMMENT_PREFIX + "painting viewto" + "\n");
            this.allowPaintingEditViewTo =
                    config.getBoolean("allowPaintingEditViewTo", category, false,
                                      COMMENT_PREFIX + "painting editviewto" + "\n");
            this.allowPaintOnBlockView =
                    config.getBoolean("allowPaintOnBlockView", category, true,
                                      COMMENT_PREFIX + "paintonblock view" + "\n");
            this.allowPaintOnBlockEditView =
                    config.getBoolean("allowPaintOnBlockEditView", category, false,
                                      COMMENT_PREFIX + "paintonblock editview" + "\n");
            this.allowPaintOnBlockViewTo =
                    config.getBoolean("allowPaintOnBlockViewTo", category, false,
                                      COMMENT_PREFIX + "paintonblock viewto" + "\n");
            this.allowPaintOnBlockEditViewTo =
                    config.getBoolean("allowPaintOnBlockEditViewTo", category, false,
                                      COMMENT_PREFIX + "paintonblock editviewto" + "\n");
            config.restartlessReset();
            return this;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public class ChangeableClientConfig {
        public boolean perFrameBrushUse = true;
        
        public boolean forceUnhidePaintRemover = false;
        
        public boolean paintingFrameInfoSideLetters = false;
        
        public boolean renderPaintingTile = true;
        public int renderPaintingTilePartFrameType = 1;
        public int renderPaintingTilePartPictureType = 2;
        public double renderPaintingTileMaxRenderDistanceSquared = Math.pow(128.0D, 2);
        public boolean renderPaintingFrameTile = true;
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
            final String category = ModComponentPainting.this.getName();
            this.perFrameBrushUse = config.getBoolean("perFrameBrushUse", category, true, "");
            this.forceUnhidePaintRemover =
                    config.getBoolean("forceUnhidePaintRemover", category, false,
                                      "show paint remover in creative tab even\n if paint on block is not allowed");
            this.paintingFrameInfoSideLetters =
                    config.getBoolean("paintingFrameInfoSideLetters", category, false, "");
            
            final String PART_RENDER_TYPE = String.join("\n", "render type of specific part",
                                                        "-1: disable", "0: smooth lighting off",
                                                        "1: smooth lighting minimum",
                                                        "2: smooth lighting maximum", "");
            
            this.renderPaintingTile = config.getBoolean("paintingTile", CLIENT_RENDER, true, "");
            this.renderPaintingTilePartFrameType =
                    config.getInt("paintingTilePartFrameType", CLIENT_RENDER,
                                  1, -1, 2, PART_RENDER_TYPE);
            this.renderPaintingTilePartPictureType =
                    config.getInt("paintingTilePartPictureType", CLIENT_RENDER,
                                  2, -1, 2, PART_RENDER_TYPE);
            this.renderPaintingTileMaxRenderDistanceSquared =
                    Math.pow(config.getFloat("renderPaintingTileMaxRenderDistance", CLIENT_RENDER,
                                             128.0F, 0.0F, 1024.0F, ""),
                             2);
            this.renderPaintingFrameTile =
                    config.getBoolean("paintingFrameTile", CLIENT_RENDER, true, "");
            this.renderPaintingFrameTilePartFrameType =
                    config.getInt("paintingFrameTilePartFrameType", CLIENT_RENDER,
                                  0, -1, 2, PART_RENDER_TYPE);
            this.renderPaintingFrameTilePartPictureType =
                    config.getInt("paintingFrameTilePartPictureType", CLIENT_RENDER,
                                  2, -1, 2, PART_RENDER_TYPE);
            this.renderPaintOnBlockMaxRenderDistanceSquared =
                    Math.pow(config.getFloat("renderPaintOnBlockMaxRenderDistance", CLIENT_RENDER,
                                             256.0F, 0.0F, 1024.0F, ""),
                             2);
            this.renderPaintOnBlockPartPictureType =
                    config.getInt("paintOnBlockPartPictureType", CLIENT_RENDER,
                                  2, -1, 2, PART_RENDER_TYPE);
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
    
    protected static final Pattern RADIUS_LINE =
            Pattern.compile("^\\d+: *(?:(?:\\d+(?:\\.\\d*)?)|(?:\\.\\d+))[dDfF]?$");
    
    protected static SortedMap<Integer, Double> getRadiuses(
            ModConfig config,
            String name,
            String category,
            String[] defaultValues,
            String comment) {
        Property prop = config.get(category, name, defaultValues, null, RADIUS_LINE);
        prop.setComment(comment + " [default: " + prop.getDefault() + "]");
        SortedMap<Integer, Double> map = new TreeMap<>();
        parseRadiuses(prop.getStringList(), map);
        return map;
    }
    
    protected static void parseRadiuses(String[] lines, Map<Integer, Double> radiuses) {
        radiuses.clear();
        for (String line : lines) {
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
    
    protected static void readMap(ByteBuf buf, Map<Integer, Double> map) {
        map.clear();
        IntStream.range(0, buf.readInt()).forEach(i-> {
            int size = buf.readInt();
            double radius = buf.readDouble();
            map.put(size, radius);
        });
    }
    
    protected static void writeMap(ByteBuf buf, Map<Integer, Double> map) {
        buf.writeInt(map.size());
        map.forEach((s, r)-> {
            buf.writeInt(s);
            buf.writeDouble(r);
        });
    }
}
