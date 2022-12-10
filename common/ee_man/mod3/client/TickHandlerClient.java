package ee_man.mod3.client;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.TickType;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.core.Version;

public class TickHandlerClient implements ITickHandler{
	
	private boolean nagged;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData){
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData){
		if(nagged)
			return;
		EntityPlayer player = (EntityPlayer)tickData[0];
		if(Version.needsUpdateNoticeAndMarkAsSeen()){
			player.addChatMessage(String.format("\u00A7cNew version of " + DefaultProperties.MOD_NAME + " available: %s for Minecraft %s", Version.getRecommendedVersion(), Loader.instance().getMinecraftModContainer().getVersion()));
			for(String updateLine : Version.getChangelog()){
				player.addChatMessage("\u00A79" + updateLine);
			}
		}
		nagged = true;
	}
	
	@Override
	public EnumSet<TickType> ticks(){
		return EnumSet.of(TickType.PLAYER);
	}
	
	@Override
	public String getLabel(){
		return DefaultProperties.MOD_ID + " - Player update tick";
	}
	
}
