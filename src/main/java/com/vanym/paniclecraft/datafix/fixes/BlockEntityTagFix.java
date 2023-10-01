package com.vanym.paniclecraft.datafix.fixes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vanym.paniclecraft.datafix.DataFix;
import com.vanym.paniclecraft.datafix.ITypedFixableData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;

public class BlockEntityTagFix {
    
    public static final int VERSION = 2;
    
    protected static NBTTagCompound fixSignSlotTag(NBTTagCompound slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:advanced_sign".equals(t.getString("id")))
                .map(t->t.getCompoundTag("tag"))
                .filter(t->t.hasKey("Sign", 10))
                .ifPresent(t-> {
                    t.setTag("BlockEntityTag", t.getTag("Sign"));
                    t.removeTag("Sign");
                });
        return slot;
    }
    
    protected static NBTTagCompound fixPaintingSlotTag(NBTTagCompound slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:painting".equals(t.getString("id")))
                .map(t->t.getCompoundTag("tag"))
                .filter(t->t.hasKey("Picture", 10))
                .ifPresent(t-> {
                    NBTTagCompound blockTag = new NBTTagCompound();
                    blockTag.setTag("Picture", t.getTag("Picture"));
                    t.setTag("BlockEntityTag", blockTag);
                    t.removeTag("Picture");
                });
        return slot;
    }
    
    protected static NBTTagCompound fixFrameSlotTag(NBTTagCompound slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:paintingframe".equals(t.getString("id")))
                .map(t->t.getCompoundTag("tag"))
                .ifPresent(t-> {
                    NBTTagCompound blockTag = new NBTTagCompound();
                    for (int i = 0; i < 6; ++i) {
                        String name = String.format("Picture[%d]", i);
                        if (t.hasKey(name, 10)) {
                            blockTag.setTag(name, t.getTag(name));
                            t.removeTag(name);
                        }
                    }
                    if (!blockTag.hasNoTags()) {
                        t.setTag("BlockEntityTag", blockTag);
                    }
                });
        return slot;
    }
    
    protected static NBTTagCompound fixChessSlotTag(NBTTagCompound slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:chess_desk".equals(t.getString("id")))
                .map(t->t.getCompoundTag("tag"))
                .filter(t->t.hasKey("Moves", 9))
                .ifPresent(t-> {
                    NBTTagCompound blockTag = new NBTTagCompound();
                    blockTag.setTag("Moves", t.getTag("Moves"));
                    t.setTag("BlockEntityTag", blockTag);
                    t.removeTag("Moves");
                });
        return slot;
    }
    
    public static List<ITypedFixableData> advsign() {
        return Arrays.asList(DataFix.create(VERSION, FixTypes.ITEM_INSTANCE,
                                            BlockEntityTagFix::fixSignSlotTag));
    }
    
    public static List<ITypedFixableData> painting() {
        return Arrays.asList(DataFix.create(VERSION, FixTypes.ITEM_INSTANCE,
                                            BlockEntityTagFix::fixPaintingSlotTag),
                             DataFix.create(VERSION, FixTypes.ITEM_INSTANCE,
                                            BlockEntityTagFix::fixFrameSlotTag));
    }
    
    public static List<ITypedFixableData> deskgame() {
        return Arrays.asList(DataFix.create(VERSION, FixTypes.ITEM_INSTANCE,
                                            BlockEntityTagFix::fixChessSlotTag));
    }
}
