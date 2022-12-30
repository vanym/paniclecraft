package com.vanym.paniclecraft.core.component;

import java.awt.Color;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.PaintingSpecialSelectionBox;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.WorldUnloadEventHandler;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPaintingFrame;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.recipe.RecipeColorizeByDye;
import com.vanym.paniclecraft.recipe.RecipeColorizeByFiller;
import com.vanym.paniclecraft.recipe.RecipeDummy;
import com.vanym.paniclecraft.recipe.RecipePaintingFrame;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameAddPainting;
import com.vanym.paniclecraft.recipe.RecipePaintingFrameRemovePainting;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
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
    
    public final Color DEFAULT_COLOR = new Color(200, 200, 200);
    
    public ItemPainting itemPainting;
    public ItemPaintBrush itemPaintBrush;
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
    public ChangeableClientConfig clientConfig = new ChangeableClientConfig();
    
    protected boolean enabled = false;
    
    @Override
    public void preInit(Configuration config) {
        if (!config.getBoolean(ENABLE_FLAG, this.getName(), true, "")) {
            return;
        }
        this.enabled = true;
        
        this.itemPainting = new ItemPainting();
        this.itemPaintBrush = new ItemPaintBrush();
        this.itemPalette = new ItemPalette();
        Core.instance.registerItem(this.itemPainting);
        Core.instance.registerItem(this.itemPaintBrush);
        Core.instance.registerItem(this.itemPalette);
        
        this.blockPainting = new BlockPainting();
        GameRegistry.registerBlock(this.blockPainting, null, this.blockPainting.getName());
        GameRegistry.registerTileEntity(TileEntityPainting.class, TileEntityPainting.ID);
        
        this.blockPaintingFrame = new BlockPaintingFrame();
        GameRegistry.registerBlock(this.blockPaintingFrame, ItemPaintingFrame.class,
                                   this.blockPaintingFrame.getName());
        GameRegistry.registerTileEntity(TileEntityPaintingFrame.class, TileEntityPaintingFrame.ID);
        
        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());
        
        Core.instance.network.registerMessage(MessagePaintBrushUse.class,
                                              MessagePaintBrushUse.class, 30,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessagePaletteSetColor.class,
                                              MessagePaletteSetColor.class, 32,
                                              Side.SERVER);
        this.initRecipe(config);
        this.config = new ChangeableConfig().read(config);
    }
    
    @Override
    public void configChanged(Configuration config) {
        this.config.read(config);
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
    public void preInitClient(Configuration config) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initClient(Configuration config) {
        if (!this.isEnabled()) {
            return;
        }
        
        this.textureCache = new PictureTextureCache();
        this.paintingTileRenderer = new TileEntityPaintingRenderer();
        this.paintingItemRenderer =
                new ItemRendererPainting(this.textureCache, this.paintingTileRenderer);
        this.paintingFrameTileRenderer = new TileEntityPaintingFrameRenderer();
        this.paintingFrameItemRenderer =
                new ItemRendererPaintingFrame(this.textureCache, this.paintingFrameTileRenderer);
        
        MinecraftForgeClient.registerItemRenderer(this.itemPainting, this.paintingItemRenderer);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(this.blockPaintingFrame),
                                                  this.paintingFrameItemRenderer);
        MinecraftForge.EVENT_BUS.register(this.textureCache);
        
        this.clientConfig = new ChangeableClientConfig().read(config);
        this.applyConfigClient();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void configChangedClient(Configuration config) {
        this.clientConfig.read(config);
        this.applyConfigClient();
    }
    
    @SideOnly(Side.CLIENT)
    protected void applyConfigClient() {
        if (this.clientConfig.perFrameBrushUse) {
            MinecraftForge.EVENT_BUS.register(this.itemPaintBrush);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this.itemPaintBrush);
        }
        
        if (this.clientConfig.renderPaintingTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class,
                                                         this.paintingTileRenderer);
        } else {
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityPainting.class,
                                                                             this.paintingTileRenderer);
        }
        
        if (this.clientConfig.renderPaintingFrameTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintingFrame.class,
                                                         this.paintingFrameTileRenderer);
        } else {
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(TileEntityPaintingFrame.class,
                                                                             this.paintingFrameTileRenderer);
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
    
    @Override
    public String getName() {
        return "painting";
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public class ChangeableConfig {
        public int paintingPlaceStack = 2;
        public int paintingDefaultWidth = 16;
        public int paintingDefaultHeight = 16;
        
        public SortedMap<Integer, Double> brushRadiuses = new TreeMap<>();
        public SortedMap<Integer, Double> smallBrushRadiuses = new TreeMap<>();
        
        protected ChangeableConfig() {
            this.brushRadiuses.put(16, 3.5D);
            this.brushRadiuses.put(32, 6.2D);
            
            this.smallBrushRadiuses.put(0, 0.1D);
        }
        
        public ChangeableConfig read(Configuration config) {
            this.paintingPlaceStack =
                    config.getInt("paintingPlaceStack", ModComponentPainting.this.getName(),
                                  2, 0, 64, "");
            this.paintingDefaultWidth =
                    config.getInt("paintingDefaultWidth", ModComponentPainting.this.getName(),
                                  16, 1, 256, "");
            this.paintingDefaultHeight =
                    config.getInt("paintingDefaultHeight", ModComponentPainting.this.getName(),
                                  16, 1, 256, "(recommend to equals width)");
            {
                String[] lines = config.getStringList("brushRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      new String[]{"1: 1.5",
                                                                   "12: 2.5",
                                                                   "16: 3.5",
                                                                   "24: 5.2",
                                                                   "32: 6.2",
                                                                   "48: 7.5",
                                                                   "64: 10.5"},
                                                      "");
                this.brushRadiuses.clear();
                parseRadiuses(lines, this.brushRadiuses);
            }
            {
                String[] lines = config.getStringList("smallBrushRadiuses",
                                                      ModComponentPainting.this.getName(),
                                                      new String[]{"1: 0.1"}, "");
                this.smallBrushRadiuses.clear();
                parseRadiuses(lines, this.smallBrushRadiuses);
            }
            return this;
        }
        
        public double getBrushRadius(int row) {
            return getRadius(row, this.brushRadiuses);
        }
        
        public double getSmallBrushRadius(int row) {
            return getRadius(row, this.smallBrushRadiuses);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public class ChangeableClientConfig {
        public boolean perFrameBrushUse = true;
        
        public boolean renderPaintingTile = true;
        public boolean renderPaintingItem = true;
        public boolean renderPaintingFrameTile = true;
        public boolean renderPaintingFrameItem = true;
        public boolean renderProfiling = false;
        public boolean paintingSpecialSelectionBox = true;
        public Color paintingSpecialSelectionBoxColor = null;
        public boolean paintingNoneSelectionBox = false;
        
        public ChangeableClientConfig() {}
        
        public ChangeableClientConfig read(Configuration config) {
            this.perFrameBrushUse =
                    config.getBoolean("perFrameBrushUse", ModComponentPainting.this.getName(),
                                      true, "");
            
            this.renderPaintingTile = config.getBoolean("paintingTile", CLIENT_RENDER, true, "");
            this.renderPaintingItem = config.getBoolean("paintingItem", CLIENT_RENDER, true, "");
            this.renderPaintingFrameTile =
                    config.getBoolean("paintingFrameTile", CLIENT_RENDER, true, "");
            this.renderPaintingFrameItem =
                    config.getBoolean("paintingFrameItem", CLIENT_RENDER, true, "");
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
            return this;
        }
    }
    
    protected static double getRadius(int row, SortedMap<Integer, Double> radiuses) {
        try {
            int key = radiuses.headMap(row + 1).lastKey();
            return radiuses.get(key);
        } catch (NoSuchElementException e) {
            return 0.0D;
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
