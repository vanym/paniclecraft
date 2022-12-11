package ee_man.mod3.client;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DEF;
import ee_man.mod3.plugins.computercraft.ComputerCraftPlugin;
import ee_man.mod3.utils.ISidePaintingProvider;

@SideOnly(Side.CLIENT)
public class ClientEventHandler{
	
	@SubscribeEvent
	public void textureStitchEvent(TextureStitchEvent.Pre event){
		if(Loader.isModLoaded("ComputerCraft")){
			if(event.map.getTextureType() == 0)
				if(ComputerCraftPlugin.turtlePaintBrush != null){
					ComputerCraftPlugin.turtlePaintBrush.iconLeft = event.map.registerIcon(DEF.MOD_ID + ":" + "turtle.PaintBrush.left");
					ComputerCraftPlugin.turtlePaintBrush.iconRight = event.map.registerIcon(DEF.MOD_ID + ":" + "turtle.PaintBrush.right");
				}
		}
	}
	
	@SubscribeEvent
	public void texruteUnload(WorldEvent.Unload event){
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()){
			for(Object tileE : event.world.loadedTileEntityList){
				if(tileE instanceof ISidePaintingProvider){
					((ISidePaintingProvider)tileE).onWorldUnload();
				}
			}
		}
	}
}
