package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintingFrame extends ItemBlockMod3 {
    
    protected static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
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
        if (Arrays.stream(EnumFacing.VALUES)
                  .map(side->ItemPaintingFrame.getPictureTag(fuel, side))
                  .anyMatch(Optional::isPresent)) {
            return 0;
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
            for (EnumFacing side : SIDE_ORDER) {
                Optional<String> info =
                        getPictureTag(itemStack, side).map(ItemPainting::pictureSizeInformation);
                info.ifPresent(i-> {
                    mapCount.put(i, mapCount.getOrDefault(i, 0) + 1);
                    mapLetters.put(i, mapLetters.getOrDefault(i, "") + SIDE_LETTERS.get(side));
                });
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
        ItemStack stack = new ItemStack(Core.instance.painting.itemPaintingFrame);
        if (map == null || map.isEmpty()) {
            return stack;
        }
        map.forEach((pside, picture)-> {
            NBTTagCompound pictureTag = Optional.ofNullable(picture)
                                                .map(Picture::serializeNBT)
                                                .orElseGet(NBTTagCompound::new);
            setPictureTag(stack, pside, pictureTag);
        });
        return stack;
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
    
    public static void setPictureTag(
            ItemStack stack,
            EnumFacing pside,
            NBTTagCompound pictureTag) {
        setPictureTag(stack, pside.ordinal(), pictureTag);
    }
    
    public static void setPictureTag(ItemStack stack, int side, NBTTagCompound pictureTag) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getOrCreateBlockEntityTag(stack).setTag(name, pictureTag);
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, EnumFacing pside) {
        return getPictureTag(stack, pside.ordinal());
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.hasKey(name, 10))
                        .map(tag->tag.getCompoundTag(name));
    }
    
    public static void removePictureTag(ItemStack stack, EnumFacing pside) {
        removePictureTag(stack, pside.ordinal());
    }
    
    public static void removePictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getBlockEntityTag(stack).ifPresent(tag->tag.removeTag(name));
        ItemUtils.cleanBlockEntityTag(stack);
    }
    
    public static void setPictureTagName(NBTTagCompound pictureTag, String name) {
        pictureTag.setString(Picture.TAG_NAME, name);
    }
    
    public static Optional<String> removePictureTagName(NBTTagCompound pictureTag) {
        if (!pictureTag.hasKey(Picture.TAG_NAME, 8)) {
            return Optional.empty();
        }
        String name = pictureTag.getString(Picture.TAG_NAME);
        pictureTag.removeTag(Picture.TAG_NAME);
        return Optional.of(name);
    }
}
