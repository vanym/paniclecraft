package com.vanym.paniclecraft.datafix.fixes;

import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;

public class BlockEntityTagFix {
    
    public static final int VERSION = 2;
    
    protected static CompoundNBT fixSignSlotTag(CompoundNBT slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:advanced_sign".equals(t.getString("id")))
                .map(t->t.getCompound("tag"))
                .filter(t->t.contains("Sign", 10))
                .ifPresent(t-> {
                    t.put("BlockEntityTag", t.get("Sign"));
                    t.remove("Sign");
                });
        return slot;
    }
    
    protected static CompoundNBT fixPaintingSlotTag(CompoundNBT slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:painting".equals(t.getString("id")))
                .map(t->t.getCompound("tag"))
                .filter(t->t.contains("Picture", 10))
                .ifPresent(t-> {
                    CompoundNBT blockTag = new CompoundNBT();
                    blockTag.put("Picture", t.get("Picture"));
                    t.put("BlockEntityTag", blockTag);
                    t.remove("Picture");
                });
        return slot;
    }
    
    protected static CompoundNBT fixFrameSlotTag(CompoundNBT slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:paintingframe".equals(t.getString("id")))
                .map(t->t.getCompound("tag"))
                .ifPresent(t-> {
                    CompoundNBT blockTag = new CompoundNBT();
                    for (int i = 0; i < 6; ++i) {
                        String name = String.format("Picture[%d]", i);
                        if (t.contains(name, 10)) {
                            blockTag.put(name, t.get(name));
                            t.remove(name);
                        }
                    }
                    if (!blockTag.isEmpty()) {
                        t.put("BlockEntityTag", blockTag);
                    }
                });
        return slot;
    }
    
    protected static CompoundNBT fixChessSlotTag(CompoundNBT slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:chess_desk".equals(t.getString("id")))
                .map(t->t.getCompound("tag"))
                .filter(t->t.contains("Moves", 9))
                .ifPresent(t-> {
                    CompoundNBT blockTag = new CompoundNBT();
                    blockTag.put("Moves", t.get("Moves"));
                    t.put("BlockEntityTag", blockTag);
                    t.remove("Moves");
                });
        return slot;
    }
}
