package com.vanym.paniclecraft.core.component;

import java.awt.Color;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.PictureTextureCache;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPainting;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingFrameRenderer;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.network.message.MessagePaintBrushUse;
import com.vanym.paniclecraft.network.message.MessagePaletteChange;
import com.vanym.paniclecraft.recipe.RecipePaintBrushByDye;
import com.vanym.paniclecraft.recipe.RecipePaintBrushByPaintFiller;
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
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModComponentPainting implements ModComponent {
    
    public final Color DEFAULT_COLOR = new Color(200, 200, 200);
    
    public ItemPainting itemPainting;
    public ItemPaintBrush itemPaintBrush;
    public ItemPalette itemPalette;
    public BlockPainting blockPainting;
    public BlockPaintingFrame blockPaintingFrame;
    
    @SideOnly(Side.CLIENT)
    public TileEntityPaintingRenderer tilePaintingRenderer = new TileEntityPaintingRenderer();
    @SideOnly(Side.CLIENT)
    public TileEntityPaintingFrameRenderer tilePaintingFrameRenderer =
            new TileEntityPaintingFrameRenderer();
    @SideOnly(Side.CLIENT)
    public boolean specialBoundingBox = true;
    
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
        config.getInt("PaintingRow", this.getName(), 16, 1, 32, // TODO
                      "(recommend to degree 2 like 8,16,32)");
        ItemPaintBrush.brushRadiusRound =
                config.get(this.getName(), "brushRadius", 3.5D,
                           "[range: 1 ~ 2048, default: 3.5]", 1.0D, 2048.0D)
                      .getDouble(3.5D);
        ItemPainting.paintingPlaceStack =
                config.getInt("PaintingPlaceStack", this.getName(), 2, 0, 256, "");
        Core.instance.registerItem(this.itemPainting);
        Core.instance.registerItem(this.itemPaintBrush);
        Core.instance.registerItem(this.itemPalette);
        if (config.getBoolean("Crafting_PaintBrushColorByDyeChange", this.getName(), true, "")) {
            RecipePaintBrushByDye recipePaintBrushByDye = new RecipePaintBrushByDye();
            GameRegistry.addRecipe(recipePaintBrushByDye);
        }
        if (config.getBoolean("Crafting_PaintBrushColorByPaintFiller", this.getName(), true, "")) {
            RecipePaintBrushByPaintFiller recipePaintBrushByPaintFiller =
                    new RecipePaintBrushByPaintFiller();
            GameRegistry.addRecipe(recipePaintBrushByPaintFiller);
            FMLCommonHandler.instance().bus().register(recipePaintBrushByPaintFiller);
            RecipeSorter.register(DEF.MOD_ID + ":recipePaintBrushByPaintFiller",
                                  RecipePaintBrushByPaintFiller.class,
                                  RecipeSorter.Category.SHAPELESS, "");
        }
        if (config.getBoolean("Crafting_BigPaintBrush", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(this.itemPaintBrush, 1, 0),
                    new Object[]{"w", "s", Character.valueOf('w'),
                                 new ItemStack(Blocks.wool, 1, 0), Character.valueOf('s'),
                                 "stickWood"}));
        }
        if (config.getBoolean("Crafting_SmallPaintBrush", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(this.itemPaintBrush, 1, 1),
                    new Object[]{"f", "s", Character.valueOf('f'), Items.feather,
                                 Character.valueOf('s'), "stickWood"}));
        }
        if (config.getBoolean("Crafting_PaintFiller", this.getName(), true, "")) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(this.itemPaintBrush, 1, 2),
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
        GameRegistry.registerTileEntity(TileEntityPainting.class, DEF.MOD_ID + ".painting");
        this.blockPaintingFrame = new BlockPaintingFrame();
        GameRegistry.registerBlock(this.blockPaintingFrame,
                                   this.blockPaintingFrame.getUnlocalizedName().substring(5));
        GameRegistry.registerTileEntity(TileEntityPaintingFrame.class,
                                        DEF.MOD_ID + ".paintingFrame");
        int crafting_paintingFrame_amount =
                config.getInt("Crafting_PaintingFrame_Amount", this.getName(), 1, 0, 64,
                              "\'0\' to disable");
        if (crafting_paintingFrame_amount > 0) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(this.blockPaintingFrame, crafting_paintingFrame_amount),
                    new Object[]{"sss", "sws", "sss", Character.valueOf('w'), "plankWood",
                                 Character.valueOf('s'), "stickWood"}));
        }
        
        Core.instance.network.registerMessage(MessagePaintBrushUse.class,
                                              MessagePaintBrushUse.class, 30,
                                              Side.SERVER);
        Core.instance.network.registerMessage(MessagePaletteChange.class,
                                              MessagePaletteChange.class, 31,
                                              Side.SERVER);
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
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(this.blockPaintingFrame),
                                                      new ItemRendererPaintingFrame());
        }
        if (textureCache != null) {
            MinecraftForge.EVENT_BUS.register(textureCache);
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
}
