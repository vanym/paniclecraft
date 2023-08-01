package com.vanym.paniclecraft.core.component.advsign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class FormattingUtils {
    
    public static IChatComponent parseLine(String line) {
        Matcher matcher = EnumChatFormatting.formattingCodePattern.matcher(line);
        List<IChatComponent> list = new ArrayList<>();
        ChatStyle style = new ChatStyle();
        for (int last = 0;;) {
            boolean find = matcher.find();
            String sub = find ? line.substring(last, matcher.start()) : line.substring(last);
            if (!sub.isEmpty()) {
                ChatStyle copy = style.createShallowCopy();
                int index = list.size() - 1;
                if (index >= 0 && list.get(index).getChatStyle().equals(copy)) {
                    String begin = list.get(index).getUnformattedTextForChat();
                    list.set(index, new ChatComponentText(begin + sub).setChatStyle(copy));
                } else {
                    list.add(new ChatComponentText(sub).setChatStyle(copy));
                }
            }
            if (find) {
                last = matcher.end();
                applyToStyle(style, byCode(matcher.group().charAt(1)));
            } else {
                break;
            }
        }
        return toComponent(list);
    }
    
    public static IChatComponent toComponent(List<IChatComponent> list) {
        if (list.isEmpty()) {
            return new ChatComponentText("");
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            IChatComponent root = new ChatComponentText("");
            list.forEach(root::appendSibling);
            return root;
        }
    }
    
    public static EnumChatFormatting byCode(char code) {
        char lower = Character.toLowerCase(code);
        return Arrays.stream(EnumChatFormatting.values())
                     .filter(f->f.getFormattingCode() == lower)
                     .findAny()
                     .orElse(null);
    }
    
    public static void applyToStyle(ChatStyle style, EnumChatFormatting formatting) {
        switch (formatting) {
            case RESET:
                style.setBold(null)
                     .setItalic(null)
                     .setObfuscated(null)
                     .setStrikethrough(null)
                     .setUnderlined(null)
                     .setColor(null);
            break;
            case BOLD:
                style.setBold(true);
            break;
            case ITALIC:
                style.setItalic(true);
            break;
            case OBFUSCATED:
                style.setObfuscated(true);
            break;
            case STRIKETHROUGH:
                style.setStrikethrough(true);
            break;
            case UNDERLINE:
                style.setUnderlined(true);
            break;
            default:
                style.setColor(formatting);
            break;
        }
    }
    
    public static String trimReset(String str) {
        String r = EnumChatFormatting.RESET.toString();
        if (str.startsWith(r)) {
            str = str.substring(r.length());
        }
        if (str.endsWith(r)) {
            str = str.substring(0, str.length() - r.length());
        }
        return str;
    }
}
