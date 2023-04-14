package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintingFrame extends ItemBlock {
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    public static final EnumFacing FRONT;
    public static final EnumFacing LEFT;
    public static final EnumFacing BACK;
    public static final EnumFacing RIGHT;
    public static final EnumFacing BOTTOM;
    public static final EnumFacing TOP;
    
    static {
        FRONT = EnumFacing.NORTH;
        LEFT = FRONT.rotateY();
        BACK = LEFT.rotateY();
        RIGHT = BACK.rotateY();
        BOTTOM = EnumFacing.DOWN;
        TOP = EnumFacing.UP;
        
        TreeMap<EnumFacing, String> letters = new TreeMap<>();
        letters.put(FRONT, "F");
        letters.put(LEFT, "L");
        letters.put(BACK, "K");
        letters.put(RIGHT, "R");
        letters.put(BOTTOM, "B");
        letters.put(TOP, "T");
        SIDE_LETTERS = Collections.unmodifiableMap(letters);
    }
    
    public static final List<EnumFacing> SIDE_ORDER =
            Arrays.asList(FRONT, RIGHT, TOP, LEFT, BACK, BOTTOM);
    protected static final Map<EnumFacing, String> SIDE_LETTERS;
    
    public ItemPaintingFrame(Block block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }
    
    @Override
    @Nullable
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }
    
    @Override
    public int getItemBurnTime(ItemStack fuel) {
        if (fuel.hasTagCompound()) {
            NBTTagCompound itemTag = fuel.getTagCompound();
            if (SIDE_ORDER.stream()
                          .map(EnumFacing::getIndex)
                          .map(ItemPaintingFrame::getPictureTag)
                          .anyMatch(itemTag::hasKey)) {
                return 0;
            }
        }
        return -1;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        if (itemStack.hasTagCompound()) {
            Map<String, String> mapLetters = new TreeMap<>();
            Map<String, Integer> mapCount = new TreeMap<>();
            NBTTagCompound itemTag = itemStack.getTagCompound();
            for (int i = 0; i < SIDE_ORDER.size(); ++i) {
                EnumFacing side = SIDE_ORDER.get(i);
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
    
    public static ItemStack getItemWithPictures(Map<EnumFacing, Picture> map) {
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
        Map<EnumFacing, Picture> map = new HashMap<>();
        for (int i = 0; i < ISidePictureProvider.N; i++) {
            Picture picture = provider.getPicture(i);
            if (picture == null) {
                continue;
            }
            EnumFacing pside = EnumFacing.getFront(i);
            map.put(pside, picture);
        }
        return getItemWithPictures(map);
    }
    
    public static ItemStack getItemWithEmptyPictures(EnumFacing... psides) {
        if (psides == null) {
            return getItemWithPictures(null);
        }
        Map<EnumFacing, Picture> map = new HashMap<>();
        for (EnumFacing pside : psides) {
            map.put(pside, null);
        }
        return getItemWithPictures(map);
    }
    
    public static String getPictureTag(EnumFacing pside) {
        return getPictureTag(pside.ordinal());
    }
    
    public static String getPictureTag(int side) {
        return String.format(TAG_PICTURE_N, side);
    }
}
