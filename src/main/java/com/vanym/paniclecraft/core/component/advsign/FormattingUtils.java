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
        Matcher matcher = TextFormatting.FORMATTING_CODE_PATTERN.matcher(line);
        List<ITextComponent> list = new ArrayList<>();
        Style style = new Style();
        for (int last = 0;;) {
            boolean find = matcher.find();
            String sub = find ? line.substring(last, matcher.start()) : line.substring(last);
            if (!sub.isEmpty()) {
                Style copy = style.createShallowCopy();
                int index = list.size() - 1;
                if (index >= 0 && list.get(index).getStyle().equals(copy)) {
                    String begin = list.get(index).getUnformattedComponentText();
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
            list.forEach(root::appendSibling);
            return root;
        }
    }
    
    public static TextFormatting byCode(char code) {
        char lower = Character.toLowerCase(code);
        return Arrays.stream(TextFormatting.values())
                     .filter(f->f.formattingCode == lower)
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
        Style copy1 = style.createShallowCopy().setParentStyle(null);
        Style copy2 = style.createShallowCopy()
                           .setParentStyle(new Style().setColor(TextFormatting.BLACK)
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
            String str = sub.getUnformattedComponentText();
            return IntStream.range(0, str.length())
                            .mapToObj(str::charAt)
                            .map(String::valueOf)
                            .map(StringTextComponent::new)
                            .peek(comp->comp.setStyle(style.createDeepCopy()));
        });
    }
    
    public static ITextComponent substring(ITextComponent line, int beginIndex, int endIndex) {
        List<ITextComponent> list = fragmentate(line).collect(Collectors.toList());
        return toComponent(list.subList(beginIndex, endIndex));
    }
}
