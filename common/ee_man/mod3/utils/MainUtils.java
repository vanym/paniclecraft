package ee_man.mod3.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;

import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class MainUtils{
	public static int[] getRGBFromInt(int par1){
		return new int[]{((par1 >> 16) & 0xFF), ((par1 >> 8) & 0xFF), (par1 & 0xFF)};
	}
	
	public static int getIntFromRGB(int red, int green, int blue){
		int rgb = red;
		rgb = (rgb << 8) + green;
		rgb = (rgb << 8) + blue;
		return rgb;
	}
	
	public static Object[] getItemStackInfo(ItemStack item){
		if(item == null)
			return new Object[]{"null", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Boolean.valueOf(false)};
		else
			return new Object[]{item.getDisplayName(), Integer.valueOf(item.itemID), Integer.valueOf(item.getItemDamage()), Integer.valueOf(item.stackSize), Boolean.valueOf(item.hasTagCompound())};
	}
	
	public static boolean isPlayerOp(String playerName){
		MinecraftServer server = MinecraftServer.getServer();
		return playerName.equals(server.getServerOwner()) || server.getConfigurationManager().isPlayerOpped(playerName);
	}
	
	public static ArrayList<PicData> getPicsFromJar(String path){
		ArrayList<PicData> pics = new ArrayList<PicData>();
		
		Properties names = new Properties();
		InputStream namesStream = MainUtils.class.getResourceAsStream(path + "/names.properties");
		if(namesStream != null){
			try{
				names.load(namesStream);
			} catch(IOException e){
			}
		}
		
		URL fileLink = null;
		int i = 0;
		do{
			fileLink = MainUtils.class.getResource(path + "/" + Integer.toString(i) + ".jpg");
			if(fileLink != null){
				PicData bufPic = getPicDataByURL(fileLink);
				if(bufPic != null)
					bufPic.name = names.getProperty(Integer.toString(i));
				pics.add(bufPic);
			}
			i++;
		} while(fileLink != null);
		
		return pics;
	}
	
	public static void copyObjectDataOnAllSuperClasses(Class<? extends Object> objectClass, Object from, Object to){
		Class<? extends Object> notFirstObjectClass = objectClass;
		while(notFirstObjectClass != null){
			copyObjectDataOnOnlyFirstClass(notFirstObjectClass, from, to);
			notFirstObjectClass = notFirstObjectClass.getSuperclass();
		}
	}
	
	public static void copyObjectDataOnOnlyFirstClass(Class<? extends Object> objectClass, Object from, Object to){
		Field[] fields = objectClass.getDeclaredFields();
		for(Field f : fields){
			int mods = f.getModifiers();
			f.setAccessible(true);
			try{
				if(!Modifier.isStatic(mods))
					f.set(to, f.get(from));
			} catch(IllegalArgumentException e){
			} catch(IllegalAccessException e){
			}
		}
	}
	
	public static PicData getPicDataByURL(URL fileLink){
		BufferedImage img = null;
		try{
			img = ImageIO.read(fileLink);
		} catch(IOException e){
		}
		
		if(img == null ? true : (img.getWidth() != img.getHeight() || img.getWidth() > 100))
			return null;
		int row = img.getWidth();
		
		byte[] tempByte = new byte[img.getHeight() * img.getWidth() * 3];
		
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				Object pixelIm = img.getRaster().getDataElements(x, y, null);
				int i = (y * img.getHeight() + x) * 3;
				tempByte[i + 0] = (byte)img.getColorModel().getRed(pixelIm);
				tempByte[i + 1] = (byte)img.getColorModel().getGreen(pixelIm);
				tempByte[i + 2] = (byte)img.getColorModel().getBlue(pixelIm);
			}
		}
		
		return new PicData(tempByte, row);
	}
	
	public static class PicData{
		public byte[] byteArray;
		public int row;
		public String name;
		
		public PicData(){
		}
		
		public PicData(byte[] par1, int par2){
			this.row = par2;
			this.byteArray = par1;
		}
	}
}
