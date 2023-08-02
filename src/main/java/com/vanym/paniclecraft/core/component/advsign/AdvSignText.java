package com.vanym.paniclecraft.core.component.advsign;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

public class AdvSignText implements INBTSerializable<CompoundNBT> {
    
    public static final int MAX_LINES = 32;
    public static final int MIN_LINES = 1;
    
    protected final List<ITextComponent> lines;
    
    protected Color textColor = Color.BLACK;
    
    public AdvSignText() {
        this(5);
    }
    
    public AdvSignText(int size) {
        size = Math.max(MIN_LINES, Math.min(MAX_LINES, size));
        this.lines = Stream.generate(()->new StringTextComponent(""))
                           .limit(size)
                           .collect(Collectors.toList());
    }
    
    public AdvSignText(CompoundNBT nbtTag) {
        this(0);
        this.deserializeNBT(nbtTag);
    }
    
    public List<ITextComponent> getLines() {
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
            this.lines.add(new StringTextComponent(""));
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
                         .map(ITextComponent::getString)
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
                         .map(ITextComponent::getString)
                         .allMatch(line->line.length() <= 64 * size
                             && IntStream.range(0, line.length())
                                         .mapToObj(line::charAt)
                                         .allMatch(SharedConstants::isAllowedCharacter))
            && this.lines.stream()
                         .allMatch(root->FormattingUtils.stream(root)
                                                        .allMatch(comp->comp == root
                                                            || !comp.getUnformattedComponentText()
                                                                    .isEmpty()));
    }
    
    public static final String TAG_LINES = "Lines";
    public static final String TAG_TEXTCOLOR = "TextColor";
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbtTag = new CompoundNBT();
        ListNBT linesTag = new ListNBT();
        this.lines.stream()
                  .map(ITextComponent.Serializer::toJson)
                  .map(StringNBT::new)
                  .forEachOrdered(linesTag::add);
        nbtTag.put(TAG_LINES, linesTag);
        nbtTag.putInt(TAG_TEXTCOLOR, this.textColor.getRGB());
        return nbtTag;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbtTag) {
        this.textColor = new Color(nbtTag.getInt(TAG_TEXTCOLOR), true);
        this.lines.clear();
        ListNBT linesTag = nbtTag.getList(TAG_LINES, 8);
        IntStream.range(0, linesTag.size())
                 .mapToObj(linesTag::getString)
                 .map(ITextComponent.Serializer::fromJson)
                 .forEachOrdered(this.lines::add);
    }
    
    protected static boolean isValid(ITextComponent component) {
        return Optional.ofNullable(component)
                       .filter(StringTextComponent.class::isInstance)
                       .map(ITextComponent::getStyle)
                       .filter(style->style.getHoverEvent() == null)
                       .filter(style->style.getClickEvent() == null)
                       .filter(style->style.getInsertion() == null)
                       .filter(style->style.getColor() == null || style.getColor().isColor())
                       .isPresent();
    }
}
