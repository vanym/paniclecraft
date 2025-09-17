package com.vanym.paniclecraft.core.component.advsign;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class FormattingUtils {
    
    public static ITextComponent parseLine(String line) {
        Matcher matcher = TextFormatting.STRIP_FORMATTING_PATTERN.matcher(line);
        List<ITextComponent> list = new ArrayList<>();
        Style style = Style.EMPTY;
        for (int last = 0;;) {
            boolean find = matcher.find();
            String sub = find ? line.substring(last, matcher.start()) : line.substring(last);
            if (!sub.isEmpty()) {
                int index = list.size() - 1;
                if (index >= 0 && list.get(index).getStyle().equals(style)) {
                    String begin = list.get(index).getContents();
                    list.set(index, new StringTextComponent(begin + sub).setStyle(style));
                } else {
                    list.add(new StringTextComponent(sub).setStyle(style));
                }
            }
            if (find) {
                last = matcher.end();
                style = style.applyFormat(byCode(matcher.group().charAt(1)));
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
            IFormattableTextComponent root = new StringTextComponent("");
            list.forEach(root::append);
            return root;
        }
    }
    
    public static TextFormatting byCode(char code) {
        return TextFormatting.getByCode(code);
    }
    
    public static Style toStyle(TextFormatting formatting) {
        return Style.EMPTY.applyFormat(formatting);
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
        Style copy1 = Style.EMPTY.applyTo(style);
        Style copy2 = Style.EMPTY.withColor(TextFormatting.BLACK)
                                 .setObfuscated(true)
                                 .withBold(true)
                                 .setStrikethrough(true)
                                 .setUnderlined(true)
                                 .withItalic(true)
                                 .applyTo(style);
        Color patchColor = patch.getColor();
        if (copy2.getColor().equals(copy1.getColor())
            && patchColor != null) {
            Color color = style.getColor();
            if (patchColor.equals(color)) {
                style = style.withColor((Color)null);
            } else if (color == null) {
                style = style.withColor(patchColor);
            }
        }
        if (copy1.isObfuscated() == copy2.isObfuscated() && patch.isObfuscated()) {
            style = style.setObfuscated(!style.isObfuscated());
        }
        if (copy1.isBold() == copy2.isBold() && patch.isBold()) {
            style = style.withBold(!style.isBold());
        }
        if (copy1.isStrikethrough() == copy2.isStrikethrough() && patch.isStrikethrough()) {
            style = style.setStrikethrough(!style.isStrikethrough());
        }
        if (copy1.isUnderlined() == copy2.isUnderlined() && patch.isUnderlined()) {
            style = style.setUnderlined(!style.isUnderlined());
        }
        if (copy1.isItalic() == copy2.isItalic() && patch.isItalic()) {
            style = style.withItalic(!style.isItalic());
        }
        return style;
    }
    
    public static Style invert(Style style) {
        return invertBy(style, Style.EMPTY.withColor(TextFormatting.RED)
                                          .setObfuscated(true)
                                          .withBold(true)
                                          .setStrikethrough(true)
                                          .setUnderlined(true)
                                          .withItalic(true));
    }
    
    public static Stream<ITextComponent> stream(ITextComponent component) {
        return Streams.concat(Stream.of(component),
                              component.getSiblings().stream().flatMap(FormattingUtils::stream));
    }
    
    public static Stream<IFormattableTextComponent> fragmentate(ITextComponent component) {
        return stream(component).flatMap(sub-> {
            Style style = sub.getStyle();
            String str = sub.getContents();
            return IntStream.range(0, str.length())
                            .mapToObj(str::charAt)
                            .map(String::valueOf)
                            .map(StringTextComponent::new)
                            .peek(comp->comp.setStyle(style));
        });
    }
    
    public static ITextComponent substring(ITextComponent line, int beginIndex, int endIndex) {
        List<ITextComponent> list = fragmentate(line).collect(Collectors.toList());
        return toComponent(list.subList(beginIndex, endIndex));
    }
}
