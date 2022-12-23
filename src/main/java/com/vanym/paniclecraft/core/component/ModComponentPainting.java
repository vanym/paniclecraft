package com.vanym.paniclecraft.core.component;

import java.awt.Color;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
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
        if (config.getBoolean("Crafting_PaintBrushColorByDyeChange", this.getName(), true, "")) {
            RecipeColorizeByDye recipe = new RecipeColorizeByDye();
            GameRegistry.addRecipe(recipe);
        }
        if (config.getBoolean("Crafting_PaintBrushColorByPaintFiller", this.getName(), true, "")) {
            RecipeColorizeByFiller recipe = new RecipeColorizeByFiller();
            GameRegistry.addRecipe(recipe);
            FMLCommonHandler.instance().bus().register(recipe);
        }
        if (config.getBoolean("Crafting_BigPaintBrush", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemPaintBrush.getBrush(),
                    new Object[]{"w", "s", Character.valueOf('w'),
                                 new ItemStack(Blocks.wool, 1, 0), Character.valueOf('s'),
                                 "stickWood"}));
        }
        if (config.getBoolean("Crafting_SmallPaintBrush", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemPaintBrush.getSmallBrush(),
                    new Object[]{"f", "s", Character.valueOf('f'), Items.feather,
                                 Character.valueOf('s'), "stickWood"}));
        }
        if (config.getBoolean("Crafting_PaintFiller", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    this.itemPaintBrush.getFiller(),
                    new Object[]{"w", "b", Character.valueOf('w'), "dyeWhite",
                                 Character.valueOf('b'), Items.bowl}));
        }
        int crafting_painting_amount = config.getInt("Crafting_Painting_Amount", this.getName(), 8,
                                                     0, 64, "\'0\' to disable");
        if (crafting_painting_amount > 0) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(this.itemPainting, crafting_painting_amount),
                    new Object[]{"wsw", "scs", "wsw", Character.valueOf('w'), "plankWood",
                                 Character.valueOf('s'), "stickWood", Character.valueOf('c'),
                                 Blocks.wool}));
        }
        if (config.getBoolean("Crafting_Painting_Clear", this.getName(), true, "")) {
            GameRegistry.addShapelessRecipe(new ItemStack(this.itemPainting), this.itemPainting);
        }
        if (config.getBoolean("Crafting_Palette", this.getName(), false, "")) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(
                    new ItemStack(this.itemPalette),
                    new Object[]{"slabWood", "dyeRed", "dyeGreen", "dyeBlue"}));
        }
        this.blockPainting = new BlockPainting();
        GameRegistry.registerBlock(this.blockPainting, null,
                                   this.blockPainting.getUnlocalizedName().substring(5));
        GameRegistry.registerTileEntity(TileEntityPainting.class,
                                        DEF.MOD_ID + "." + TileEntityPainting.IN_MOD_ID);
        this.blockPaintingFrame = new BlockPaintingFrame();
        GameRegistry.registerBlock(this.blockPaintingFrame, ItemPaintingFrame.class,
                                   this.blockPaintingFrame.getUnlocalizedName().substring(5));
        GameRegistry.registerTileEntity(TileEntityPaintingFrame.class,
                                        DEF.MOD_ID + "." + TileEntityPaintingFrame.IN_MOD_ID);
        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());
        if (config.getBoolean("Crafting_PaintingFrame", this.getName(), true, "")) {
            GameRegistry.addRecipe(new RecipePaintingFrame(
                    new Object[]{"sss", "sps", "sss", Character.valueOf('p'), this.itemPainting,
                                 Character.valueOf('s'), "stickWood"}));
            RecipePaintingFrameAddPainting.createAllVariants().forEach(GameRegistry::addRecipe);
            RecipePaintingFrameRemovePainting removeRecipe =
                    new RecipePaintingFrameRemovePainting();
            GameRegistry.addRecipe(removeRecipe);
            FMLCommonHandler.instance().bus().register(removeRecipe);
        }
        
        Core.instance.network.registerMessage(MessagePaintBrushUse.class,
                                              MessagePaintBrushUse.class, 30,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessagePaletteChange.class,
                                              MessagePaletteChange.class, 31,
                                              Side.SERVER);
        this.config = new ChangeableConfig().read(config);
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
