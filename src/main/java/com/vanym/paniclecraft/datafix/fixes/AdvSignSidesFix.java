package com.vanym.paniclecraft.datafix.fixes;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.datafix.DataFix;
import com.vanym.paniclecraft.datafix.ITypedFixableData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.text.ITextComponent;

public class AdvSignSidesFix {
    
    public static final int VERSION = 1;
    
    protected static void processSignTag(NBTTagCompound sign) {
        if (sign.hasKey("FrontText")
            || sign.hasKey("BackText")
            || !sign.hasKey("Lines", 9)
            || !sign.hasKey("TextColor", 3)) {
            return;
        }
        AdvSignText frontText = new AdvSignText();
        List<ITextComponent> lines = frontText.getLines();
        lines.clear();
        NBTTagList linesTag = sign.getTagList("Lines", 8);
        IntStream.range(0, linesTag.tagCount())
                 .mapToObj(linesTag::getStringTagAt)
                 .map(FormattingUtils::parseLine)
                 .forEachOrdered(lines::add);
        frontText.setTextColor(new Color(sign.getInteger("TextColor"), true));
        sign.setTag("FrontText", frontText.serializeNBT());
        sign.setTag("BackText", frontText.serializeNBT());
        sign.removeTag("Lines");
        sign.removeTag("TextColor");
        if (sign.hasKey("Form") || !sign.hasKey("OnStick", 1)) {
            return;
        }
        sign.setInteger("Form", sign.getBoolean("OnStick") ? 1 : 0); // AdvSignForm
        sign.removeTag("OnStick");
    }
    
    protected static NBTTagCompound fixBlockEntityTag(NBTTagCompound data) {
        Optional.of(data)
                .filter(t->"paniclecraft:advanced_sign".equals(t.getString("id")))
                .ifPresent(AdvSignSidesFix::processSignTag);
        return data;
    }
    
    protected static NBTTagCompound fixSlotTag(NBTTagCompound slot) {
        Optional.of(slot)
                .filter(t->"paniclecraft:advanced_sign".equals(t.getString("id")))
                .map(t->t.getCompoundTag("tag"))
                .map(t->t.getCompoundTag("Sign"))
                .filter(t->!t.hasNoTags())
                .ifPresent(AdvSignSidesFix::processSignTag);
        return slot;
    }
    
    public static List<ITypedFixableData> all() {
        return Arrays.asList(DataFix.create(VERSION, FixTypes.BLOCK_ENTITY,
                                            AdvSignSidesFix::fixBlockEntityTag),
                             DataFix.create(VERSION, FixTypes.ITEM_INSTANCE,
                                            AdvSignSidesFix::fixSlotTag));
    }
}
