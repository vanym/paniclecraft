package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

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
            ItemStack stack,
            @Nullable World world,
            List<String> list,
            ITooltipFlag flag) {
        if (stack.hasTagCompound()) {
            Map<String, String> mapLetters = new TreeMap<>();
            Map<String, Integer> mapCount = new TreeMap<>();
            for (SideName name : SideName.values()) {
                Optional<String> info =
                        ItemPaintingFrame.getPictureTag(stack, name.getSide())
                                         .map(ItemPainting::pictureSizeInformation);
                info.ifPresent(i-> {
                    mapCount.put(i, mapCount.getOrDefault(i, 0) + 1);
                    mapLetters.put(i, mapLetters.getOrDefault(i, "") + name.getLetter());
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
            putPictureTag(stack, pside, pictureTag);
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
    
    public static void putPictureTag(
            ItemStack stack,
            EnumFacing pside,
            NBTTagCompound pictureTag) {
        putPictureTag(stack, pside.getIndex(), pictureTag);
    }
    
    public static void putPictureTag(ItemStack stack, int side, NBTTagCompound pictureTag) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getOrCreateBlockEntityTag(stack).setTag(name, pictureTag);
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, EnumFacing pside) {
        return getPictureTag(stack, pside.getIndex());
    }
    
    public static Optional<NBTTagCompound> getPictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.hasKey(name, 10))
                        .map(tag->tag.getCompoundTag(name));
    }
    
    public static Optional<NBTTagCompound> removePictureTag(ItemStack stack, EnumFacing pside) {
        return removePictureTag(stack, pside.getIndex());
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
    
    public static enum SideName {
        FRONT(EnumFacing.NORTH, "F"),
        RIGHT(EnumFacing.WEST, "R"),
        TOP(EnumFacing.UP, "T"),
        LEFT(EnumFacing.EAST, "L"),
        BACK(EnumFacing.SOUTH, "K"),
        BOTTOM(EnumFacing.DOWN, "B");
        
        protected final EnumFacing side;
        protected final String letter;
        
        SideName(EnumFacing side, String letter) {
            this.side = side;
            this.letter = letter;
        }
        
        public EnumFacing getSide() {
            return this.side;
        }
        
        public String getLetter() {
            return this.letter;
        }
        
        public static Stream<SideName> stream() {
            return Arrays.stream(values());
        }
        
        public static SideName bySide(EnumFacing side) {
            return stream().filter(n->n.side == side).findAny().orElse(null);
        }
        
        public static SideName byName(String name) {
            return stream().filter(n->Stream.of(n.name(), n.side.name(), n.letter)
                                            .anyMatch(name::equalsIgnoreCase))
                           .findAny()
                           .orElse(null);
        }
    }
}
