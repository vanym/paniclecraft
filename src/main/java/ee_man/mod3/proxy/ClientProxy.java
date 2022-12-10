package ee_man.mod3.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import ee_man.mod3.client.ClientEventHandler;
import ee_man.mod3.client.renderer.item.*;
import ee_man.mod3.client.renderer.tileentity.*;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.tileentity.TileEntityAdvSign;
import ee_man.mod3.tileentity.TileEntityCannon;
import ee_man.mod3.tileentity.TileEntityChessDesk;
import ee_man.mod3.tileentity.TileEntityPainting;
import ee_man.mod3.tileentity.TileEntityPaintingFrame;
import ee_man.mod3.utils.Painting;

public class ClientProxy extends CommonProxy{
	
	public static TileEntityPaintingRenderer tilePaintingRenderer = new TileEntityPaintingRenderer();
	public static TileEntityPaintingFrameRenderer tilePaintingFrameRenderer = new TileEntityPaintingFrameRenderer();
	public static TileEntityAdvSignRenderer tileAdvSignRenderer = new TileEntityAdvSignRenderer();
	public static TileEntityCannonRenderer tileCannonRenderer = new TileEntityCannonRenderer();
	public static TileEntityChessDeskRenderer tileChessDeskRenderer = new TileEntityChessDeskRenderer();
	
	@Override
	public void preInit(Configuration config){
		Painting.specialBoundingBox = config.getBoolean("Painting_SpecialBoundingBox", "Client_Render", true, "");
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public void init(Configuration config){
		registerRenders(config);
	}
	
	@Override
	public void postInit(Configuration config){
	}
	
	public void registerRenders(Configuration config){
		if(config.getBoolean("Painting_Tile", "Client_Render", true, ""))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class, tilePaintingRenderer);
		if(config.getBoolean("Painting_Item", "Client_Render", true, "") && ModItems.itemPainting != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.itemPainting, new ItemRendererPainting());
		if(config.getBoolean("PaintingFrame_Tile", "Client_Render", true, ""))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintingFrame.class, tilePaintingFrameRenderer);
		if(config.getBoolean("PaintingFrame_Item", "Client_Render", true, "") && ModItems.blockPaintingFrame != null)
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModItems.blockPaintingFrame), new ItemRendererPaintingFrame());
		if(config.getBoolean("PortableWorkbench_Item", "Client_Render", true, "") && ModItems.itemWorkbench != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.itemWorkbench, new ItemRendererPortableWorkbench());
		if(config.getBoolean("AdvSign_Tile", "Client_Render", true, ""))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvSign.class, tileAdvSignRenderer);
		if(config.getBoolean("AdvSign_Item", "Client_Render", true, "") && ModItems.itemAdvSign != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.itemAdvSign, new ItemRendererAdvSign());
		if(config.getBoolean("Cannon_Tile", "Client_Render", true, ""))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, tileCannonRenderer);
		if(config.getBoolean("Cannon_Item", "Client_Render", true, "") && ModItems.itemCannon != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.itemCannon, new ItemRendererCannon());
		if(config.getBoolean("Chess_Tile", "Client_Render", true, ""))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChessDesk.class, tileChessDeskRenderer);
		if(config.getBoolean("Chess_Item", "Client_Render", true, "") && ModItems.itemChessDesk != null)
			MinecraftForgeClient.registerItemRenderer(ModItems.itemChessDesk, new ItemRendererChessDesk());
	}
	
	public static void deleteTexture(int texID){
		GL11.glDeleteTextures(texID);
		// System.out.println(texID);
	}
}
