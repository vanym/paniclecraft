package ee_man.mod3.client;

import java.io.File;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import ee_man.mod3.Core;
import ee_man.mod3.blocks.BlockPainting;
import ee_man.mod3.client.renderer.item.*;
import ee_man.mod3.client.renderer.tileEntity.*;
import ee_man.mod3.core.Proxy;
import ee_man.mod3.tileEntity.*;

public class ProxyClient extends Proxy{
	
	@Override
	public void registerRenderThings(){
		Property renderChessDesk = Core.config.get("render", "renderChessDesk", true);
		Property renderChessDeskItem = Core.config.get("render", "renderChessDeskItem", true);
		Property renderAdvSign = Core.config.get("render", "renderAdvSign", true);
		Property renderAdvSignItem = Core.config.get("render", "renderAdvSignItem", true);
		Property renderUnbreakableAdvSignItem = Core.config.get("render", "renderUnbreakableAdvSignItem", true);
		Property renderPaintBlock = Core.config.get("render", "renderPaintBlock", true);
		Property renderPaintingItem = Core.config.get("render", "renderPaintingItem", true);
		Property renderCannon = Core.config.get("render", "renderCannon", true);
		Property renderCannonItem = Core.config.get("render", "renderCannonItem", true);
		Property brushSpecialBoundingBox = Core.config.get("Paint", "specialBoundingBox", true, "Enables or disables special Bounding Box when you use the paint brush");
		Property renderSaverChest = Core.config.get("render", "renderSaverChest", true);
		Property renderSaverChestItem = Core.config.get("render", "renderSaverChestItem", true);
		Property renderPrivateChest = Core.config.get("render", "renderPrivateChest", true);
		Property renderPrivateChestItem = Core.config.get("render", "renderPrivateChestItem", true);
		Property renderPortableWorkbenchItem = Core.config.get("render", "renderPortableWorkbenchItem", true);
		BlockPainting.specialBoundingBox = brushSpecialBoundingBox.getBoolean(true);
		if(renderAdvSign.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvSign.class, new TileEntityAdvSignRenderer());
		if(renderPaintBlock.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPainting.class, new TileEntityPaintingRenderer());
		if(renderChessDesk.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChessDesk.class, new TileEntityChessDeskRenderer());
		if(renderCannon.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		if(renderCannonItem.getBoolean(true) && renderCannon.getBoolean(true) && Core.itemCannonBlock != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemCannonBlock.itemID, new ItemRendererCannon());
		if(renderChessDeskItem.getBoolean(true) && renderChessDesk.getBoolean(true) && Core.itemChessDesk != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemChessDesk.itemID, new ItemRendererChessDesk());
		if(renderAdvSignItem.getBoolean(true) && renderAdvSign.getBoolean(true) && Core.itemAdvSign != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemAdvSign.itemID, new ItemRendererAdvSign());
		if(renderUnbreakableAdvSignItem.getBoolean(true) && renderAdvSign.getBoolean(true) && Core.itemUnbreakableAdvSign != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemUnbreakableAdvSign.itemID, new ItemRendererAdvSign());
		if(renderPaintingItem.getBoolean(true) && renderPaintBlock.getBoolean(true) && Core.itemPaintingBlock != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemPaintingBlock.itemID, new ItemRendererPainting());
		if(renderSaverChest.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySaverChest.class, new TileEntitySaverChestRenderer());
		if(renderSaverChest.getBoolean(true) && Core.itemSaverChest != null && renderSaverChestItem.getBoolean(true))
			MinecraftForgeClient.registerItemRenderer(Core.itemSaverChest.itemID, new ItemRendererSaverChest());
		if(renderPrivateChest.getBoolean(true))
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPrivateChest.class, new TileEntityPrivateChestRenderer());
		if(renderPrivateChest.getBoolean(true) && Core.itemPrivateChest != null && renderPrivateChestItem.getBoolean(true))
			MinecraftForgeClient.registerItemRenderer(Core.itemPrivateChest.itemID, new ItemRendererPrivateChest());
		if(renderPortableWorkbenchItem.getBoolean(true) && Core.itemPortableWorkbench != null)
			MinecraftForgeClient.registerItemRenderer(Core.itemPortableWorkbench.itemID, new ItemRendererPortableWorkbench());
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
		Core.config.save();
	}
	
	public File getWorldFolder(){
		return new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFile("."), "saves" + File.separator + DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
	}
}
