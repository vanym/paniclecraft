package com.vanym.paniclecraft.core.component.advsign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class FormattingUtils {
    
    public static ITextComponent parseLine(String line) {
        Matcher matcher = TextFormatting.STRIP_FORMATTING_PATTERN.matcher(line);
        List<ITextComponent> list = new ArrayList<>();
        Style style = new Style();
        for (int last = 0;;) {
            boolean find = matcher.find();
            String sub = find ? line.substring(last, matcher.start()) : line.substring(last);
            if (!sub.isEmpty()) {
                Style copy = style.copy();
                int index = list.size() - 1;
                if (index >= 0 && list.get(index).getStyle().equals(copy)) {
                    String begin = list.get(index).getContents();
                    list.set(index, new StringTextComponent(begin + sub).setStyle(copy));
                } else {
                    list.add(new StringTextComponent(sub).setStyle(copy));
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
    
    public static ITextComponent toComponent(List<ITextComponent> list) {
        if (list.isEmpty()) {
            return new StringTextComponent("");
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            ITextComponent root = new StringTextComponent("");
            list.forEach(root::append);
            return root;
        }
    }
    
    public static TextFormatting byCode(char code) {
        char lower = Character.toLowerCase(code);
        return Arrays.stream(TextFormatting.values())
                     .filter(f->f.code == lower)
                     .findAny()
                     .orElse(null);
    }
    
    public static Style applyToStyle(Style style, TextFormatting formatting) {
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
    
    public static Style toStyle(TextFormatting formatting) {
        return applyToStyle(new Style(), formatting);
    }
    
    public static String trimReset(String str) {
        String r = TextFormatting.RESET.toString();
        if (str.startsWith(r)) {
            str = str.substring(r.length());
        }
        if (str.endsWith(r)) {
            str = str.substring(0, str.length() - r.length());
        }
        return str;
    }
    
    public static Style invertBy(Style style, Style patch) {
        Style copy1 = style.copy().inheritFrom(null);
        Style copy2 = style.copy()
                           .inheritFrom(new Style().setColor(TextFormatting.BLACK)
                                                   .setObfuscated(true)
                                                   .setBold(true)
                                                   .setStrikethrough(true)
                                                   .setUnderlined(true)
                                                   .setItalic(true));
        TextFormatting patchColor = patch.getColor();
        if (copy1.getColor() == copy2.getColor()
            && patchColor != null
            && patchColor != TextFormatting.RESET) {
            TextFormatting color = style.getColor();
            if (color == patchColor) {
                style.setColor(TextFormatting.RESET);
            } else if (color == null || color == TextFormatting.RESET) {
                style.setColor(patchColor);
            }
        }
        if (copy1.isObfuscated() == copy2.isObfuscated() && patch.isObfuscated()) {
            style.setObfuscated(!style.isObfuscated());
        }
        if (copy1.isBold() == copy2.isBold() && patch.isBold()) {
            style.setBold(!style.isBold());
        }
        if (copy1.isStrikethrough() == copy2.isStrikethrough() && patch.isStrikethrough()) {
            style.setStrikethrough(!style.isStrikethrough());
        }
        if (copy1.isUnderlined() == copy2.isUnderlined() && patch.isUnderlined()) {
            style.setUnderlined(!style.isUnderlined());
        }
        if (copy1.isItalic() == copy2.isItalic() && patch.isItalic()) {
            style.setItalic(!style.isItalic());
        }
        return style;
    }
    
    public static Style invert(Style style) {
        return invertBy(style, new Style().setColor(TextFormatting.RED)
                                          .setObfuscated(true)
                                          .setBold(true)
                                          .setStrikethrough(true)
                                          .setUnderlined(true)
                                          .setItalic(true));
    }
    
    public static Stream<ITextComponent> stream(ITextComponent component) {
        return component.stream();
    }
    
    public static Stream<ITextComponent> fragmentate(ITextComponent component) {
        return stream(component).flatMap(sub-> {
            Style style = sub.getStyle();
            String str = sub.getContents();
            return IntStream.range(0, str.length())
                            .mapToObj(str::charAt)
                            .map(String::valueOf)
                            .map(StringTextComponent::new)
                            .peek(comp->comp.setStyle(style.flatCopy()));
        });
    }
    
    public static ITextComponent substring(ITextComponent line, int beginIndex, int endIndex) {
        List<ITextComponent> list = fragmentate(line).collect(Collectors.toList());
        return toComponent(list.subList(beginIndex, endIndex));
    }
    
    public static ITextComponent normalize(ITextComponent component) {
        return parseLine(component.getColoredString());
    }
}
