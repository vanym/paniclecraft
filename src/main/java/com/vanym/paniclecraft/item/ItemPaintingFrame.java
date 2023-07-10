package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemPaintingFrame extends ItemBlockMod3 {
    
    protected static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
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
    
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void onFuelBurnTime(net.minecraftforge.event.FuelBurnTimeEvent event) {
        ItemStack fuel = event.fuel;
        if (fuel.getItem() instanceof ItemPaintingFrame
            && Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
                     .map(side->ItemPaintingFrame.getPictureTag(fuel, side))
                     .anyMatch(Optional::isPresent)) {
            event.burnTime = 0;
            event.setResult(Event.Result.DENY);
        }
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
            for (ForgeDirection side : SIDE_ORDER) {
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
    
    public static ItemStack getItemWithPictures(Map<ForgeDirection, Picture> map) {
        ItemStack stack = new ItemStack(Core.instance.painting.itemPaintingFrame);
        if (map == null || map.isEmpty()) {
            return stack;
        }
        map.forEach((pside, picture)-> {
            NBTTagCompound pictureTag = Optional.ofNullable(picture)
                                                .map(Picture::serializeNBT)
                                                .orElseGet(NBTTagCompound::new);
            putPictureTag(stack, pside, pictureTag);
        });
        return stack;
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
    
    public static void putPictureTag(
            ItemStack stack,
            ForgeDirection pside,
            NBTTagCompound pictureTag) {
        putPictureTag(stack, pside.ordinal(), pictureTag);
    }
    
    public static void putPictureTag(ItemStack stack, int side, NBTTagCompound pictureTag) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getOrCreateBlockEntityTag(stack).setTag(name, pictureTag);
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, ForgeDirection pside) {
        return getPictureTag(stack, pside.ordinal());
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.hasKey(name, 10))
                        .map(tag->tag.getCompoundTag(name));
    }
    
    public static Optional<NBTTagCompound> removePictureTag(ItemStack stack, ForgeDirection pside) {
        return removePictureTag(stack, pside.ordinal());
    }
    
    public static Optional<NBTTagCompound> removePictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        Optional<NBTTagCompound> tagOpt = getPictureTag(stack, side);
        ItemUtils.getBlockEntityTag(stack).ifPresent(tag->tag.removeTag(name));
        ItemUtils.cleanBlockEntityTag(stack);
        return tagOpt;
    }
    
    public static void putPictureTagName(NBTTagCompound pictureTag, String name) {
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
