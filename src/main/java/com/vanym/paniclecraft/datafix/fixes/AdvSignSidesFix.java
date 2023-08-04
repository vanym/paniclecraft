package com.vanym.paniclecraft.datafix.fixes;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;

public class AdvSignSidesFix {
    
    public static void processSignTag(NBTTagCompound sign) {
        if (sign.hasKey("FrontText")
            || sign.hasKey("BackText")
            || !sign.hasKey("Lines", 9)
            || !sign.hasKey("TextColor", 3)) {
            return;
        }
        AdvSignText frontText = new AdvSignText();
        List<IChatComponent> lines = frontText.getLines();
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
}
