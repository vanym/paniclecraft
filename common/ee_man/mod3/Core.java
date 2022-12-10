package ee_man.mod3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import ee_man.mod3.blocks.*;
import ee_man.mod3.client.CreativeTab;
import ee_man.mod3.client.TickHandlerClient;
import ee_man.mod3.core.EventHandler;
import ee_man.mod3.core.GuiHandler;
import ee_man.mod3.core.Proxy;
import ee_man.mod3.core.Version;
import ee_man.mod3.enchantment.EnchantmentRange;
import ee_man.mod3.items.*;
import ee_man.mod3.network.*;
import ee_man.mod3.plugins.computercraft.ComputerCraftPlugin;
import ee_man.mod3.plugins.computercraft.peripheralTileEntity.TileEntityCannonPeripheral;
import ee_man.mod3.recipe.*;
import ee_man.mod3.tileEntity.*;
import ee_man.mod3.utils.Localization;
import ee_man.mod3.utils.MainUtils;

/**
 * @author ee_man
 */
@Mod(modid = DefaultProperties.MOD_ID, name = DefaultProperties.MOD_NAME, version = Version.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {DefaultProperties.MOD_ID}, packetHandler = PacketHandlerClient.class), serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {DefaultProperties.MOD_ID}, packetHandler = PacketHandlerServer.class))
public class Core{
	
	@Instance
	public static Core instance = new Core();
	
	private GuiHandler guiHandler = new GuiHandler();
	
	public static Item itemBroom;
	public static Item itemVacuumCleaner;
	public static Item itemSignEditTool;
	public static Item itemAdvSign;
	public static Item itemUnbreakableAdvSign;
	public static Item itemPaintingBlock;
	public static Item itemPaintBrush;
	public static Item itemChessDesk;
	public static Item itemCannonBlock;
	public static Item itemSaverChest;
	public static Item itemAntiEnchantWand;
	public static Item itemPrivateChest;
	public static Item itemPrivateChestUpgrade;
	public static Item itemPortableWorkbench;
	public static Item itemPlayerInv;
	public static Item itemPalette;
	public static Item itemRobotPanel;
	public static Item itemRobot;
	public static Item itemNormalChestToPrivateChestUpgrade;
	public static Item itemPaintingMover;
	
	public static Block blockAdvSignPost;
	public static Block blockAdvSignWall;
	public static Block blockUnbreakableAdvSignPost;
	public static Block blockUnbreakableAdvSignWall;
	public static Block blockPainting;
	public static Block blockChessDesk;
	public static Block blockCannonBlock;
	public static Block blockSaverChest;
	public static Block blockPrivateChest;
	public static Block blockPlayerInv;
	
	public static Enchantment enchantmentRange;
	
	public static CreativeTabs creativeTab;
	
	public static Logger log = Logger.getLogger(DefaultProperties.MOD_NAME);
	
	public static Configuration config;
	
