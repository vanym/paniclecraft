package com.vanym.paniclecraft.client.gui.element;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStyleEditor extends AbstractButton {
    
    protected final Style style;
    protected final TextFormatting iconType;
    
    protected final Consumer<Style> updater;
    protected final Supplier<Boolean> highlighted;
    
    public GuiStyleEditor(int id,
            int x,
            int y,
            int width,
            int height,
            Consumer<Style> updater,
            Supplier<Boolean> highlighted,
            Style style,
            TextFormatting iconType) {
        super(id, x, y, width, height, "");
        this.style = style.createShallowCopy().setParentStyle(null);
        this.iconType = Objects.requireNonNull(iconType);
        this.updater = Objects.requireNonNull(updater);
        this.highlighted = Objects.requireNonNull(highlighted);
    }
    
    @Override
    public void onPress() {
        this.updater.accept(this.style.createShallowCopy());
    }
    
    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
        if (!this.visible) {
            return;
        }
        if (this.iconType.isFancyStyling()) {
            mc.fontRenderer.drawString(this.iconType + this.iconType.name().substring(0, 1),
                                       this.x, this.y, Color.WHITE.getRGB());
        } else if (this.iconType == TextFormatting.RESET) {
            mc.fontRenderer.drawString("✕", this.x, this.y, Color.WHITE.getRGB());
        } else /* colors */ {
            int rgb = mc.fontRenderer.colorCode[this.iconType.getColorIndex()];
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0xff000000 | rgb);
        }
        if (this.highlighted.get()) {
            Color color = Color.YELLOW.darker();
            this.drawHorizontalLine(this.x - 1, this.x + this.width,
                                    this.y - 1, color.getRGB());
            this.drawHorizontalLine(this.x - 1, this.x + this.width,
                                    this.y + this.height, color.getRGB());
            this.drawVerticalLine(this.x - 1,
                                  this.y - 1, this.y + this.height, color.getRGB());
            this.drawVerticalLine(this.x + this.width,
                                  this.y - 1, this.y + this.height, color.getRGB());
        }
    }
    
    public static GuiStyleEditor create(
            int id,
            int x,
            int y,
            int width,
            int height,
            Consumer<Style> updater,
            Supplier<Style> getter,
            TextFormatting code) {
        Style style = FormattingUtils.toStyle(code);
        return new GuiStyleEditor(
                id,
                x,
                y,
                width,
                height,
                (update)->updater.accept(FormattingUtils.invertBy(update, getter.get())),
                ()-> {
                    Style parent = getter.get();
                    Style copy = style.createShallowCopy().setParentStyle(parent);
                    return !copy.isEmpty() && copy.equals(parent);
                },
                style,
                code);
    }
    
    public static List<GuiStyleEditor> createMenu(
            int id,
            int x,
            int y,
            Supplier<Style> getter,
            Consumer<Style> updater) {
        List<GuiStyleEditor> list = new ArrayList<>();
        for (int i = TextFormatting.BLACK.ordinal();
             i <= TextFormatting.WHITE.ordinal();
             i++) {
            int offsetX = (i % 4) * 8, offsetY = (i / 4) * 8;
            TextFormatting code = TextFormatting.values()[i];
            list.add(create(id++, x + 7 + offsetX, y + offsetY, 7, 7, updater, getter, code));
        }
        for (int i = TextFormatting.OBFUSCATED.ordinal(), k = 0;
             i <= TextFormatting.ITALIC.ordinal();
             i++, k++) {
            TextFormatting code = TextFormatting.values()[i];
            list.add(create(id++, x + k * 8, y + 33, 6, 9, updater, getter, code));
        }
        list.add(new GuiStyleEditor(
                id++,
                x,
                y + 24,
                6,
                9,
                updater,
                ()->false,
                new Style().setColor(TextFormatting.RESET).createDeepCopy(),
                TextFormatting.RESET));
        return list;
    }
}
