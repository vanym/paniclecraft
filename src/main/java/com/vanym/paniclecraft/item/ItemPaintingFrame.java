package com.vanym.paniclecraft.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

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
    
    public static final String TAG_PICTURE_N = TileEntityPaintingFrame.TAG_PICTURE_N;
    
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
        if (fuel.hasTag()) {
            CompoundNBT itemTag = fuel.getTag();
            if (SIDE_ORDER.stream()
                          .map(Direction::getIndex)
                          .map(ItemPaintingFrame::getPictureTag)
                          .anyMatch(itemTag::contains)) {
                return 0;
            }
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
            CompoundNBT itemTag = itemStack.getTag();
            for (int i = 0; i < SIDE_ORDER.size(); ++i) {
                Direction side = SIDE_ORDER.get(i);
                final String TAG_PICTURE_I = getPictureTag(side.ordinal());
                if (!itemTag.contains(TAG_PICTURE_I)) {
                    continue;
                }
                CompoundNBT pictureTag = itemTag.getCompound(TAG_PICTURE_I);
                String info;
                if (pictureTag.isEmpty()) {
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
                list.add(new StringTextComponent(sb.toString()));
            });
        }
    }
    
    public static ItemStack getItemWithPictures(Map<Direction, Picture> map) {
        ItemStack itemS = new ItemStack(Core.instance.painting.itemPaintingFrame);
        if (map == null) {
            return itemS;
        }
        CompoundNBT itemTag = new CompoundNBT();
        map.forEach((pside, picture)-> {
            final String TAG_PICTURE_I = getPictureTag(pside);
            CompoundNBT pictureTag = new CompoundNBT();
            if (picture != null) {
                picture.writeToNBT(pictureTag);
            }
            itemTag.put(TAG_PICTURE_I, pictureTag);
        });
        if (!itemTag.isEmpty()) {
            itemS.setTag(itemTag);
        }
        return itemS;
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
    
    public static String getPictureTag(Direction pside) {
        return getPictureTag(pside.ordinal());
    }
    
    public static String getPictureTag(int side) {
        return String.format(TAG_PICTURE_N, side);
    }
}
