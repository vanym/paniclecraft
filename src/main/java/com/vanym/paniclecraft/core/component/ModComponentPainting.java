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
import com.vanym.paniclecraft.network.message.MessagePaletteChange;
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
    public TileEntityPaintingRenderer tilePaintingRenderer = new TileEntityPaintingRenderer();
    @SideOnly(Side.CLIENT)
    public TileEntityPaintingFrameRenderer tilePaintingFrameRenderer =
            new TileEntityPaintingFrameRenderer();
    @SideOnly(Side.CLIENT)
    public boolean specialBoundingBox = true;
    @SideOnly(Side.CLIENT)
    public boolean renderProfiling = false;
    
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
        Core.instance.network.registerMessage(MessagePaletteChange.class,
                                              MessagePaletteChange.class, 31,
                                              Side.SERVER);
        this.initRecipe(config);
        this.config = new ChangeableConfig().read(config);
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
        
        boolean perFrameBrushUse = config.getBoolean("perFrameBrushUse", this.getName(), true, "");
        if (perFrameBrushUse && this.itemPaintBrush != null) {
            MinecraftForge.EVENT_BUS.register(this.itemPaintBrush);
        }
        
        PictureTextureCache textureCache = null;
        boolean paintingTile = config.getBoolean("paintingTile", CLIENT_RENDER, true, "");
        if (paintingTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class,
                                                         this.tilePaintingRenderer);
        }
        boolean paintingItem = config.getBoolean("paintingItem", CLIENT_RENDER, true, "");
        if (paintingItem) {
            if (textureCache == null) {
                textureCache = new PictureTextureCache();
            }
            ItemRendererPainting paintingItemRenderer = new ItemRendererPainting(textureCache);
            MinecraftForgeClient.registerItemRenderer(this.itemPainting, paintingItemRenderer);
        }
        boolean paintingFrameTile = config.getBoolean("paintingFrameTile", CLIENT_RENDER, true, "");
        if (paintingFrameTile) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintingFrame.class,
                                                         this.tilePaintingFrameRenderer);
        }
        boolean paintingFrameItem = config.getBoolean("paintingFrameItem", CLIENT_RENDER, true, "");
        if (paintingFrameItem) {
            if (textureCache == null) {
                textureCache = new PictureTextureCache();
            }
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(this.blockPaintingFrame),
                                                      new ItemRendererPaintingFrame(textureCache));
        }
        this.renderProfiling =
                config.getBoolean("paintingRenderProfiling", CLIENT_RENDER, false, "");
        if (textureCache != null) {
            MinecraftForge.EVENT_BUS.register(textureCache);
        }
        boolean paintingSpecialSelectionBox =
                config.getBoolean("paintingSpecialSelectionBox", CLIENT_RENDER, true, "");
        String paintingSpecialSelectionBoxColorString =
                config.getString("paintingSpecialSelectionBoxColor", CLIENT_RENDER, "",
                                 "Color of selection box. Example: #ff0000");
        Color paintingSpecialSelectionBoxColor;
        try {
            paintingSpecialSelectionBoxColor = Color.decode(paintingSpecialSelectionBoxColorString);
        } catch (NumberFormatException e) {
            paintingSpecialSelectionBoxColor = null;
        }
        boolean paintingNoneSelectionBox =
                config.getBoolean("paintingNoneSelectionBox", CLIENT_RENDER, false, "");
        if (paintingSpecialSelectionBox || paintingNoneSelectionBox) {
            MinecraftForge.EVENT_BUS.register(new PaintingSpecialSelectionBox(
                    paintingNoneSelectionBox,
                    paintingSpecialSelectionBoxColor));
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
