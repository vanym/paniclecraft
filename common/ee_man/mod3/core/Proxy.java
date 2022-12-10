package ee_man.mod3.core;

import java.io.File;

import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.FMLCommonHandler;
import ee_man.mod3.DefaultProperties;

public class Proxy{
	public void registerRenderThings(){
	}
	
	public File getWorldFolder(){
		return new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFile("."), DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
	}
	
	public File getModFolderInWorld(){
		File f = new File(this.getWorldFolder(), DefaultProperties.MOD_ID);
		f.mkdirs();
		return f;
	}
}
