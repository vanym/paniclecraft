package com.vanym.paniclecraft.core.component.advsign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    
    public static ChatStyle applyToStyle(ChatStyle style, EnumChatFormatting formatting) {
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
        return style;
    }
    
    public static ChatStyle toStyle(EnumChatFormatting formatting) {
        return applyToStyle(new ChatStyle(), formatting);
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
    
    public static ChatStyle invertBy(ChatStyle style, ChatStyle patch) {
        ChatStyle copy1 = style.createShallowCopy().setParentStyle(null);
        ChatStyle copy2 = style.createShallowCopy()
                               .setParentStyle(new ChatStyle().setColor(EnumChatFormatting.BLACK)
                                                              .setObfuscated(true)
                                                              .setBold(true)
                                                              .setStrikethrough(true)
                                                              .setUnderlined(true)
                                                              .setItalic(true));
        EnumChatFormatting patchColor = patch.getColor();
        if (copy1.getColor() == copy2.getColor()
            && patchColor != null
            && patchColor != EnumChatFormatting.RESET) {
            EnumChatFormatting color = style.getColor();
            if (color == patchColor) {
                style.setColor(EnumChatFormatting.RESET);
            } else if (color == null || color == EnumChatFormatting.RESET) {
                style.setColor(patchColor);
            }
        }
        if (copy1.getObfuscated() == copy2.getObfuscated() && patch.getObfuscated()) {
            style.setObfuscated(!style.getObfuscated());
        }
        if (copy1.getBold() == copy2.getBold() && patch.getBold()) {
            style.setBold(!style.getBold());
        }
        if (copy1.getStrikethrough() == copy2.getStrikethrough() && patch.getStrikethrough()) {
            style.setStrikethrough(!style.getStrikethrough());
        }
        if (copy1.getUnderlined() == copy2.getUnderlined() && patch.getUnderlined()) {
            style.setUnderlined(!style.getUnderlined());
        }
        if (copy1.getItalic() == copy2.getItalic() && patch.getItalic()) {
            style.setItalic(!style.getItalic());
        }
        return style;
    }
    
    public static ChatStyle invert(ChatStyle style) {
        return invertBy(style, new ChatStyle().setColor(EnumChatFormatting.RED)
                                              .setObfuscated(true)
                                              .setBold(true)
                                              .setStrikethrough(true)
                                              .setUnderlined(true)
                                              .setItalic(true));
    }
    
    @SuppressWarnings("unchecked")
    public static Stream<IChatComponent> stream(IChatComponent component) {
        return StreamSupport.stream((Spliterator<IChatComponent>)component.spliterator(), false);
    }
    
    public static Stream<IChatComponent> fragmentate(IChatComponent component) {
        return stream(component).flatMap(sub-> {
            ChatStyle style = sub.getChatStyle();
            String str = sub.getUnformattedTextForChat();
            return IntStream.range(0, str.length())
                            .mapToObj(str::charAt)
                            .map(String::valueOf)
                            .map(ChatComponentText::new)
                            .peek(comp->comp.setChatStyle(style.createDeepCopy()));
        });
    }
    
    public static IChatComponent substring(IChatComponent line, int beginIndex, int endIndex) {
        List<IChatComponent> list = fragmentate(line).collect(Collectors.toList());
        return toComponent(list.subList(beginIndex, endIndex));
    }
}