	@SidedProxy(clientSide = "ee_man.mod3.client.ProxyClient", serverSide = "ee_man.mod3.core.Proxy")
	public static Proxy proxy;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		proxy.registerRenderThings();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		Localization.addLocalization(DefaultProperties.LANG_FILE, "en_US");
		NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
		TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ModMetadata modMeta = event.getModMetadata();
		modMeta.modId = DefaultProperties.MOD_ID;
		modMeta.name = DefaultProperties.MOD_NAME;
		modMeta.authorList = Arrays.asList(new String[]{"ee_man"});
		modMeta.url = "http://www.minecraftforum.net/topic/1715756-";
		modMeta.description = "Create, Play or Draw and Clean up After";
		modMeta.version = Version.VERSION;
		modMeta.autogenerated = false;
		Version.check();
		log.setParent(FMLLog.getLogger());
		Core.config = new Configuration(new File(event.getModConfigurationDirectory(), DefaultProperties.MOD_NAME + ".cfg"));
		try{
			Core.config.load();
			Property Broom_id = Core.config.get("items_ids", "Broom", DefaultProperties.ITEM_ID_BROOM);
			
			Property VacuumCleaner_id = Core.config.get("items_ids", "VacuumCleaner", DefaultProperties.ITEM_ID_VACUUMCLEANER);
			
			Property SignEditTool_id = Core.config.get("items_ids", "SignEditTool", DefaultProperties.ITEM_ID_SIGNEDITTOOL);
			
			Property AdvSign_id = Core.config.get("items_ids", "AdvSign", DefaultProperties.ITEM_ID_ADVSIGN);
			Property AdvSignPost_id = Core.config.get("Blocks_ids", "AdvSignPost", DefaultProperties.BLOCK_ID_ADVSIGNPOST);
			Property AdvSignWall_id = Core.config.get("Blocks_ids", "AdvSignWall", DefaultProperties.BLOCK_ID_ADVSIGNWALL);
			
			Property UnbAdvSign_id = Core.config.get("items_ids", "UnbreakableAdvSign", 0);
			Property UnbAdvSignPost_id = Core.config.get("Blocks_ids", "UnbreakableAdvSignPost", 0);
			Property UnbAdvSignWall_id = Core.config.get("Blocks_ids", "UnbreakableAdvSignWall", 0);
			
			Property PaintingItem_id = Core.config.get("items_ids", "PaintingBlock", DefaultProperties.ITEM_ID_PAINTING);
			Property Painting_id = Core.config.get("Blocks_ids", "PaintingBlock", DefaultProperties.BLOCK_ID_PAINTING);
			Property ItemPaintbrush_id = Core.config.get("items_ids", "ItemPaintBrush", DefaultProperties.ITEM_ID_PAINTBRUSH);
			
			Property ChessDeskItem_id = Core.config.get("items_ids", "ChessDesk", DefaultProperties.ITEM_ID_CHESSDESK);
			Property ChessDesk_id = Core.config.get("Blocks_ids", "ChessDesk", DefaultProperties.BLOCK_ID_CHESSDESK);
			
			Property CannonItem_id = Core.config.get("items_ids", "Cannon", DefaultProperties.ITEM_ID_CANNON);
			Property Cannon_id = Core.config.get("Blocks_ids", "Cannon", DefaultProperties.BLOCK_ID_CANNON);
			
			Property SaverChest_id = Core.config.get("Blocks_ids", "SaverChest", DefaultProperties.BLOCK_ID_SAVERCHEST);
			
			Property AntiEnchantWand_id = Core.config.get("items_ids", "AntiEnchantWand", DefaultProperties.ITEM_ID_ANTIENCHANTWAND);
			
			Property EnchantmentRange_id = Core.config.get("Enchantment_ids", "Range", DefaultProperties.ENCHANTMENT_ID_RANGE);
			
			Property PrivateChest_id = Core.config.get("Blocks_ids", "PrivateChest", DefaultProperties.BLOCK_ID_PRIVATECHEST);
			
			Property PrivateChestUpgrade_id = Core.config.get("items_ids", "PrivateChestUpgrade", DefaultProperties.ITEM_ID_UPGRADEPRIVATECHEST);
			
			Property NormalChestToPrivateChestUpgrade_id = Core.config.get("items_ids", "NormalChestToPrivateChestUpgrade", DefaultProperties.ITEM_ID_NORMALCHESTTOPRIVATECHESTUPGRADE);
			
			Property PortableWorkbench_id = Core.config.get("items_ids", "PortableWorkbench", DefaultProperties.ITEM_ID_WORKBENCH);
			
			Property PlayerInv_id = Core.config.get("Blocks_ids", "PlayerInvBlock", DefaultProperties.BLOCK_ID_PLAYERINV);
			
			Property Palette_id = Core.config.get("items_ids", "Palette", DefaultProperties.ITEM_ID_PALETTE);
			
			Property CreativeTab_Item_id = Core.config.get("CreativeTab", "Item_id", 0);
			
			creativeTab = (new CreativeTab(DefaultProperties.MOD_ID));
			
			if(CreativeTab_Item_id.getInt(0) > 0 && Item.itemsList[CreativeTab_Item_id.getInt(0)] != null)
				((CreativeTab)creativeTab).item = new ItemStack(Item.itemsList[CreativeTab_Item_id.getInt(0)]);
			if(Broom_id.getInt(DefaultProperties.ITEM_ID_BROOM) != 0){
				itemBroom = (new ItemBroom(Broom_id.getInt(DefaultProperties.ITEM_ID_BROOM), Core.config.get("Broom", "MaxDamage", 3072).getInt(3072), Core.config.get("Broom", "radius", 6).getDouble(6)).setUnlocalizedName("broom"));
				if(config.get("Crafting", "Broom", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBroom, 1), new Object[]{"001", "120", "110", Character.valueOf('1'), "stickWood", Character.valueOf('2'), Item.silk}));
				Property Broom_BONUS_CHEST_GEN = config.get("ChestGen", "Broom_" + ChestGenHooks.BONUS_CHEST, 2);
				if(Broom_BONUS_CHEST_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(itemBroom.itemID, 0, 1, 1, Broom_BONUS_CHEST_GEN.getInt(2)));
			}
			if(VacuumCleaner_id.getInt(DefaultProperties.ITEM_ID_VACUUMCLEANER) != 0){
				itemVacuumCleaner = (new ItemVacuumCleaner(VacuumCleaner_id.getInt(DefaultProperties.ITEM_ID_VACUUMCLEANER), Core.config.get("VacuumCleaner", "MaxDamage", 6144).getInt(3072), Core.config.get("VacuumCleaner", "radius", 6).getDouble(6)).setUnlocalizedName("vacuumCleaner"));
				if(config.get("Crafting", "VacuumCleaner", true).getBoolean(true))
					GameRegistry.addRecipe(new ItemStack(itemVacuumCleaner, 1), new Object[]{"011", "102", Character.valueOf('1'), Item.ingotIron, Character.valueOf('2'), Item.redstone});
			}
			if(SignEditTool_id.getInt(DefaultProperties.ITEM_ID_SIGNEDITTOOL) != 0){
				Property SignEditTool_MaxDamage = Core.config.get("SignEditTool", "MaxDamage", 1024);
				itemSignEditTool = (new ItemSignEdit(SignEditTool_id.getInt(DefaultProperties.ITEM_ID_SIGNEDITTOOL), SignEditTool_MaxDamage.getInt()).setUnlocalizedName("signEditTool"));
				if(config.get("Crafting", "SingEditTool", true).getBoolean(true))
					GameRegistry.addShapelessRecipe(new ItemStack(itemSignEditTool, 1), new Object[]{Item.sign, Item.feather});
			}
			if(AdvSign_id.getInt(DefaultProperties.ITEM_ID_ADVSIGN) != 0 && AdvSignPost_id.getInt(DefaultProperties.BLOCK_ID_PAINTING) != 0 && AdvSignWall_id.getInt(DefaultProperties.BLOCK_ID_ADVSIGNWALL) != 0){
				Property rightClickLink = config.get("AdvSign", "RightClickLink", true);
				blockAdvSignPost = (new BlockAdvSign(AdvSignPost_id.getInt(DefaultProperties.BLOCK_ID_ADVSIGNPOST), true, rightClickLink.getBoolean(true), AdvSign_id.getInt(DefaultProperties.ITEM_ID_ADVSIGN))).setHardness(1.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("advSign");
				blockAdvSignWall = (new BlockAdvSign(AdvSignWall_id.getInt(DefaultProperties.BLOCK_ID_ADVSIGNWALL), false, rightClickLink.getBoolean(true), AdvSign_id.getInt(DefaultProperties.ITEM_ID_ADVSIGN))).setHardness(1.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("advSign");
				itemAdvSign = (new ItemAdvSign(AdvSign_id.getInt(DefaultProperties.ITEM_ID_ADVSIGN), blockAdvSignPost, blockAdvSignWall).setUnlocalizedName("advSign"));
				if(config.get("Crafting", "AdvSign", true).getBoolean(true))
					GameRegistry.addShapelessRecipe(new ItemStack(itemAdvSign, 1), new Object[]{Item.sign, Item.book});
				GameRegistry.registerTileEntity(TileEntityAdvSign.class, DefaultProperties.MOD_ID + ".advSign");
				TileEntityAdvSign.isAddedToTileMap = true;
			}
			if(UnbAdvSign_id.getInt(0) != 0 && UnbAdvSignPost_id.getInt(0) != 0 && UnbAdvSignWall_id.getInt(0) != 0){
				Property rightClickLink = config.get("UnbreakableAdvSign", "RightClickLink", true);
				blockUnbreakableAdvSignPost = (new BlockUnbreakableAdvSign(UnbAdvSignPost_id.getInt(0), true, rightClickLink.getBoolean(true))).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("unbreakableAdvSign");
				blockUnbreakableAdvSignWall = (new BlockUnbreakableAdvSign(UnbAdvSignWall_id.getInt(0), false, rightClickLink.getBoolean(true))).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("unbreakableAdvSign");
				itemUnbreakableAdvSign = (new ItemAdvSign(UnbAdvSign_id.getInt(0), blockUnbreakableAdvSignPost, blockUnbreakableAdvSignWall).setUnlocalizedName("unbreakableAdvSign"));
				if(!TileEntityAdvSign.isAddedToTileMap)
					GameRegistry.registerTileEntity(TileEntityAdvSign.class, DefaultProperties.MOD_ID + ".advSign");
			}
			if(PaintingItem_id.getInt(DefaultProperties.ITEM_ID_PAINTING) != 0 && ItemPaintbrush_id.getInt(DefaultProperties.ITEM_ID_PAINTBRUSH) != 0 && Painting_id.getInt(DefaultProperties.BLOCK_ID_PAINTING) != 0){
				itemPaintBrush = (new ItemPaintBrush(ItemPaintbrush_id.getInt(DefaultProperties.ITEM_ID_PAINTBRUSH)).setUnlocalizedName("paintBrush"));
				itemPaintingBlock = (new ItemPainting(PaintingItem_id.getInt(DefaultProperties.ITEM_ID_PAINTING))).setUnlocalizedName("paintingBlock");
				blockPainting = (new BlockPainting(Painting_id.getInt(DefaultProperties.BLOCK_ID_PAINTING)).setHardness(0.5F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("paintingBlock"));
				ItemPaintBrush.paintRow = Core.config.get("Paint", "Row", 16, "Row can be from 4 to 100 (recommend to degree 2 like 8,16,32,64)").getInt(16);
				if(ItemPaintBrush.paintRow < 1)
					ItemPaintBrush.paintRow = 1;
				ItemPaintBrush.brushRadius = Core.config.get("Paint", "BrushRadius", 3, "it need be >0").getInt(3);
				if(ItemPaintBrush.brushRadius < 1)
					ItemPaintBrush.brushRadius = 1;
				ItemPaintBrush.setNoDrawPixels(Core.config.get("Paint", "BrushNoDrawPixels", "3;3,2;3,3;2", "|x|;|y|,|x|;|y|,|x|;|y|...").getString());
				GameRegistry.registerTileEntity(TileEntityPainting.class, DefaultProperties.MOD_ID + ".paint");
				Property PaintingMover_id = config.get("items_ids", "PaintingMover", DefaultProperties.ITEM_ID_PAINTINGMOVER);
				if(PaintingMover_id.getInt(DefaultProperties.ITEM_ID_PAINTINGMOVER) != 0){
					itemPaintingMover = new ItemPaintingMover(PaintingMover_id.getInt(DefaultProperties.ITEM_ID_PAINTINGMOVER), config.get("PaintingMover", "MaxDamage", 256).getInt(256)).setUnlocalizedName("paintingMover");
					if(config.get("Crafting", "PaintingMover", true).getBoolean(true))
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintingMover, 1), new Object[]{"is", " s", Character.valueOf('s'), "stickWood", Character.valueOf('i'), Item.ingotIron}));
				}
				if(config.get("Crafting", "PaintBrushColorChange", true).getBoolean(true))
					GameRegistry.addRecipe(new RecipePaintBrush());
				if(config.get("Crafting", "BigPaintBrush", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintBrush, 1, 0), new Object[]{"w", "s", Character.valueOf('w'), new ItemStack(Block.cloth, 1, 0), Character.valueOf('s'), "stickWood"}));
				if(config.get("Crafting", "SmallPaintBrush", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintBrush, 1, 1), new Object[]{"f", "s", Character.valueOf('f'), Item.feather, Character.valueOf('s'), "stickWood"}));
				Property Painting_craft = config.get("Crafting", "Painting", 8);
				if(Painting_craft.getInt(8) != 0)
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPaintingBlock, Painting_craft.getInt(8)), new Object[]{"wsw", "scs", "wsw", Character.valueOf('w'), "plankWood", Character.valueOf('s'), "stickWood", Character.valueOf('c'), Block.cloth}));
				if(Palette_id.getInt(DefaultProperties.ITEM_ID_PALETTE) != 0){
					itemPalette = new ItemPalette(Palette_id.getInt(DefaultProperties.ITEM_ID_PALETTE)).setUnlocalizedName("palette");
					if(config.get("Crafting", "Palette", false).getBoolean(false))
						GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPalette), new Object[]{"slabWood", "dyeRed", "dyeGreen", "dyeBlue"}));
				}
				ItemPainting.pictures = ItemPainting.getItemsFromPics(MainUtils.getPicsFromJar("/pictures"));
				ItemStack is = new ItemStack(Core.itemPaintingBlock);
				Property Painting_GEN;
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.DUNGEON_CHEST, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.MINESHAFT_CORRIDOR, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.STRONGHOLD_LIBRARY, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.STRONGHOLD_CROSSING, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.STRONGHOLD_CORRIDOR, 0);
				if(Painting_GEN.getInt(0) != 0)
					ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(0)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.PYRAMID_DESERT_CHEST, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2);
				if(Painting_GEN.getInt(2) != 0)
					ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(2)));
				Painting_GEN = config.get("ChestGen", "Painting_" + ChestGenHooks.VILLAGE_BLACKSMITH, 0);
				if(Painting_GEN.getInt(0) != 0)
					ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(is, 1, 1, Painting_GEN.getInt(0)));
			}
			if(ChessDeskItem_id.getInt(DefaultProperties.ITEM_ID_CHESSDESK) != 0 && ChessDesk_id.getInt(DefaultProperties.BLOCK_ID_CHESSDESK) != 0){
				blockChessDesk = (new BlockChessDesk(ChessDesk_id.getInt(DefaultProperties.BLOCK_ID_CHESSDESK))).setHardness(0.5F).setUnlocalizedName("chessDesk");
				itemChessDesk = (new ItemChessDesk(ChessDeskItem_id.getInt(DefaultProperties.ITEM_ID_CHESSDESK))).setUnlocalizedName("chessDesk");
				GameRegistry.registerTileEntity(TileEntityChessDesk.class, DefaultProperties.MOD_ID + ".chess");
				if(config.get("Crafting", "ChessBoard", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemChessDesk, 1), new Object[]{"w b", "ppp", Character.valueOf('w'), new ItemStack(Block.cloth, 1, 0), Character.valueOf('b'), new ItemStack(Block.cloth, 1, 15), Character.valueOf('p'), "plankWood"}));
				// GameRegistry.addShapelessRecipe(new ItemStack(itemChessDesk),
				// new Object[]{Core.itemChessDesk});
			}
			if(CannonItem_id.getInt(DefaultProperties.ITEM_ID_CANNON) != 0 && Cannon_id.getInt(DefaultProperties.BLOCK_ID_CANNON) != 0){
				Class<? extends TileEntity> tileClass = ((Loader.isModLoaded("ComputerCraft") && config.get("ComputerCraft", "CannonPeripheral", true).getBoolean(true)) ? TileEntityCannonPeripheral.class : TileEntityCannon.class);
				blockCannonBlock = new BlockCannon(Cannon_id.getInt(DefaultProperties.BLOCK_ID_CANNON), tileClass).setHardness(1.5F);
				itemCannonBlock = new ItemCannon(CannonItem_id.getInt(DefaultProperties.ITEM_ID_CANNON)).setUnlocalizedName("cannon");
				GameRegistry.registerTileEntity(tileClass, DefaultProperties.MOD_ID + ".cannon");
				if(config.get("Crafting", "Cannon", true).getBoolean(true))
					GameRegistry.addRecipe(new ItemStack(itemCannonBlock, 1), new Object[]{"i  ", " i ", "iii", Character.valueOf('i'), Item.ingotIron});
				TileEntityCannon.defMaxStrength = (byte)config.get("Cannon", "MaxStrength", (int)TileEntityCannon.defMaxStrength, "0 - 127").getInt(TileEntityCannon.defMaxStrength);
			}
			if(SaverChest_id.getInt(DefaultProperties.BLOCK_ID_SAVERCHEST) != 0){
				blockSaverChest = new BlockSaverChest(SaverChest_id.getInt(DefaultProperties.BLOCK_ID_SAVERCHEST)).setHardness(3.0F).setUnlocalizedName("chest");
				itemSaverChest = new ItemBlockMod3(SaverChest_id.getInt(DefaultProperties.BLOCK_ID_SAVERCHEST) - 256, true).setCreativeTab(creativeTab).setUnlocalizedName("chest");
				GameRegistry.registerTileEntity(TileEntitySaverChest.class, DefaultProperties.MOD_ID + ".chest");
				for(int i = 0; i < 4; i++)
					if(config.get("Crafting", "WoodenChest_" + BlockLog.woodType[i], true).getBoolean(true))
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemSaverChest, 1, i), new Object[]{"plp", "l0l", "plp", Character.valueOf('p'), "plankWood", Character.valueOf('l'), new ItemStack(Block.wood, 1, i)}));
			}
			if(AntiEnchantWand_id.getInt(DefaultProperties.ITEM_ID_ANTIENCHANTWAND) != 0){
				itemAntiEnchantWand = new ItemMod3(AntiEnchantWand_id.getInt(DefaultProperties.ITEM_ID_ANTIENCHANTWAND)).setUnlocalizedName("antiEnchantWand").setMaxStackSize(1).setMaxDamage(Core.config.get("AntiEnchantWand", "MaxDamage", 32, "0 is infinite").getInt(32)).setFull3D();
				if(config.get("Crafting", "AntiEnchantWand", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntiEnchantWand), new Object[]{" gb", " dg", "s  ", Character.valueOf('s'), "stickWood", Character.valueOf('g'), Item.ingotGold, Character.valueOf('d'), Item.diamond, Character.valueOf('b'), Item.book}));
				RecipeAntiEnchant antiEnchantRecipe = new RecipeAntiEnchant();
				if(config.get("Crafting", "RemovingEnchant", true).getBoolean(true)){
					GameRegistry.addRecipe(antiEnchantRecipe);
					GameRegistry.registerCraftingHandler(antiEnchantRecipe);
				}
			}
			if(EnchantmentRange_id.getInt(DefaultProperties.ENCHANTMENT_ID_RANGE) != 0)
				enchantmentRange = new EnchantmentRange(EnchantmentRange_id.getInt(DefaultProperties.ENCHANTMENT_ID_RANGE), 5, config.get("EnchantmentRange", "MaxLevel", 5).getInt(5));
			if(PrivateChest_id.getInt(DefaultProperties.BLOCK_ID_PRIVATECHEST) != 0){
				blockPrivateChest = new BlockPrivateChest(PrivateChest_id.getInt(DefaultProperties.BLOCK_ID_PRIVATECHEST)).setUnlocalizedName("privateChest");
				itemPrivateChest = new ItemBlockMod3(PrivateChest_id.getInt(DefaultProperties.BLOCK_ID_PRIVATECHEST) - 256, false).setUnlocalizedName("privateChest");
				if(config.get("Crafting", "PrivateChest", true).getBoolean(true))
					GameRegistry.addRecipe(new ItemStack(blockPrivateChest), new Object[]{"ror", "oco", "oco", Character.valueOf('r'), Item.redstone, Character.valueOf('o'), Block.obsidian, Character.valueOf('c'), Block.chest});
				if(PrivateChestUpgrade_id.getInt(DefaultProperties.ITEM_ID_UPGRADEPRIVATECHEST) != 0){
					itemPrivateChestUpgrade = new ItemPrivateChestUpgrade(PrivateChestUpgrade_id.getInt(DefaultProperties.ITEM_ID_UPGRADEPRIVATECHEST)).setUnlocalizedName("privateChestUpgrade");
					if(config.get("Crafting", "PrivateUpgrade", true).getBoolean(true))
						GameRegistry.addRecipe(new ItemStack(itemPrivateChestUpgrade, 1, 0), new Object[]{" i ", "iri", "i i", Character.valueOf('r'), Item.redstone, Character.valueOf('i'), Item.ingotIron});
					if(config.get("Crafting", "UnbreakableUpgrade_Obsidian", true).getBoolean(true))
						GameRegistry.addRecipe(new ItemStack(itemPrivateChestUpgrade, 1, 1), new Object[]{" i ", "ioi", "i i", Character.valueOf('o'), Block.obsidian, Character.valueOf('i'), Item.ingotIron});
					if(config.get("Crafting", "UnbreakableUpgrade_BedRock", true).getBoolean(true))
						GameRegistry.addRecipe(new ItemStack(itemPrivateChestUpgrade, 1, 1), new Object[]{" i ", "ibi", "i i", Character.valueOf('b'), Block.bedrock, Character.valueOf('i'), Item.ingotIron});
					if(config.get("Crafting", "CraftingUpgrade", true).getBoolean(true))
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPrivateChestUpgrade, 1, 3), new Object[]{" p ", "pwp", "p p", Character.valueOf('w'), new ItemStack(Block.workbench), Character.valueOf('p'), "plankWood"}));
					if(itemVacuumCleaner != null && config.get("Crafting", "CollectingUpgrade", true).getBoolean(true))
						GameRegistry.addRecipe(new ItemStack(itemPrivateChestUpgrade, 1, 2), new Object[]{" i ", "ivi", "i i", Character.valueOf('v'), itemVacuumCleaner, Character.valueOf('i'), Item.ingotIron});
				}
				if(NormalChestToPrivateChestUpgrade_id.getInt(DefaultProperties.ITEM_ID_NORMALCHESTTOPRIVATECHESTUPGRADE) != 0){
					itemNormalChestToPrivateChestUpgrade = new ItemNormalChestToPrivateChestUpgrade(NormalChestToPrivateChestUpgrade_id.getInt(DefaultProperties.ITEM_ID_NORMALCHESTTOPRIVATECHESTUPGRADE), blockPrivateChest.blockID).setUnlocalizedName("normalChestToPrivateChestUpgrade");
					if(config.get("Crafting", "NormalChestToPrivateChestUpgrade", true).getBoolean(true))
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemNormalChestToPrivateChestUpgrade), new Object[]{"ror", "oco", "owo", Character.valueOf('r'), Item.redstone, Character.valueOf('o'), Block.obsidian, Character.valueOf('c'), Block.chest, Character.valueOf('w'), "plankWood"}));
				}
				GameRegistry.registerTileEntity(TileEntityPrivateChest.class, DefaultProperties.MOD_ID + ".privateChest");
			}
			if(PortableWorkbench_id.getInt(DefaultProperties.ITEM_ID_WORKBENCH) != 0){
				itemPortableWorkbench = new ItemWorkbench(PortableWorkbench_id.getInt(DefaultProperties.ITEM_ID_WORKBENCH), Core.config.get("PortableWorkbench", "MaxDamage", 4096, "0 is infinite").getInt(4096)).setUnlocalizedName("portableWorkbench");
				if(config.get("Crafting", "PortableWorkbench", true).getBoolean(true))
					GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPortableWorkbench, 1), new Object[]{Block.workbench, "stickWood", "stickWood"}));
			}
			if(PlayerInv_id.getInt(DefaultProperties.BLOCK_ID_PLAYERINV) != 0){
				blockPlayerInv = new BlockPlayerInv(PlayerInv_id.getInt(DefaultProperties.BLOCK_ID_PLAYERINV)).setUnlocalizedName("playerInv");
				itemPlayerInv = new ItemBlockMod3(PlayerInv_id.getInt(DefaultProperties.BLOCK_ID_PLAYERINV) - 256, false).setUnlocalizedName("playerInv");
				if(config.get("Crafting", "PlayerInv", false).getBoolean(false))
					GameRegistry.addRecipe(new ItemStack(blockPlayerInv), new Object[]{"ede", "dsd", "ede", Character.valueOf('e'), Item.enderPearl, Character.valueOf('d'), Item.diamond, Character.valueOf('s'), Item.netherStar});
				GameRegistry.registerTileEntity(TileEntityPlayerInv.class, DefaultProperties.MOD_ID + ".playerInv");
			}
			
			if(Loader.isModLoaded("ComputerCraft")){
				ComputerCraftPlugin.registerTurtles();
			}
		} finally{
			Core.config.save();
		}
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){
	}
	
	public static ArrayList<ItemStack> getAllItemsArray(){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(Core.itemBroom != null)
			items.add(new ItemStack(Core.itemBroom));
		if(Core.itemVacuumCleaner != null)
			items.add(new ItemStack(Core.itemVacuumCleaner));
		if(Core.itemSignEditTool != null)
			items.add(new ItemStack(Core.itemSignEditTool));
		if(Core.itemAdvSign != null)
			items.add(new ItemStack(Core.itemAdvSign));
		if(Core.itemPaintingBlock != null){
			items.add(new ItemStack(Core.itemPaintingBlock));
		}
		if(Core.itemPaintBrush != null){
			items.add(new ItemStack(Core.itemPaintBrush, 1, 0));
			items.add(new ItemStack(Core.itemPaintBrush, 1, 1));
		}
		if(Core.itemPalette != null)
			items.add(new ItemStack(Core.itemPalette));
		if(Core.itemChessDesk != null)
			items.add(new ItemStack(Core.itemChessDesk));
		if(Core.itemSaverChest != null){
			items.add(new ItemStack(Core.itemSaverChest, 1, 0));
			items.add(new ItemStack(Core.itemSaverChest, 1, 1));
			items.add(new ItemStack(Core.itemSaverChest, 1, 2));
			items.add(new ItemStack(Core.itemSaverChest, 1, 3));
		}
		if(Core.itemPrivateChest != null)
			items.add(new ItemStack(Core.itemPrivateChest));
		if(Core.itemPrivateChestUpgrade != null){
			items.add(new ItemStack(Core.itemPrivateChestUpgrade, 1, 0));
			items.add(new ItemStack(Core.itemPrivateChestUpgrade, 1, 1));
			items.add(new ItemStack(Core.itemPrivateChestUpgrade, 1, 2));
			items.add(new ItemStack(Core.itemPrivateChestUpgrade, 1, 3));
		}
		if(Core.itemCannonBlock != null)
			items.add(new ItemStack(Core.itemCannonBlock));
		if(Core.itemAntiEnchantWand != null)
			items.add(new ItemStack(Core.itemAntiEnchantWand));
		if(Core.itemPortableWorkbench != null)
			items.add(new ItemStack(Core.itemPortableWorkbench));
		if(Core.itemPlayerInv != null)
			items.add(new ItemStack(Core.itemPlayerInv));
		if(Core.itemRobotPanel != null)
			items.add(new ItemStack(Core.itemRobotPanel));
		if(Core.itemRobot != null)
			items.add(new ItemStack(Core.itemRobot));
		if(Core.itemUnbreakableAdvSign != null)
			items.add(new ItemStack(Core.itemUnbreakableAdvSign));
		if(Core.itemNormalChestToPrivateChestUpgrade != null)
			items.add(new ItemStack(Core.itemNormalChestToPrivateChestUpgrade));
		if(Core.itemPaintingMover != null)
			items.add(new ItemStack(Core.itemPaintingMover));
		return items;
	}
}