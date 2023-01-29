package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemPaintingFrame extends ItemBlock {
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    public static final ForgeDirection FRONT;
    public static final ForgeDirection LEFT;
    public static final ForgeDirection BACK;
    public static final ForgeDirection RIGHT;
    public static final ForgeDirection BOTTOM;
    public static final ForgeDirection TOP;
    
    static {
        FRONT = ForgeDirection.NORTH;
        LEFT = FRONT.getRotation(ForgeDirection.UP);
        BACK = LEFT.getRotation(ForgeDirection.UP);
        RIGHT = BACK.getRotation(ForgeDirection.UP);
        BOTTOM = ForgeDirection.DOWN;
        TOP = ForgeDirection.UP;
        
        TreeMap<ForgeDirection, String> letters = new TreeMap<>();
        letters.put(FRONT, "F");
        letters.put(LEFT, "L");
        letters.put(BACK, "K");
        letters.put(RIGHT, "R");
        letters.put(BOTTOM, "B");
        letters.put(TOP, "T");
        SIDE_LETTERS = Collections.unmodifiableMap(letters);
    }
    
    public static final List<ForgeDirection> SIDE_ORDER =
            Arrays.asList(FRONT, RIGHT, TOP, LEFT, BACK, BOTTOM);
    protected static final Map<ForgeDirection, String> SIDE_LETTERS;
    
    public ItemPaintingFrame(Block block) {
        super(block);
    }
    
    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        if (armorType == 0) {
            return true;
        }
        return super.isValidArmor(stack, armorType, entity);
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            EntityPlayer entityPlayer,
            List list,
            boolean advancedItemTooltips) {
        if (itemStack.hasTagCompound()) {
            Map<String, String> mapLetters = new TreeMap<>();
            Map<String, Integer> mapCount = new TreeMap<>();
            NBTTagCompound itemTag = itemStack.getTagCompound();
            for (int i = 0; i < SIDE_ORDER.size(); ++i) {
                ForgeDirection side = SIDE_ORDER.get(i);
                final String TAG_PICTURE_I = getPictureTag(side.ordinal());
                if (!itemTag.hasKey(TAG_PICTURE_I)) {
                    continue;
                }
                NBTTagCompound pictureTag = itemTag.getCompoundTag(TAG_PICTURE_I);
                String info;
                if (pictureTag.hasNoTags()) {
                    info = "";
                } else {
                    info = ItemPainting.pictureSizeInformation(pictureTag);
                }
                mapCount.put(info, mapCount.getOrDefault(info, 0) + 1);
                mapLetters.put(info, mapLetters.getOrDefault(info, "") + SIDE_LETTERS.get(side));
            }
            Map mapInfo;
            if (Core.instance.painting.clientConfig.paintingFrameInfoSideLetters) {
                mapInfo = mapLetters;
            } else {
                mapInfo = mapCount;
            }
            mapInfo.forEach((info, count)-> {
                StringBuilder sb = new StringBuilder();
                sb.append(info);
                sb.append("×");
                sb.append(count);
                list.add(sb.toString());
            });
        }
    }
    
    public static ItemStack getItemWithPictures(Map<ForgeDirection, Picture> map) {
        ItemStack itemS = new ItemStack(Core.instance.painting.itemPaintingFrame);
        if (map == null) {
            return itemS;
        }
        NBTTagCompound itemTag = new NBTTagCompound();
        map.forEach((pside, picture)-> {
            final String TAG_PICTURE_I = getPictureTag(pside);
            NBTTagCompound pictureTag = new NBTTagCompound();
            if (picture != null) {
                picture.writeToNBT(pictureTag);
            }
            itemTag.setTag(TAG_PICTURE_I, pictureTag);
        });
        if (!itemTag.hasNoTags()) {
            itemS.setTagCompound(itemTag);
        }
        return itemS;
    }
    
    public static ItemStack getFrameAsItem(ISidePictureProvider provider) {
        if (provider == null) {
            return getItemWithPictures(null);
        }
        Map<ForgeDirection, Picture> map = new HashMap<>();
        for (int i = 0; i < ISidePictureProvider.N; i++) {
            Picture picture = provider.getPicture(i);
            if (picture == null) {
                continue;
            }
            ForgeDirection pside = ForgeDirection.getOrientation(i);
            map.put(pside, picture);
        }
        return getItemWithPictures(map);
    }
    
    public static ItemStack getItemWithEmptyPictures(ForgeDirection... psides) {
        if (psides == null) {
            return getItemWithPictures(null);
        }
        Map<ForgeDirection, Picture> map = new HashMap<>();
        for (ForgeDirection pside : psides) {
            map.put(pside, null);
        }
        return getItemWithPictures(map);
    }
    
    public static String getPictureTag(ForgeDirection pside) {
        return getPictureTag(pside.ordinal());
    }
    
    public static String getPictureTag(int side) {
        return String.format(TAG_PICTURE_N, side);
    }
}
