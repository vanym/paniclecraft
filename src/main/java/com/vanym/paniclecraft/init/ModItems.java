package com.vanym.paniclecraft.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockAdvSign;
import com.vanym.paniclecraft.block.BlockCannon;
import com.vanym.paniclecraft.block.BlockChessDesk;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.CreativeTabMod3;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.item.ItemBroom;
import com.vanym.paniclecraft.item.ItemCannon;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.item.ItemMod3;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.item.ItemWorkbench;
import com.vanym.paniclecraft.recipe.RecipePaintBrushByDye;
import com.vanym.paniclecraft.recipe.RecipePaintBrushByPaintFiller;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.Painting;

public class ModItems{
	public static CreativeTabMod3 tab;
	
	public static ItemBroom itemBroom;
	
	public static ItemAdvSign itemAdvSign;
	public static BlockAdvSign blockAdvSignPost;
	public static BlockAdvSign blockAdvSignWall;
	
	public static ItemPainting itemPainting;
	public static ItemPaintBrush itemPaintBrush;
	public static ItemPalette itemPalette;
	public static BlockPainting blockPainting;
	public static BlockPaintingFrame blockPaintingFrame;
	
	public static BlockChessDesk blockChessDesk;
	public static ItemChessDesk itemChessDesk;
	
	public static BlockCannon blockCannon;
	public static ItemCannon itemCannon;
	
	public static ItemWorkbench itemWorkbench;
	
