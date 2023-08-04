package com.vanym.paniclecraft.datafix.fixes;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;

public class AdvSignSidesFix {
    
    public static void processSignTag(CompoundNBT sign) {
        if (sign.contains("FrontText")
            || sign.contains("BackText")
            || !sign.contains("Lines", 9)
            || !sign.contains("TextColor", 3)) {
            return;
        }
        AdvSignText frontText = new AdvSignText();
        List<ITextComponent> lines = frontText.getLines();
        lines.clear();
        ListNBT linesTag = sign.getList("Lines", 8);
        IntStream.range(0, linesTag.size())
                 .mapToObj(linesTag::getString)
                 .map(FormattingUtils::parseLine)
                 .forEachOrdered(lines::add);
        frontText.setTextColor(new Color(sign.getInt("TextColor"), true));
        sign.put("FrontText", frontText.serializeNBT());
        sign.put("BackText", frontText.serializeNBT());
        sign.remove("Lines");
        sign.remove("TextColor");
        if (sign.contains("Form") || !sign.contains("OnStick", 1)) {
            return;
        }
        sign.putInt("Form", sign.getBoolean("OnStick") ? 1 : 0); // AdvSignForm
        sign.remove("OnStick");
    }
}
