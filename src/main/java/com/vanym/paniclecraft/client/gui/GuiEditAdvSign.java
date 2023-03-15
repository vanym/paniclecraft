package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.ColorUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;

@SideOnly(Side.CLIENT)
public class GuiEditAdvSign extends GuiScreen {
    
    protected final TileEntityAdvSign tileAS;
    
    protected int editLine = 0;
    protected int updateCounter;
    
    protected GuiButton buttonDone;
    protected GuiButton buttonCopy;
    protected GuiButton buttonPaste;
    protected GuiButton buttonAddSect;
    protected GuiButton buttonAddLine;
    protected GuiButton buttonRemoveLine;
    protected GuiButton buttonToggleStick;
    
    protected GuiCircularSlider sliderDir;
    
    protected GuiHexColorField standColorHex;
    protected GuiHexColorField textColorHex;
    
    public GuiEditAdvSign(TileEntityAdvSign tileAS) {
        this.tileAS = tileAS;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        int xCenter = this.width / 2;
        this.buttonDone =
                new GuiButton(0, xCenter - 100, this.height / 4 + 120, I18n.format("gui.done"));
        this.buttonAddLine = new GuiButton(1, xCenter + 59, this.height / 4 + 68, 20, 20, "+");
        this.buttonRemoveLine = new GuiButton(
                2,
                this.buttonAddLine.xPosition + 21,
                this.buttonAddLine.yPosition,
                20,
                20,
                "-");
        this.buttonAddSect =
                new GuiButton(3, xCenter - 100, this.height / 4 + 99, 20, 20, "+\u00a7");
        this.buttonCopy =
                new GuiButton(4, xCenter - 79, this.height / 4 + 99, 30, 20, "Copy");
        this.buttonPaste =
                new GuiButton(5, xCenter - 48, this.height / 4 + 99, 30, 20, "Paste");
        this.buttonToggleStick =
                new GuiButton(14, xCenter - 100, this.height / 4 + 78, 70, 20, "Toggle Stick");
        this.lineButtonsUpdate();
        this.sliderDir = new GuiCircularSlider(15, xCenter - 100, this.height / 4 + 36, 40, 40);
        this.sliderDir.setGetter(()->this.tileAS.getDirection() / 360.0D);
        this.sliderDir.setSetter(v-> {
            v *= 16.0D;
            if (GuiScreen.isShiftKeyDown()) {
                v = (double)Math.round(v);
            }
            v *= 22.5D;
            v = (double)Math.round(v);
            this.tileAS.setDirection(v);
        });
        this.sliderDir.setOffset(-0.25D);
        this.standColorHex =
                new GuiHexColorField(
                        this.fontRendererObj,
                        xCenter + 48,
                        this.height / 4 + 106);
        this.standColorHex.setRGB(ColorUtils.getAlphaless(this.tileAS.getStandColor()));
        this.standColorHex.setSetter(rgb->this.tileAS.setStandColor(new Color(rgb)));
        this.textColorHex =
                new GuiHexColorField(this.fontRendererObj, xCenter + 48, this.height / 4 + 90);
        this.textColorHex.setRGB(ColorUtils.getAlphaless(this.tileAS.getTextColor()));
        this.textColorHex.setSetter(rgb->this.tileAS.setTextColor(new Color(rgb)));
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.buttonDone);
        this.buttonList.add(this.buttonRemoveLine);
        this.buttonList.add(this.buttonAddLine);
        this.buttonList.add(this.buttonAddSect);
        this.buttonList.add(this.buttonCopy);
        this.buttonList.add(this.buttonPaste);
        this.buttonList.add(this.buttonToggleStick);
        this.buttonList.add(this.sliderDir);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Core.instance.network.sendToServer(new MessageAdvSignChange(this.tileAS));
    }
    
    @Override
    public void updateScreen() {
        ++this.updateCounter;
        this.standColorHex.updateCursorCounter();
        this.textColorHex.updateCursorCounter();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) {
            return;
        }
        if (button.id == this.buttonDone.id) {
            this.mc.displayGuiScreen(null);
        } else if (button.id == this.buttonAddLine.id) {
            if (this.tileAS.lines.size() >= TileEntityAdvSign.MAX_LINES) {
                return;
            }
            this.tileAS.lines.add("");
            this.lineButtonsUpdate();
        } else if (button.id == this.buttonRemoveLine.id) {
            if (this.tileAS.lines.size() <= 1) {
                return;
            }
            int last = this.tileAS.lines.size() - 1;
            this.tileAS.lines.remove(last);
            this.editLine = Math.min(this.editLine, last - 1);
            IntStream.range(0, this.tileAS.lines.size()).forEach(this::refitText);
            this.lineButtonsUpdate();
        } else if (button.id == this.buttonAddSect.id) {
            this.addText("\u00a7");
        } else if (button.id == this.buttonCopy.id) {
            GuiScreen.setClipboardString(String.join(System.lineSeparator(), this.tileAS.lines));
        } else if (button.id == this.buttonPaste.id) {
            this.pasteFull(GuiScreen.getClipboardString());
            this.lineButtonsUpdate();
        } else if (button.id == this.buttonToggleStick.id) {
            this.tileAS.setStick(!this.tileAS.onStick());
        }
    }
    
    @Override
    protected void keyTyped(char character, int key) {
        if (this.standColorHex.textboxKeyTyped(character, key)
            || this.textColorHex.textboxKeyTyped(character, key)) {
            return;
        }
        if (key == 1) {
            this.actionPerformed(this.buttonDone);
            return;
        }
        switch (character) {
            case 3: // Ctrl+c
                GuiScreen.setClipboardString(this.getCurrentLine());
            break;
            case 22: // Ctrl+v
                this.addText(GuiScreen.getClipboardString());
            break;
            case 24: // Ctrl+x
                GuiScreen.setClipboardString(this.getCurrentLine());
                this.setCurrentLine("");
            break;
            default:
                switch (key) {
                    case 14: { // backspace
                        String line = this.getCurrentLine();
                        if (line.length() > 0) {
                            if (GuiScreen.isCtrlKeyDown()) {
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
                            if (GuiScreen.isCtrlKeyDown()) {
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
    }
    
    @Override
    protected void mouseClicked(int x, int y, int eventButton) {
        super.mouseClicked(x, y, eventButton);
        this.standColorHex.mouseClicked(x, y, eventButton);
        this.textColorHex.mouseClicked(x, y, eventButton);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void mouseClickMove(int x, int y, int button, long timeSinceMouseClick) {
        super.mouseClickMove(x, y, button, timeSinceMouseClick);
        Stream<GuiCircularSlider> sliders = this.buttonList.stream()
                                                           .filter(GuiCircularSlider.class::isInstance)
                                                           .map(GuiCircularSlider.class::cast);
        sliders.forEach(s->s.mouseDragged(this.mc, x, y));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        if (!this.sliderDir.isPressed()) {
            this.drawDefaultBackground();
        }
        this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit"), this.width / 2, 40,
                                0xffffff);
        if (this.sliderDir.isPressed()) {
            this.sliderDir.drawButton(this.mc, mouseX, mouseY);
            return;
        }
        this.drawSign();
        String linesText = String.format("Lines:%2d", this.tileAS.lines.size());
        int linesTextWidth = this.fontRendererObj.getStringWidth(linesText);
        this.drawString(this.fontRendererObj, linesText,
                        this.buttonAddLine.xPosition - 2 - linesTextWidth,
                        this.buttonAddLine.yPosition + 10, 0xffffff);
        String standText = "Stand:";
        int standTextWidth = this.fontRendererObj.getStringWidth(standText);
        this.drawString(this.fontRendererObj, standText,
                        this.standColorHex.xPosition - 2 - standTextWidth,
                        this.standColorHex.yPosition + 3, 0xffffff);
        this.standColorHex.drawTextBox();
        String textText = "Text:";
        int textTextWidth = this.fontRendererObj.getStringWidth(textText);
        this.drawString(this.fontRendererObj, textText,
                        this.textColorHex.xPosition - 2 - textTextWidth,
                        this.textColorHex.yPosition + 3, 0xffffff);
        this.textColorHex.drawTextBox();
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    protected void drawSign() {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2, 0.0F, 50.0F);
        float scale = 93.75F;
        GL11.glScalef(-scale, -scale, -scale);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        
        int selectLine = -1;
        if (this.updateCounter / 6 % 2 == 0) {
            selectLine = this.editLine;
        }
        
        Core.instance.advSign.tileAdvSignRenderer.renderTileEntityAt(this.tileAS, -0.5D, -0.75D,
                                                                     -0.5D, 0.0F, true, false,
                                                                     selectLine);
        GL11.glPopMatrix();
    }
    
    protected void lineButtonsUpdate() {
        this.buttonAddLine.enabled = (this.tileAS.lines.size() < TileEntityAdvSign.MAX_LINES);
        this.buttonRemoveLine.enabled = (this.tileAS.lines.size() > 1);
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
            if (c != '\u00a7' && !ChatAllowedCharacters.isAllowedCharacter(c)) {
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
        return this.fontRendererObj.getStringWidth(line.toString()) <= this.getMaxLineFontWidth();
    }
}
