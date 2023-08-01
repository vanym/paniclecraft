package com.vanym.paniclecraft.client.gui.element;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

@SideOnly(Side.CLIENT)
public class GuiStyleEditor extends AbstractButton {
    
    protected final ChatStyle style;
    protected final EnumChatFormatting iconType;
    
    protected final Consumer<ChatStyle> updater;
    protected final Supplier<Boolean> highlighted;
    
    public GuiStyleEditor(int id,
            int x,
            int y,
            int width,
            int height,
            Consumer<ChatStyle> updater,
            Supplier<Boolean> highlighted,
            ChatStyle style,
            EnumChatFormatting iconType) {
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
    public void drawButton(Minecraft mc, int x, int y) {
        if (!this.visible) {
            return;
        }
        if (this.iconType.isFancyStyling()) {
            mc.fontRenderer.drawString(this.iconType +
                                       this.iconType.name().substring(0, 1),
                                       this.xPosition, this.yPosition, Color.WHITE.getRGB());
        } else if (this.iconType == EnumChatFormatting.RESET) {
            mc.fontRenderer.drawString("✕", this.xPosition, this.yPosition, Color.WHITE.getRGB());
        } else /* colors */ {
            int rgb = mc.fontRenderer.colorCode[this.iconType.ordinal()];
            drawRect(this.xPosition, this.yPosition,
                     this.xPosition + this.width,
                     this.yPosition + this.height, 0xff000000 | rgb);
        }
        if (this.highlighted.get()) {
            Color color = Color.YELLOW.darker();
            this.drawHorizontalLine(this.xPosition - 1, this.xPosition + this.width,
                                    this.yPosition - 1, color.getRGB());
            this.drawHorizontalLine(this.xPosition - 1, this.xPosition + this.width,
                                    this.yPosition + this.height, color.getRGB());
            this.drawVerticalLine(this.xPosition - 1,
                                  this.yPosition - 1, this.yPosition + this.height, color.getRGB());
            this.drawVerticalLine(this.xPosition + this.width,
                                  this.yPosition - 1, this.yPosition + this.height, color.getRGB());
        }
    }
    
    public static GuiStyleEditor create(
            int id,
            int x,
            int y,
            int width,
            int height,
            Consumer<ChatStyle> updater,
            Supplier<ChatStyle> getter,
            EnumChatFormatting code) {
        ChatStyle style = FormattingUtils.toStyle(code);
        return new GuiStyleEditor(
                id,
                x,
                y,
                width,
                height,
                (update)->updater.accept(FormattingUtils.invertBy(update, getter.get())),
                ()-> {
                    ChatStyle parent = getter.get();
                    ChatStyle copy = style.createShallowCopy().setParentStyle(parent);
                    return !copy.isEmpty() && copy.equals(parent);
                },
                style,
                code);
    }
    
    public static List<GuiStyleEditor> createMenu(
            int id,
            int x,
            int y,
            Supplier<ChatStyle> getter,
            Consumer<ChatStyle> updater) {
        List<GuiStyleEditor> list = new ArrayList<>();
        for (int i = EnumChatFormatting.BLACK.ordinal();
             i <= EnumChatFormatting.WHITE.ordinal();
             i++) {
            int offsetX = (i % 4) * 8, offsetY = (i / 4) * 8;
            EnumChatFormatting code = EnumChatFormatting.values()[i];
            list.add(create(id++, x + 7 + offsetX, y + offsetY, 7, 7, updater, getter, code));
        }
        for (int i = EnumChatFormatting.OBFUSCATED.ordinal(), k = 0;
             i <= EnumChatFormatting.ITALIC.ordinal();
             i++, k++) {
            EnumChatFormatting code = EnumChatFormatting.values()[i];
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
                new ChatStyle().setColor(EnumChatFormatting.RESET).createDeepCopy(),
                EnumChatFormatting.RESET));
        return list;
    }
}
