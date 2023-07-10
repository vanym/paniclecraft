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
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPaintingFrame extends BlockItem {
    
    protected static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
    public static final Direction FRONT;
    public static final Direction LEFT;
    public static final Direction BACK;
    public static final Direction RIGHT;
    public static final Direction BOTTOM;
    public static final Direction TOP;
    
    static {
        FRONT = Direction.NORTH;
        LEFT = FRONT.rotateY();
        BACK = LEFT.rotateY();
        RIGHT = BACK.rotateY();
        BOTTOM = Direction.DOWN;
        TOP = Direction.UP;
        
        TreeMap<Direction, String> letters = new TreeMap<>();
        letters.put(FRONT, "F");
        letters.put(LEFT, "L");
        letters.put(BACK, "K");
        letters.put(RIGHT, "R");
        letters.put(BOTTOM, "B");
        letters.put(TOP, "T");
        SIDE_LETTERS = Collections.unmodifiableMap(letters);
    }
    
    public static final List<Direction> SIDE_ORDER =
            Arrays.asList(FRONT, RIGHT, TOP, LEFT, BACK, BOTTOM);
    protected static final Map<Direction, String> SIDE_LETTERS;
    
    public ItemPaintingFrame(Block block) {
        super(block, new Item.Properties().group(Core.instance.tab)
                                          .setTEISR(()->ItemRendererPaintingFrame::create));
        this.setRegistryName(block.getRegistryName());
    }
    
    @Override
    @Nullable
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
    
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (Arrays.stream(Direction.values())
                  .map(side->ItemPaintingFrame.getPictureTag(fuel, side))
                  .anyMatch(Optional::isPresent)) {
            return 0;
        }
        return -1;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemStack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        if (itemStack.hasTag()) {
            Map<String, String> mapLetters = new TreeMap<>();
            Map<String, Integer> mapCount = new TreeMap<>();
            for (Direction side : SIDE_ORDER) {
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
                list.add(new StringTextComponent(sb.toString()));
            });
        }
    }
    
    public static ItemStack getItemWithPictures(Map<Direction, Picture> map) {
        ItemStack stack = new ItemStack(Core.instance.painting.itemPaintingFrame);
        if (map == null || map.isEmpty()) {
            return stack;
        }
        map.forEach((pside, picture)-> {
            CompoundNBT pictureTag = Optional.ofNullable(picture)
                                             .map(Picture::serializeNBT)
                                             .orElseGet(CompoundNBT::new);
            setPictureTag(stack, pside, pictureTag);
        });
        return stack;
    }
    
    public static ItemStack getFrameAsItem(ISidePictureProvider provider) {
        if (provider == null) {
            return getItemWithPictures(null);
        }
        Map<Direction, Picture> map = new HashMap<>();
        for (int i = 0; i < ISidePictureProvider.N; i++) {
            Picture picture = provider.getPicture(i);
            if (picture == null) {
                continue;
            }
            Direction pside = Direction.byIndex(i);
            map.put(pside, picture);
        }
        return getItemWithPictures(map);
    }
    
    public static ItemStack getItemWithEmptyPictures(Direction... psides) {
        if (psides == null) {
            return getItemWithPictures(null);
        }
        Map<Direction, Picture> map = new HashMap<>();
        for (Direction pside : psides) {
            map.put(pside, null);
        }
        return getItemWithPictures(map);
    }
    
    public static void setPictureTag(
            ItemStack stack,
            Direction pside,
            CompoundNBT pictureTag) {
        setPictureTag(stack, pside.getIndex(), pictureTag);
    }
    
    public static void setPictureTag(ItemStack stack, int side, CompoundNBT pictureTag) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getOrCreateBlockEntityTag(stack).put(name, pictureTag);
    }
    
    public static Optional<CompoundNBT> getPictureTag(ItemStack stack, Direction pside) {
        return getPictureTag(stack, pside.getIndex());
    }
    
    public static Optional<CompoundNBT> getPictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        return ItemUtils.getBlockEntityTag(stack)
                        .filter(tag->tag.contains(name, 10))
                        .map(tag->tag.getCompound(name));
    }
    
    public static void removePictureTag(ItemStack stack, Direction pside) {
        removePictureTag(stack, pside.getIndex());
    }
    
    public static void removePictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        ItemUtils.getBlockEntityTag(stack).ifPresent(tag->tag.remove(name));
        ItemUtils.cleanBlockEntityTag(stack);
    }
    
    public static void setPictureTagName(CompoundNBT pictureTag, String name) {
        pictureTag.putString(Picture.TAG_NAME, name);
    }
    
    public static Optional<String> removePictureTagName(CompoundNBT pictureTag) {
        if (!pictureTag.contains(Picture.TAG_NAME, 8)) {
            return Optional.empty();
        }
        String name = pictureTag.getString(Picture.TAG_NAME);
        pictureTag.remove(Picture.TAG_NAME);
        return Optional.of(name);
    }
}
