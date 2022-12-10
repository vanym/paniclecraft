package ee_man.mod3.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;

import net.minecraftforge.common.Property;

public class Version implements Runnable{
	
	private static Version instance = new Version();
	
	public enum EnumUpdateState{
		CURRENT, OUTDATED, CONNECTION_ERROR
	}
	
	public static final String VERSION = "1.0.0.4ePre1";
	private static final String REMOTE_VERSION_FILE = "https://dl.dropbox.com/u/14165098/MyMod/new/ver.txt";
	private static final String REMOTE_CHANGELOG_ROOT = "https://dl.dropbox.com/u/14165098/MyMod/new/verLog/" + DefaultProperties.MOD_ID + "/";
	
	public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;
	
	public static final int FORGE_VERSION_MAJOR = 4;
	public static final int FORGE_VERSION_MINOR = 0;
	public static final int FORGE_VERSION_PATCH = 0;
	
	private static String recommendedVersion;
	private static String[] cachedChangelog;
	
	public static String getVersion(){
		return VERSION;
	}
	
	public static boolean isOutdated(){
		return currentVersion == EnumUpdateState.OUTDATED;
	}
	
	public static boolean needsUpdateNoticeAndMarkAsSeen(){
		if(!isOutdated())
			return false;
		
		Property seen = Core.config.get("vars", "version.seen", VERSION);
		seen.comment = "indicates the last version the user has been informed about and will suppress further notices on it.";
		String seenVersion = seen.getString();
		Property seen_times = Core.config.get("vars", "version.seen." + seenVersion + ".times", 0);
		
		if(recommendedVersion == null || (recommendedVersion.equals(seenVersion) && seen_times.getInt() >= 3))
			return false;
		
		seen.set(recommendedVersion);
		seen_times.set(seen_times.getInt() + 1);
		Core.config.save();
		return true;
	}
	
	public static String getRecommendedVersion(){
		return recommendedVersion;
	}
	
	public static void versionCheck(){
		try{
			
			String location = REMOTE_VERSION_FILE;
			HttpURLConnection conn = null;
			while(location != null && !location.isEmpty()){
				URL url = new URL(location);
				conn = (HttpURLConnection)url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
				conn.connect();
				location = conn.getHeaderField("Location");
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = null;
			String mcVersion = Loader.instance().getMinecraftModContainer().getVersion();
			while((line = reader.readLine()) != null){
				if(line.startsWith(mcVersion)){
					if(line.contains(DefaultProperties.MOD_ID)){
						
						String[] tokens = line.split(":");
						recommendedVersion = tokens[2];
						
						if(line.endsWith(VERSION)){
							Core.log.finer("Using the latest version [" + getVersion() + "] for Minecraft " + mcVersion);
							currentVersion = EnumUpdateState.CURRENT;
							return;
						}
					}
				}
			}
			
			Core.log.warning("Using outdated version [" + VERSION + "] for Minecraft " + mcVersion + ". Consider updating.");
			currentVersion = EnumUpdateState.OUTDATED;
			
		} catch(Exception e){
			Core.log.warning("Unable to read from remote version authority.");
			Core.log.warning(e.toString());
			currentVersion = EnumUpdateState.CONNECTION_ERROR;
		}
	}
	
	public static String[] getChangelog(){
		if(cachedChangelog == null){
			cachedChangelog = grabChangelog(recommendedVersion);
		}
		
		return cachedChangelog;
	}
	
	public static String[] grabChangelog(String version){
		
		try{
			
			String location = REMOTE_CHANGELOG_ROOT + version + ".txt";
			HttpURLConnection conn = null;
			while(location != null && !location.isEmpty()){
				URL url = new URL(location);
				conn = (HttpURLConnection)url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
				conn.connect();
				location = conn.getHeaderField("Location");
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = null;
			ArrayList<String> changelog = new ArrayList<String>();
			while((line = reader.readLine()) != null){
				if(line.startsWith("#")){
					continue;
				}
				if(line.isEmpty()){
					continue;
				}
				
				changelog.add(line);
			}
			
			return changelog.toArray(new String[0]);
			
		} catch(Exception ex){
			ex.printStackTrace();
			Core.log.warning("Unable to read changelog from remote site.");
		}
		
		return new String[]{String.format("Unable to retrieve changelog for %s %s", DefaultProperties.MOD_NAME, version)};
	}
	
	@Override
	public void run(){
		
		int count = 0;
		currentVersion = null;
		
		Core.log.info("Beginning version check");
		
		try{
			while((count < 3) && ((currentVersion == null) || (currentVersion == EnumUpdateState.CONNECTION_ERROR))){
				versionCheck();
				count++;
				
				if(currentVersion == EnumUpdateState.CONNECTION_ERROR){
					Core.log.info("Version check attempt " + count + " failed, trying again in 10 seconds");
					Thread.sleep(10000);
				}
			}
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		
		if(currentVersion == EnumUpdateState.CONNECTION_ERROR){
			Core.log.info("Version check failed");
		}
		
	}
	
	public static void check(){
		
		new Thread(instance).start();
	}
	
}