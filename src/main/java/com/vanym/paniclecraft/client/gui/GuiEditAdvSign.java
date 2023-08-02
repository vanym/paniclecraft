package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.client.gui.element.GuiStyleEditor;
import com.vanym.paniclecraft.client.utils.AdvTextInput;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditAdvSign extends Screen {
    
    protected final TileEntityAdvSign sign;
    
    protected final SideEditState frontState;
    protected final SideEditState backState;
    
    protected boolean front;
    
    protected int updateCounter;
    
    protected Button buttonDone;
    protected Button buttonCopy;
    protected Button buttonPaste;
    protected Button buttonAddLine;
    protected Button buttonRemoveLine;
    protected Button buttonToggleStick;
    protected Button buttonFlip;
    
    protected GuiCircularSlider sliderDir;
    
    protected GuiHexColorField standColorHex;
    protected GuiHexColorField textColorHex;
    
    public GuiEditAdvSign(TileEntityAdvSign sign) {
        this(sign, true);
    }
    
    public GuiEditAdvSign(TileEntityAdvSign sign, boolean front) {
        super(NarratorChatListener.field_216868_a);
        this.sign = sign;
        this.front = front;
        this.frontState = new SideEditState(sign.getFront());
        this.backState = new SideEditState(sign.getBack());
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
        this.buttonCopy = new Button(
                xCenter - 100,
                this.height / 4 + 99,
                40,
                20,
                "Copy",
                this::actionPerformed);
        this.buttonPaste = new Button(
                xCenter - 59,
                this.height / 4 + 99,
                40,
                20,
                "Paste",
                this::actionPerformed);
        this.buttonToggleStick = new Button(
                xCenter - 100,
                this.height / 4 + 57,
                55,
                20,
                "Stick: ",
                this::actionPerformed);
        this.buttonFlip = new Button(
                xCenter - 100,
                this.height / 4 + 78,
                60,
                20,
                "Side: ",
                this::actionPerformed);
        this.sliderDir = new GuiCircularSlider(xCenter - 100, this.height / 4 + 15, 40, 40);
        this.sliderDir.setGetter(()->this.sign.getDirection() / 360.0D);
        this.sliderDir.setSetter(v-> {
            v *= 16.0D;
            if (Screen.hasShiftDown()) {
                v = (double)Math.round(v);
            }
            v *= 22.5D;
            v = (double)Math.round(v);
            this.sign.setDirection(v);
        });
        this.sliderDir.setOffset(-0.25D);
        this.standColorHex =
                new GuiHexColorField(
                        this.font,
                        xCenter + 48,
                        this.height / 4 + 106);
        this.standColorHex.setSetter(rgb->this.sign.setStandColor(new Color(rgb)));
        this.textColorHex = new GuiHexColorField(this.font, xCenter + 48, this.height / 4 + 90);
        this.textColorHex.setSetter(rgb->this.getState().getText().setTextColor(new Color(rgb)));
        List<GuiStyleEditor> stylingMenu =
                GuiStyleEditor.createMenu(xCenter + 61, this.height / 4 + 25,
                                          ()->this.getState().getInput().getStyle(),
                                          (style)-> {
                                              SideEditState state = this.getState();
                                              state.getInput().applyStyle(style);
                                              state.updateLine();
                                          });
        this.updateElements();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.addButton(this.buttonDone);
        this.addButton(this.buttonRemoveLine);
        this.addButton(this.buttonAddLine);
        this.addButton(this.buttonCopy);
        this.addButton(this.buttonPaste);
        this.addButton(this.buttonToggleStick);
        this.addButton(this.buttonFlip);
        this.addButton(this.sliderDir);
        stylingMenu.forEach(this::addButton);
    }
    
    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.getState().updateLine();
        Core.instance.network.sendToServer(new MessageAdvSignChange(this.sign));
        super.onClose();
    }
    
    @Override
    public void tick() {
        ++this.updateCounter;
        this.standColorHex.tick();
        this.textColorHex.tick();
    }
    
    protected void actionPerformed(Button button) {
        if (button == this.buttonDone) {
            this.minecraft.displayGuiScreen(null);
        } else if (button == this.buttonAddLine) {
            AdvSignText text = this.getState().getText();
            text.getLines().add(new StringTextComponent(""));
            text.fixSize();
            this.updateElements();
        } else if (button == this.buttonRemoveLine) {
            AdvSignText text = this.getState().getText();
            text.removeLast();
            text.fixSize();
            this.getState()
                .switchToLine(Math.min(this.getState().getLine(),
                                       text.getLines().size() - 1));
            this.updateElements();
        } else if (button == this.buttonCopy) {
            this.minecraft.keyboardListener.setClipboardString(this.getState()
                                                                   .getText()
                                                                   .getLines()
                                                                   .stream()
                                                                   .map(ITextComponent::getFormattedText)
                                                                   .map(FormattingUtils::trimReset)
                                                                   .collect(Collectors.joining(System.lineSeparator())));
        } else if (button == this.buttonPaste) {
            this.getState().pasteFull(this.minecraft.keyboardListener.getClipboardString());
            this.updateElements();
        } else if (button == this.buttonToggleStick) {
            this.sign.setStick(!this.sign.onStick());
            this.updateElements();
        } else if (button == this.buttonFlip) {
            this.front = !this.front;
            this.updateElements();
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
        if (key == 200 /* up */ || key == 201 /* page up */) {
            this.getState().switchLine(-1);
        } else if (Stream.of(208, 209, 15, 28, 156 /* down, page down, tab, enter, enter numpad */)
                         .anyMatch(code->code == key)) {
            this.getState().switchLine(+1);
        } else {
            AdvTextInput input = this.getState().getInput();
            if (!input.keyTyped(character, key)
                && SharedConstants.isAllowedCharacter(character)) {
                input.insertText(Character.toString(character));
            }
            this.getState().updateLine();
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
        String linesText = String.format("Lines:%2d", this.getState().getText().getLines().size());
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
        GlStateManager.rotatef(this.front ? 180.0F : 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
        Core.instance.advSign.tileAdvSignRenderer.render(this.sign, -0.5D, -0.75D,
                                                         -0.5D, 0.0F, -1, true, false,
                                                         this);
        GlStateManager.popMatrix();
    }
    
    public boolean isBlink() {
        return this.updateCounter / 6 % 2 == 0;
    }
    
    public AdvTextInput getInput(boolean front, int line) {
        return this.getState(front).getInput(line);
    }
    
    protected SideEditState getState() {
        return this.getState(this.front);
    }
    
    protected SideEditState getState(boolean front) {
        return front ? this.frontState : this.backState;
    }
    
    protected void updateElements() {
        this.buttonAddLine.active = !this.getState().getText().isMax();
        this.buttonRemoveLine.active = !this.getState().getText().isMin();
        this.standColorHex.setRGB(ColorUtils.getAlphaless(this.sign.getStandColor()));
        this.textColorHex.setRGB(ColorUtils.getAlphaless(this.getState().getText().getTextColor()));
        this.buttonToggleStick.setMessage("Stick: " + (this.sign.onStick() ? "ON" : "OFF"));
        this.buttonFlip.setMessage("Side: " + (this.front ? "Front" : "Back"));
    }
    
    protected class SideEditState {
        
        public final AdvTextInput input = new AdvTextInput();
        protected final AdvSignText text;
        
        protected int editLine = 0;
        
        public SideEditState(AdvSignText text) {
            this.text = text;
            this.switchLine(0);
        }
        
        public void switchLine(int offset) {
            this.switchToLine(this.editLine + offset);
        }
        
        public void switchToLine(int line) {
            int size = this.text.getLines().size();
            if (size > 0) {
                this.editLine = (size + line % size) % size;
                this.input.read(this.text.getLines().get(this.editLine));
            }
        }
        
        public void pasteFull(String text) {
            List<ITextComponent> lines = this.text.getLines();
            lines.clear();
            Arrays.stream(text.split("\\R", AdvSignText.MAX_LINES))
                  .limit(AdvSignText.MAX_LINES)
                  .map(FormattingUtils::parseLine)
                  .forEachOrdered(lines::add);
            for (int i = 0; i < lines.size(); i++) {
                this.switchToLine(i);
                this.trim();
                this.updateLine();
            }
        }
        
        protected void trim() {
            while (!this.lineFits(this.input.getComponent().getFormattedText())
                && (this.input.removeBack() || this.input.removeLast())) {
            }
        }
        
        protected boolean lineFits(CharSequence line) {
            int max = this.getMaxLineFontWidth();
            return GuiEditAdvSign.this.font.getStringWidth(line.toString()) <= max;
        }
        
        public void updateLine() {
            this.trim();
            try {
                this.text.getLines().set(this.editLine, this.input.getComponent());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        
        public int getLine() {
            return this.editLine;
        }
        
        public AdvTextInput getInput() {
            return this.input;
        }
        
        public AdvSignText getText() {
            return this.text;
        }
        
        public AdvTextInput getInput(int line) {
            return this.editLine == line ? this.input : null;
        }
        
        protected int getMaxLineFontWidth() {
            int size = this.text.getLines().size();
            return 23 * size;
        }
    }
}
