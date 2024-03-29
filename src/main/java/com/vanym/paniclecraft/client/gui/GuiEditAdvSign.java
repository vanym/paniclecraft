package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.client.gui.element.GuiStyleEditor;
import com.vanym.paniclecraft.client.utils.AdvTextInput;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.core.component.advsign.AdvSignText;
import com.vanym.paniclecraft.core.component.advsign.FormattingUtils;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ColorUtils;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.client.gui.IGuiEventListener;
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
                b->this.minecraft.displayGuiScreen(null));
        this.buttonAddLine = new Button(xCenter + 59, this.height / 4 + 68, 20, 20, "+", b-> {
            AdvSignText text = this.getState().getText();
            text.getLines().add(new StringTextComponent(""));
            text.fixSize();
            this.updateElements();
        });
        this.buttonRemoveLine = new Button(
                this.buttonAddLine.x + 21,
                this.buttonAddLine.y,
                20,
                20,
                "-",
                b-> {
                    AdvSignText text = this.getState().getText();
                    text.removeLast();
                    text.fixSize();
                    this.getState()
                        .switchToLine(Math.min(this.getState().getLine(),
                                               text.getLines().size() - 1));
                    this.updateElements();
                });
        String textCopy = I18n.format(String.format("gui.%s.advanced_sign.copy", DEF.MOD_ID));
        this.buttonCopy = new Button(xCenter - 100, this.height / 4 + 99, 40, 20, textCopy, b-> {
            GuiUtils.setClipboardString(this.getState()
                                            .getText()
                                            .getLines()
                                            .stream()
                                            .map(ITextComponent::getFormattedText)
                                            .map(FormattingUtils::trimReset)
                                            .collect(Collectors.joining(System.lineSeparator())));
        });
        String textPaste = I18n.format(String.format("gui.%s.advanced_sign.paste", DEF.MOD_ID));
        this.buttonPaste = new Button(xCenter - 59, this.height / 4 + 99, 40, 20, textPaste, b-> {
            this.getState().pasteFull(GuiUtils.getClipboardString());
            this.updateElements();
        });
        this.buttonToggleStick = new Button(xCenter - 100, this.height / 4 + 57, 55, 20, "", b-> {
            this.sign.setForm(AdvSignForm.byIndex(this.sign.getForm().getIndex() + 1));
            this.updateElements();
        });
        this.buttonFlip = new Button(xCenter - 100, this.height / 4 + 78, 60, 20, "", b-> {
            this.front = !this.front;
            this.updateElements();
        });
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
        this.addButton(this.standColorHex);
        this.addButton(this.textColorHex);
        stylingMenu.forEach(this::addButton);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.getState().updateLine();
        Core.instance.network.sendToServer(new MessageAdvSignChange(this.sign));
    }
    
    @Override
    public void tick() {
        ++this.updateCounter;
        this.standColorHex.tick();
        this.textColorHex.tick();
    }
    
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_PAGE_UP) {
            this.getState().switchLine(-1);
            return true;
        } else if (Stream.of(GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_PAGE_DOWN, GLFW.GLFW_KEY_TAB,
                             GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER)
                         .anyMatch(code->code == key)) {
            this.getState().switchLine(+1);
            return true;
        } else if (super.keyPressed(key, scanCode, modifiers)) {
            return true;
        } else {
            AdvTextInput input = this.getState().getInput();
            if (input.keyPressed(key, scanCode, modifiers)) {
                this.getState().updateLine();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char character, int key) {
        if (super.charTyped(character, key)) {
            return true;
        }
        if (SharedConstants.isAllowedCharacter(character)) {
            AdvTextInput input = this.getState().getInput();
            input.insertText(Character.toString(character));
            this.getState().updateLine();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int eventButton) {
        return super.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (this.getFocused() != null && this.isDragging()
            && this.getFocused().mouseDragged(x, y, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(x, y, button, dragX, dragY);
    }
    
    @Override
    public void setFocused(@Nullable IGuiEventListener child) {
        super.setFocused(child);
        Stream.of(this.standColorHex, this.textColorHex)
              .filter(f->f != child)
              .forEach(f->f.setFocused2(false));
    }
    
    protected boolean isRotating() {
        return this.getFocused() == this.sliderDir
            && this.sliderDir.isPressed()
            && this.isDragging();
    }
    
    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        if (!this.isRotating()) {
            this.renderBackground();
        }
        this.drawCenteredString(this.font, I18n.format("sign.edit"), this.width / 2, 40, 0xffffff);
        if (this.isRotating()) {
            this.sliderDir.render(mouseX, mouseY, renderPartialTicks);
            String tooltipKey =
                    Screen.hasShiftDown() ? "gui.%s.advanced_sign.slider_unshift_tooltip"
                                          : "gui.%s.advanced_sign.slider_shift_tooltip";
            this.drawCenteredString(this.font, I18n.format(String.format(tooltipKey, DEF.MOD_ID)),
                                    this.width / 2, this.height - 75, 0xffffff);
            return;
        }
        this.drawSign();
        int lines = this.getState().getText().getLines().size();
        String linesText = I18n.format(String.format("gui.%s.advanced_sign.lines", DEF.MOD_ID),
                                       lines, String.format("%2d", lines),
                                       String.format("%02d", lines));
        int linesTextWidth = this.font.getStringWidth(linesText);
        this.drawString(this.font, linesText,
                        this.buttonAddLine.x - 2 - linesTextWidth,
                        this.buttonAddLine.y + 10, 0xffffff);
        String stndTxt = I18n.format(String.format("gui.%s.advanced_sign.color.stand", DEF.MOD_ID));
        int standTextWidth = this.font.getStringWidth(stndTxt);
        this.drawString(this.font, stndTxt,
                        this.standColorHex.x - 2 - standTextWidth,
                        this.standColorHex.y + 3, 0xffffff);
        String textText = I18n.format(String.format("gui.%s.advanced_sign.color.text", DEF.MOD_ID));
        int textTextWidth = this.font.getStringWidth(textText);
        this.drawString(this.font, textText,
                        this.textColorHex.x - 2 - textTextWidth,
                        this.textColorHex.y + 3, 0xffffff);
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
        this.buttonToggleStick.setMessage(I18n.format(JUtils.make(()-> {
            return String.format("gui.%s.advanced_sign.stick.%s", DEF.MOD_ID,
                                 this.sign.getForm() == AdvSignForm.STICK_DOWN ? "on" : "off");
        })));
        this.buttonFlip.setMessage(I18n.format(JUtils.make(()-> {
            return String.format("gui.%s.advanced_sign.side.%s", DEF.MOD_ID,
                                 this.front ? "front" : "back");
        })));
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