	public static void init(Configuration config){
		if(config.getBoolean("CreativeTab", "General", true, "")){
			tab = new CreativeTabMod3(DEF.MOD_ID);
		}
		if(config.getBoolean("Enable", "Broom", true, "")){
			itemBroom = new ItemBroom(config.getInt("MaxDamage", "Broom", 3072, 0, Integer.MAX_VALUE, "0 is infinite"), config.get("Broom", "Radius", 6.0D).getDouble(6.0D));
			if(config.getBoolean("Crafting_Broom", "Broom", true, ""))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBroom, 1), new Object[]{"001", "120", "110", Character.valueOf('1'), "stickWood", Character.valueOf('2'), Items.string}));
			registerItem(itemBroom);
		}
		
		if(config.getBoolean("Enable", "AdvSign", true, "")){
			blockAdvSignPost = new BlockAdvSign(true);
			blockAdvSignWall = new BlockAdvSign(false);
			itemAdvSign = new ItemAdvSign(blockAdvSignPost, blockAdvSignWall);
			GameRegistry.registerBlock(blockAdvSignPost, null, blockAdvSignPost.getUnlocalizedName().substring(5) + ".post");
			GameRegistry.registerBlock(blockAdvSignWall, null, blockAdvSignWall.getUnlocalizedName().substring(5) + ".wall");
			registerItem(itemAdvSign);
			GameRegistry.registerTileEntity(TileEntityAdvSign.class, DEF.MOD_ID + ".advSign");
			if(config.getBoolean("Crafting_AdvSign_Easy", "AdvSign", true, ""))
				GameRegistry.addShapelessRecipe(new ItemStack(itemAdvSign, 1), new Object[]{Items.sign});
			if(config.getBoolean("Crafting_AdvSign_Hard", "AdvSign", false, ""))
				GameRegistry.addShapelessRecipe(new ItemStack(itemAdvSign, 1), new Object[]{Items.sign, Items.book});
			if(config.getBoolean("Crafting_AdvSign_Clear", "AdvSign", true, ""))
				GameRegistry.addShapelessRecipe(new ItemStack(itemAdvSign, 1), new Object[]{itemAdvSign});
		}
		
		if(config.getBoolean("Enable", "Painting", true, "")){
			itemPainting = new ItemPainting();
			itemPaintBrush = new ItemPaintBrush();
			itemPalette = new ItemPalette();
			ItemPaintBrush.setNoDrawPixels(config.get("Painting", "BrushNoDrawPixels", "", "|x|;|y|,|x|;|y|,|x|;|y|... example:\"3;3,2;3,3;2\"").getString());
			Painting.defPaintRow = config.getInt("PaintingRow", "Painting", 16, 1, 32, "(recommend to degree 2 like 8,16,32)");
			ItemPaintBrush.brushRadiusSquare = config.getInt("BrushRadiusSquare", "Painting", 3, 1, 1024, "");
			ItemPaintBrush.brushRadiusRound = config.get("Painting", "BrushRadiusRound", 3.5D, "[range: 1 ~ 2048, default: 3.5]", 1.0D, 2048.0D).getDouble(3.5D);
			ItemPainting.paintingPlaceStack = config.getInt("PaintingPlaceStack", "Painting", 2, 0, 256, "");
			Painting.pngPaintingSave = config.getBoolean("PaintingPngSave", "Painting", true, "");
			registerItem(itemPainting);
			registerItem(itemPaintBrush);
			registerItem(itemPalette);
			if(config.getBoolean("Crafting_PaintBrushColorByDyeChange", "Painting", true, "")){
				RecipePaintBrushByDye recipePaintBrushByDye = new RecipePaintBrushByDye();
				GameRegistry.addRecipe(recipePaintBrushByDye);
			}
			if(config.getBoolean("Crafting_PaintBrushColorByPaintFiller", "Painting", true, "")){
				RecipePaintBrushByPaintFiller recipePaintBrushByPaintFiller = new RecipePaintBrushByPaintFiller();
				GameRegistry.addRecipe(recipePaintBrushByPaintFiller);
				FMLCommonHandler.instance().bus().register(recipePaintBrushByPaintFiller);
				RecipeSorter.register(DEF.MOD_ID + ":recipePaintBrushByPaintFiller", RecipePaintBrushByPaintFiller.class, RecipeSorter.Category.SHAPELESS, "");
			}
			if(config.getBoolean("Crafting_BigPaintBrush", "Painting", true, ""))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintBrush, 1, 0), new Object[]{"w", "s", Character.valueOf('w'), new ItemStack(Blocks.wool, 1, 0), Character.valueOf('s'), "stickWood"}));
			if(config.getBoolean("Crafting_SmallPaintBrush", "Painting", true, ""))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintBrush, 1, 1), new Object[]{"f", "s", Character.valueOf('f'), Items.feather, Character.valueOf('s'), "stickWood"}));
			if(config.getBoolean("Crafting_PaintFiller", "Painting", true, ""))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintBrush, 1, 2), new Object[]{"w", "b", Character.valueOf('w'), "dyeWhite", Character.valueOf('b'), Items.bowl}));
			int crafting_painting_amount = config.getInt("Crafting_Painting_Amount", "Painting", 8, 0, 64, "\'0\' to disable");
			if(crafting_painting_amount > 0)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPainting, crafting_painting_amount), new Object[]{"wsw", "scs", "wsw", Character.valueOf('w'), "plankWood", Character.valueOf('s'), "stickWood", Character.valueOf('c'), Blocks.wool}));
			if(config.getBoolean("Crafting_Painting_Clear", "Painting", true, ""))
				GameRegistry.addShapelessRecipe(new ItemStack(itemPainting), itemPainting);
			if(config.getBoolean("Crafting_Palette", "Painting", false, ""))
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPalette), new Object[]{"slabWood", "dyeRed", "dyeGreen", "dyeBlue"}));
			blockPainting = new BlockPainting();
			GameRegistry.registerBlock(blockPainting, null, blockPainting.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityPainting.class, DEF.MOD_ID + ".painting");
			blockPaintingFrame = new BlockPaintingFrame();
			GameRegistry.registerBlock(blockPaintingFrame, blockPaintingFrame.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityPaintingFrame.class, DEF.MOD_ID + ".paintingFrame");
			int crafting_paintingFrame_amount = config.getInt("Crafting_PaintingFrame_Amount", "Painting", 1, 0, 64, "\'0\' to disable");
			if(crafting_paintingFrame_amount > 0)
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockPaintingFrame, crafting_paintingFrame_amount), new Object[]{"sss", "sws", "sss", Character.valueOf('w'), "plankWood", Character.valueOf('s'), "stickWood"}));
		}
		
		if(config.getBoolean("Enable", "Chess", true, "")){
			itemChessDesk = new ItemChessDesk();
			blockChessDesk = new BlockChessDesk();
			registerItem(itemChessDesk);
			GameRegistry.registerBlock(blockChessDesk, null, blockChessDesk.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityChessDesk.class, DEF.MOD_ID + ".chessDesk");
			if(config.getBoolean("Crafting_ChessDesk", "Chess", true, "")){
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemChessDesk, 1), new Object[]{"w b", "ppp", Character.valueOf('w'), new ItemStack(Blocks.wool, 1, 0), Character.valueOf('b'), new ItemStack(Blocks.wool, 1, 15), Character.valueOf('p'), "plankWood"}));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemChessDesk, 1), new Object[]{"w b", "ppp", Character.valueOf('w'), "blockWoolWhite", Character.valueOf('b'), "blockWoolBlack", Character.valueOf('p'), "plankWood"}));
			}
			if(config.getBoolean("Crafting_ChessDesk_Clear", "Chess", true, "")){
				GameRegistry.addShapelessRecipe(new ItemStack(itemChessDesk, 1), new ItemStack(itemChessDesk, 1));
			}
		}
		
		if(config.getBoolean("Enable", "Cannon", true, "")){
			blockCannon = new BlockCannon();
			itemCannon = new ItemCannon();
			GameRegistry.registerBlock(blockCannon, null, blockCannon.getUnlocalizedName().substring(5));
			registerItem(itemCannon);
			GameRegistry.registerTileEntity(TileEntityCannon.class, DEF.MOD_ID + ".cannon");
			if(config.getBoolean("Crafting_Cannon", "Cannon", true, "")){
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCannon, 1), new Object[]{"i  ", " i ", "idi", Character.valueOf('i'), "ingotIron", Character.valueOf('d'), Blocks.dispenser}));
			}
		}
		
		if(config.getBoolean("Enable", "PortableWorkbench", true, "")){
			itemWorkbench = new ItemWorkbench(config.getInt("MaxDamage", "PortableWorkbench", 8192, 0, Integer.MAX_VALUE, "0 is infinite"));
			registerItem(itemWorkbench);
			if(config.getBoolean("Crafting_PortableWorkbench", "PortableWorkbench", true, "")){
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemWorkbench, 1), new Object[]{"craftingTableWood", "stickWood", "stickWood"}));
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemWorkbench, 1), new Object[]{Blocks.crafting_table, "stickWood", "stickWood"}));
			}
		}
	}
	
	public static void registerItem(ItemMod3 item){
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		if(tab != null){
			item.setCreativeTab(tab);
			if(tab.iconitem == null)
				tab.iconitem = item;
		}
	}
}
