package com.vanym.paniclecraft.core.component.advsign;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vanym.paniclecraft.utils.INBTSerializable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AdvSignText implements INBTSerializable<NBTTagCompound> {
    
    public static final int MAX_LINES = 32;
    public static final int MIN_LINES = 1;
    
    protected final List<IChatComponent> lines = Stream.generate(()->new ChatComponentText(""))
                                                       .limit(5)
                                                       .collect(Collectors.toList());
    
    protected Color textColor = Color.BLACK;
    
    public AdvSignText() {}
    
    public AdvSignText(NBTTagCompound nbtTag) {
        this.deserializeNBT(nbtTag);
    }
    
    public List<IChatComponent> getLines() {
        return this.lines;
    }
    
    public boolean removeLast() {
        if (this.isMin()) {
            return false;
        }
        this.lines.remove(this.lines.size() - 1);
        return true;
    }
    
    public void fixSize() {
        if (this.lines.isEmpty()) {
            this.lines.add(new ChatComponentText(""));
        }
        while (this.lines.size() > MAX_LINES) {
            this.lines.remove(MAX_LINES);
        }
    }
    
    public boolean isMin() {
        return this.lines.size() <= MIN_LINES;
    }
    
    public boolean isMax() {
        return this.lines.size() >= MAX_LINES;
    }
    
    public void setTextColor(Color color) {
        Objects.requireNonNull(color);
        this.textColor = color;
    }
    
    public Color getTextColor() {
        return this.textColor;
    }
    
    public boolean isEmpty() {
        return this.lines.stream()
                         .map(IChatComponent::getUnformattedText)
                         .allMatch(String::isEmpty);
    }
    
    public boolean isValid() {
        int size = this.lines.size();
        return MIN_LINES <= size && size <= MAX_LINES
            && this.textColor.getAlpha() == 0xff
            && this.lines.stream()
                         .flatMap(FormattingUtils::stream)
                         .allMatch(AdvSignText::isValid)
            && this.lines.stream()
                         .map(IChatComponent::getUnformattedText)
                         .allMatch(line->line.length() <= 64 * size
                             && IntStream.range(0, line.length())
                                         .mapToObj(line::charAt)
                                         .allMatch(ChatAllowedCharacters::isAllowedCharacter))
            && this.lines.stream()
                         .allMatch(root->FormattingUtils.stream(root)
                                                        .allMatch(comp->comp == root
                                                            || !comp.getUnformattedTextForChat()
                                                                    .isEmpty()));
    }
    
    public static final String TAG_LINES = "Lines";
    public static final String TAG_TEXTCOLOR = "TextColor";
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        NBTTagList linesTag = new NBTTagList();
        this.lines.stream()
                  .map(IChatComponent.Serializer::func_150696_a)
                  .map(NBTTagString::new)
                  .forEachOrdered(linesTag::appendTag);
        nbtTag.setTag(TAG_LINES, linesTag);
        nbtTag.setInteger(TAG_TEXTCOLOR, this.textColor.getRGB());
        return nbtTag;
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbtTag) {
        this.textColor = new Color(nbtTag.getInteger(TAG_TEXTCOLOR), true);
        this.lines.clear();
        NBTTagList linesTag = nbtTag.getTagList(TAG_LINES, 8);
        IntStream.range(0, linesTag.tagCount())
                 .mapToObj(linesTag::getStringTagAt)
                 .map(IChatComponent.Serializer::func_150699_a)
                 .forEachOrdered(this.lines::add);
    }
    
    protected static boolean isValid(IChatComponent component) {
        return Optional.ofNullable(component)
                       .filter(ChatComponentText.class::isInstance)
                       .map(IChatComponent::getChatStyle)
                       .filter(style->style.getChatHoverEvent() == null)
                       .filter(style->style.getChatClickEvent() == null)
                       .filter(style->style.getColor() == null || style.getColor().isColor())
                       .isPresent();
    }
}
