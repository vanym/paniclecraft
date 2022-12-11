package ee_man.mod3;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config{
	public static Configuration config;
	
	public static void init(File configFile){
		if(config == null)
			config = new Configuration(configFile);
	}
	
	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.modID.equalsIgnoreCase(DEF.MOD_ID)){
			
		}
	}
	
	public static Configuration getConfig(){
		return config;
	}
	
	public static void load(){
		if(config == null)
			return;
	}
	
	public static void save(){
		if(config.hasChanged())
			config.save();
	}
}
