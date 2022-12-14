package com.vanym.paniclecraft.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;

import net.minecraft.util.AxisAlignedBB;

public class MainUtils {
    
    public static AxisAlignedBB getBoundsBySize(int side, double width) {
        double minX = 0.0D, maxX = 1.0D, minY = 0.0D, maxY = 1.0D, minZ = 0.0D, maxZ = 1.0D;
        switch (side) {
            case 0:
                maxY = width;
            break;
            case 1:
                minY = 1.0D - width;
            break;
            case 2:
                maxZ = width;
            break;
            case 3:
                minZ = 1.0D - width;
            break;
            case 4:
                maxX = width;
            break;
            case 5:
                minX = 1.0D - width;
            break;
        }
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public static boolean isTouchingSide(int side, AxisAlignedBB box) {
        if (box == null) {
            return false;
        }
        switch (side) {
            case 0:
                return box.minY <= 0.0D;
            case 1:
                return box.maxY >= 1.0D;
            case 2:
                return box.minZ <= 0.0D;
            case 3:
                return box.maxZ >= 1.0D;
            case 4:
                return box.minX <= 0.0D;
            case 5:
                return box.maxX >= 1.0D;
        }
        return false;
    }
    
    // public static int[] getRGBFromInt(int par1){
    // return new int[]{((par1 >> 16) & 0xFF), ((par1 >> 8) & 0xFF), (par1 & 0xFF)};
    // }
    
    public static Color getColorFromInt(int par1) {
        return new Color(par1);
    }
    
    public static int getIntFromRGB(int red, int green, int blue) {
        int rgb = red;
        rgb = (rgb << 8) + green;
        rgb = (rgb << 8) + blue;
        return rgb;
    }
    
    // public static boolean isPlayerOp(String playerName){
    // return true;
    // MinecraftServer server = MinecraftServer.getServer();
    // return playerName.equals(server.getServerOwner()) ||
    // server.getConfigurationManager().isPlayerOpped(playerName);
    // }
    
    public static ArrayList<PicData> getPicsFromJar(String path) {
        ArrayList<PicData> pics = new ArrayList<>();
        
        Properties names = new Properties();
        InputStream namesStream = MainUtils.class.getResourceAsStream(path + "/names.properties");
        if (namesStream != null) {
            try {
                names.load(namesStream);
            } catch (IOException e) {
            }
        }
        
        URL fileLink = null;
        int i = 0;
        do {
            fileLink = MainUtils.class.getResource(path + "/" + Integer.toString(i) + ".jpg");
            if (fileLink != null) {
                PicData bufPic = getPicDataByURL(fileLink);
                if (bufPic != null) {
                    bufPic.name = names.getProperty(Integer.toString(i));
                }
                pics.add(bufPic);
            }
            i++;
        } while (fileLink != null);
        
        return pics;
    }
    
    public static void copyObjectDataOnAllSuperClasses(
            Class<? extends Object> objectClass,
            Object from,
            Object to) {
        Class<? extends Object> notFirstObjectClass = objectClass;
        while (notFirstObjectClass != null) {
            copyObjectDataOnOnlyFirstClass(notFirstObjectClass, from, to);
            notFirstObjectClass = notFirstObjectClass.getSuperclass();
        }
    }
    
    public static void copyObjectDataOnOnlyFirstClass(
            Class<? extends Object> objectClass,
            Object from,
            Object to) {
        Field[] fields = objectClass.getDeclaredFields();
        for(Field f : fields) {
            int mods = f.getModifiers();
            f.setAccessible(true);
            try {
                if (!Modifier.isStatic(mods)) {
                    f.set(to, f.get(from));
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }
    
    public static PicData getPicDataByURL(URL fileLink) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(fileLink);
        } catch (IOException e) {
        }
        
        if (img == null ? true : (img.getWidth() != img.getHeight() || img.getWidth() > 100)) {
            return null;
        }
        int row = img.getWidth();
        
        byte[] tempByte = new byte[img.getHeight() * img.getWidth() * 3];
        
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                Object pixelIm = img.getRaster().getDataElements(x, y, null);
                int i = (y * img.getHeight() + x) * 3;
                tempByte[i + 0] = (byte)img.getColorModel().getRed(pixelIm);
                tempByte[i + 1] = (byte)img.getColorModel().getGreen(pixelIm);
                tempByte[i + 2] = (byte)img.getColorModel().getBlue(pixelIm);
            }
        }
        
        return new PicData(tempByte, row);
    }
    
    public static class PicData {
        public byte[] byteArray;
        public int row;
        public String name;
        
        public PicData() {
        }
        
        public PicData(byte[] par1, int par2) {
            this.row = par2;
            this.byteArray = par1;
        }
    }
}
