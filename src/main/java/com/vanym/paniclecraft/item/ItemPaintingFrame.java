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
import com.vanym.paniclecraft.client.renderer.item.ItemRendererPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
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
    
    public ItemPaintingFrame(Block block) {
        super(block, Props.create().setTEISR(()->ItemRendererPaintingFrame::create));
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
            ItemStack stack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag) {
        if (stack.hasTag()) {
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
            putPictureTag(stack, pside, pictureTag);
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
    
    public static void putPictureTag(
            ItemStack stack,
            Direction pside,
            CompoundNBT pictureTag) {
        putPictureTag(stack, pside.getIndex(), pictureTag);
    }
    
    public static void putPictureTag(ItemStack stack, int side, CompoundNBT pictureTag) {
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
    
    public static Optional<CompoundNBT> removePictureTag(ItemStack stack, Direction pside) {
        return removePictureTag(stack, pside.getIndex());
    }
    
    public static Optional<CompoundNBT> removePictureTag(ItemStack stack, int side) {
        String name = String.format(TAG_PICTURE_N, side);
        Optional<CompoundNBT> tagOpt = getPictureTag(stack, side);
        ItemUtils.getBlockEntityTag(stack).ifPresent(tag->tag.remove(name));
        ItemUtils.cleanBlockEntityTag(stack);
        return tagOpt;
    }
    
    public static void putPictureTagName(CompoundNBT pictureTag, String name) {
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
    
    public static enum SideName {
        FRONT(Direction.NORTH, "F"),
        RIGHT(Direction.WEST, "R"),
        TOP(Direction.UP, "T"),
        LEFT(Direction.EAST, "L"),
        BACK(Direction.SOUTH, "K"),
        BOTTOM(Direction.DOWN, "B");
        
        protected final Direction side;
        protected final String letter;
        
        SideName(Direction side, String letter) {
            this.side = side;
            this.letter = letter;
        }
        
        public Direction getSide() {
            return this.side;
        }
        
        public String getLetter() {
            return this.letter;
        }
        
        public static Stream<SideName> stream() {
            return Arrays.stream(values());
        }
        
        public static SideName bySide(Direction side) {
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
