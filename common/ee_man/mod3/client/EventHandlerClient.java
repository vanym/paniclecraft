package ee_man.mod3.client;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.plugins.computercraft.turtle.TurtlePaintBrush;
import ee_man.mod3.plugins.computercraft.turtle.TurtleSignEdit;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;

@SideOnly(Side.CLIENT)
public class EventHandlerClient{
	
	@ForgeSubscribe
	public void textureStitchEvent(TextureStitchEvent.Pre event){
		if(event.map.getTextureType() == 0 && Loader.isModLoaded("ComputerCraft")){
			if(Core.config.get("Turtle", "PaintBrush", true).getBoolean(true) && Core.itemPaintBrush != null)
				TurtlePaintBrush.icon = event.map.registerIcon(DefaultProperties.TEXTURE_ID + ":" + "turtle.PaintBrush");
			if(Core.config.get("Turtle", "SignEdit", true).getBoolean(true) && Core.itemSignEditTool != null)
				TurtleSignEdit.icon = event.map.registerIcon(DefaultProperties.TEXTURE_ID + ":" + "turtle.SignEdit");
		}
	}
}
