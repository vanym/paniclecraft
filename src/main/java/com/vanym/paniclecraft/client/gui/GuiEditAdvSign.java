package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditAdvSign extends Screen {
    
    protected final TileEntityAdvSign tileAS;
    
    protected int editLine = 0;
    protected int updateCounter;
    
    protected Button buttonDone;
    protected Button buttonCopy;
    protected Button buttonPaste;
    protected Button buttonAddSect;
    protected Button buttonAddLine;
    protected Button buttonRemoveLine;
    protected Button buttonToggleStick;
    
    protected GuiCircularSlider sliderDir;
    
    protected GuiHexColorField standColorHex;
    protected GuiHexColorField textColorHex;
    
    public GuiEditAdvSign(TileEntityAdvSign tileAS) {
        super(NarratorChatListener.field_216868_a);
        this.tileAS = tileAS;
    }
    
    @Override
    public void init() {
        super.init();
        int xCenter = this.width / 2;
        this.buttonDone = new Button(
                xCenter - 100,
                this.height / 4 + 120,
                200,
                20,
                I18n.format("gui.done"),
                this::actionPerformed);
        this.buttonAddLine =
                new Button(xCenter + 59, this.height / 4 + 68, 20, 20, "+", this::actionPerformed);
        this.buttonRemoveLine = new Button(
                this.buttonAddLine.x + 21,
                this.buttonAddLine.y,
                20,
                20,
                "-",
                this::actionPerformed);
        this.buttonAddSect = new Button(
                xCenter - 100,
                this.height / 4 + 99,
                20,
                20,
                "+\u00a7",
                this::actionPerformed);
        this.buttonCopy = new Button(
                xCenter - 79,
                this.height / 4 + 99,
                30,
                20,
                "Copy",
                this::actionPerformed);
        this.buttonPaste = new Button(
                xCenter - 48,
                this.height / 4 + 99,
                30,
                20,
                "Paste",
                this::actionPerformed);
        this.buttonToggleStick = new Button(
                xCenter - 100,
                this.height / 4 + 78,
                70,
                20,
                "Toggle Stick",
                this::actionPerformed);
        this.lineButtonsUpdate();
        this.sliderDir = new GuiCircularSlider(xCenter - 100, this.height / 4 + 36, 40, 40);
        this.sliderDir.setGetter(()->this.tileAS.getDirection() / 360.0D);
        this.sliderDir.setSetter(v-> {
            v *= 16.0D;
            if (Screen.hasShiftDown()) {
                v = (double)Math.round(v);
            }
            v *= 22.5D;
            v = (double)Math.round(v);
            this.tileAS.setDirection(v);
        });
        this.sliderDir.setOffset(-0.25D);
        this.standColorHex =
                new GuiHexColorField(
                        this.font,
                        xCenter + 48,
                        this.height / 4 + 106);
        this.standColorHex.setRGB(ColorUtils.getAlphaless(this.tileAS.getStandColor()));
        this.standColorHex.setSetter(rgb->this.tileAS.setStandColor(new Color(rgb)));
        this.textColorHex = new GuiHexColorField(this.font, xCenter + 48, this.height / 4 + 90);
        this.textColorHex.setRGB(ColorUtils.getAlphaless(this.tileAS.getTextColor()));
        this.textColorHex.setSetter(rgb->this.tileAS.setTextColor(new Color(rgb)));
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.addButton(this.buttonDone);
        this.addButton(this.buttonRemoveLine);
        this.addButton(this.buttonAddLine);
        this.addButton(this.buttonAddSect);
        this.addButton(this.buttonCopy);
        this.addButton(this.buttonPaste);
        this.addButton(this.buttonToggleStick);
        this.addButton(this.sliderDir);
    }
    
    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        Core.instance.network.sendToServer(new MessageAdvSignChange(this.tileAS));
        super.onClose();
    }
    
    @Override
    public void tick() {
        ++this.updateCounter;
        this.standColorHex.tick();
        this.textColorHex.tick();
    }
    
    protected void actionPerformed(Button button) {
        if (!button.active) {
            return;
        }
        if (button == this.buttonDone) {
            this.minecraft.displayGuiScreen(null);
        } else if (button == this.buttonAddLine) {
            if (this.tileAS.lines.size() >= TileEntityAdvSign.MAX_LINES) {
                return;
            }
            this.tileAS.lines.add("");
            this.lineButtonsUpdate();
        } else if (button == this.buttonRemoveLine) {
            if (this.tileAS.lines.size() <= TileEntityAdvSign.MIN_LINES) {
                return;
            }
            int last = this.tileAS.lines.size() - 1;
            this.tileAS.lines.remove(last);
            this.editLine = Math.min(this.editLine, last - 1);
            IntStream.range(0, this.tileAS.lines.size()).forEach(this::refitText);
            this.lineButtonsUpdate();
        } else if (button == this.buttonAddSect) {
            this.addText("\u00a7");
        } else if (button == this.buttonCopy) {
            this.minecraft.keyboardListener.setClipboardString(String.join(System.lineSeparator(),
                                                                           this.tileAS.lines));
        } else if (button == this.buttonPaste) {
            this.pasteFull(this.minecraft.keyboardListener.getClipboardString());
            this.lineButtonsUpdate();
        } else if (button == this.buttonToggleStick) {
            this.tileAS.setStick(!this.tileAS.onStick());
        }
    }
    
    @Override
    public boolean charTyped(char character, int key) {
        if (this.standColorHex.charTyped(character, key)
            || this.textColorHex.charTyped(character, key)) {
            return true;
        }
        if (key == 1) {
            this.actionPerformed(this.buttonDone);
            return true;
        }
        switch (character) {
            case 3: // Ctrl+c
                this.minecraft.keyboardListener.setClipboardString(this.getCurrentLine());
            break;
            case 22: // Ctrl+v
                this.addText(this.minecraft.keyboardListener.getClipboardString());
            break;
            case 24: // Ctrl+x
                this.minecraft.keyboardListener.setClipboardString(this.getCurrentLine());
                this.setCurrentLine("");
            break;
            default:
                switch (key) {
                    case 14: { // backspace
                        String line = this.getCurrentLine();
                        if (line.length() > 0) {
                            if (Screen.hasControlDown()) {
                                this.setCurrentLine("");
                            } else {
                                this.setCurrentLine(line.substring(0, line.length() - 1));
                            }
                        }
                    }
                    break;
                    case 200: // up
                        --this.editLine;
                        if (this.editLine < 0) {
                            this.editLine = this.tileAS.lines.size() - 1;
                        }
                    break;
                    case 15: // tab
                    case 28: // enter
                    case 156: // enter numpad
                    case 208: // down
                        ++this.editLine;
                        if (this.editLine >= this.tileAS.lines.size()) {
                            this.editLine = 0;
                        }
                    break;
                    case 211: { // delete
                        String line = this.getCurrentLine();
                        if (line.length() > 0) {
                            if (Screen.hasControlDown()) {
                                this.setCurrentLine("");
                            } else {
                                this.setCurrentLine(line.substring(1));
                            }
                        }
                    }
                    break;
                    default:
                        this.addText(Character.toString(character));
                    break;
                }
            break;
        }
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int eventButton) {
        return this.standColorHex.mouseClicked(x, y, eventButton)
            || this.textColorHex.mouseClicked(x, y, eventButton)
            || super.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (super.mouseDragged(x, y, button, dragX, dragY)) {
            return true;
        }
        Stream<GuiCircularSlider> sliders = this.buttons.stream()
                                                        .filter(GuiCircularSlider.class::isInstance)
                                                        .map(GuiCircularSlider.class::cast);
        return sliders.anyMatch(s->s.mouseDragged(x, y, button, dragX, dragY));
    }
    
    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        if (!this.sliderDir.isPressed()) {
            this.renderBackground();
        }
        this.drawCenteredString(this.font, I18n.format("sign.edit"), this.width / 2, 40, 0xffffff);
        if (this.sliderDir.isPressed()) {
            this.sliderDir.render(mouseX, mouseY, renderPartialTicks);
            return;
        }
        this.drawSign();
        String linesText = String.format("Lines:%2d", this.tileAS.lines.size());
        int linesTextWidth = this.font.getStringWidth(linesText);
        this.drawString(this.font, linesText,
                        this.buttonAddLine.x - 2 - linesTextWidth,
                        this.buttonAddLine.y + 10, 0xffffff);
        String standText = "Stand:";
        int standTextWidth = this.font.getStringWidth(standText);
        this.drawString(this.font, standText,
                        this.standColorHex.x - 2 - standTextWidth,
                        this.standColorHex.y + 3, 0xffffff);
        this.standColorHex.render(mouseX, mouseY, renderPartialTicks);
        String textText = "Text:";
        int textTextWidth = this.font.getStringWidth(textText);
        this.drawString(this.font, textText,
                        this.textColorHex.x - 2 - textTextWidth,
                        this.textColorHex.y + 3, 0xffffff);
        this.textColorHex.render(mouseX, mouseY, renderPartialTicks);
        super.render(mouseX, mouseY, renderPartialTicks);
    }
    
    protected void drawSign() {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.width / 2, 0.0F, 50.0F);
        float scale = 93.75F;
        GlStateManager.scalef(-scale, -scale, -scale);
        GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
        
        int selectLine = -1;
        if (this.updateCounter / 6 % 2 == 0) {
            selectLine = this.editLine;
        }
        
        Core.instance.advSign.tileAdvSignRenderer.render(this.tileAS, -0.5D, -0.75D,
                                                         -0.5D, 0.0F, -1, true, false,
                                                         selectLine);
        GlStateManager.popMatrix();
    }
    
    protected void lineButtonsUpdate() {
        this.buttonAddLine.active = (this.tileAS.lines.size() < TileEntityAdvSign.MAX_LINES);
        this.buttonRemoveLine.active = (this.tileAS.lines.size() > TileEntityAdvSign.MIN_LINES);
    }
    
    protected void pasteFull(String text) {
        this.tileAS.lines.clear();
        Arrays.stream(text.split("\\R", TileEntityAdvSign.MAX_LINES))
              .limit(TileEntityAdvSign.MAX_LINES)
              .forEachOrdered(this.tileAS.lines::add);
        IntStream.range(0, this.tileAS.lines.size()).forEach(this::refitText);
    }
    
    protected void setCurrentLine(String line) {
        this.tileAS.lines.set(this.editLine, line);
    }
    
    protected String getCurrentLine() {
        return this.tileAS.lines.get(this.editLine);
    }
    
    protected void addText(String text) {
        this.addText(text, this.editLine);
    }
    
    protected void addText(String text, int lineIndex) {
        String line = this.tileAS.lines.get(lineIndex);
        StringBuilder sb = new StringBuilder(line);
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c != '\u00a7' && !SharedConstants.isAllowedCharacter(c)) {
                continue;
            }
            sb.append(c);
            if (!this.lineFits(sb)) {
                sb.deleteCharAt(sb.length() - 1);
                break;
            }
        }
        this.tileAS.lines.set(lineIndex, sb.toString());
    }
    
    protected void refitText(int lineIndex) {
        String line = this.tileAS.lines.get(lineIndex);
        this.tileAS.lines.set(lineIndex, "");
        this.addText(line, lineIndex);
    }
    
    protected int getMaxLineFontWidth() {
        int lines = this.tileAS.lines.size();
        return 23 * lines;
    }
    
    protected boolean lineFits(CharSequence line) {
        return this.font.getStringWidth(line.toString()) <= this.getMaxLineFontWidth();
    }
}
