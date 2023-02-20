package com.vanym.paniclecraft.client.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.network.message.MessageAdvSignChange;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

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
    protected GuiButton buttonClearText;
    protected GuiButton buttonClearColor;
    
    public GuiEditAdvSign(TileEntityAdvSign tileAS) {
        this.tileAS = tileAS;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        int xCenter = this.width / 2;
        this.buttonDone =
                new GuiButton(0, xCenter - 100, this.height / 4 + 120, I18n.format("gui.done"));
        this.buttonAddLine = new GuiButton(1, xCenter + 59, this.height / 4 + 99, 20, 20, "+");
        this.buttonRemoveLine = new GuiButton(2, xCenter + 80, this.height / 4 + 99, 20, 20, "-");
        this.buttonAddSect =
                new GuiButton(3, xCenter - 100, this.height / 4 + 99, 20, 20, "+\u00a7");
        this.buttonCopy =
                new GuiButton(4, xCenter - 79, this.height / 4 + 99, 30, 20, "Copy");
        this.buttonPaste =
                new GuiButton(5, xCenter - 48, this.height / 4 + 99, 30, 20, "Paste");
        this.buttonClearColor =
                new GuiButton(12, xCenter + 80 + 30, this.height / 4 + 15, 20, 20, "CC");
        this.buttonClearText =
                new GuiButton(13, xCenter + 59 + 30, this.height / 4 + 15, 20, 20, "CT");
        if (this.tileAS.lines.size() <= 1) {
            this.buttonRemoveLine.enabled = false;
        } else if (this.tileAS.lines.size() >= 32) {
            this.buttonAddLine.enabled = false;
        }
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.buttonDone);
        this.buttonList.add(this.buttonRemoveLine);
        this.buttonList.add(this.buttonAddLine);
        this.buttonList.add(this.buttonClearColor);
        this.buttonList.add(this.buttonClearText);
        this.buttonList.add(this.buttonAddSect);
        this.buttonList.add(this.buttonCopy);
        this.buttonList.add(this.buttonPaste);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Core.instance.network.sendToServer(new MessageAdvSignChange(this.tileAS));
    }
    
    @Override
    public void updateScreen() {
        ++this.updateCounter;
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
        } else if (button.id == this.buttonClearText.id) {
            IntStream.range(0, this.tileAS.lines.size()).forEach(i->this.tileAS.lines.set(i, ""));
        } else if (button.id == this.buttonClearColor.id) {
            this.tileAS.setColor(Color.WHITE);
        }
    }
    
    @Override
    protected void keyTyped(char character, int key) {
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
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit"), this.width / 2, 40,
                                0xffffff);
        this.drawString(this.fontRendererObj, "Lines:" + this.tileAS.lines.size(),
                        this.width / 2 + 19, this.height / 4 + 110, 0xffffff);
        Color color = this.tileAS.getColor();
        this.drawString(this.fontRendererObj, "R:" + color.getRed(),
                        this.width / 2 + 29 + 30, this.height / 4 + 47, 0xffffff);
        this.drawString(this.fontRendererObj, "G:" + color.getGreen(),
                        this.width / 2 + 29 + 30, this.height / 4 + 68, 0xffffff);
        this.drawString(this.fontRendererObj, "B:" + color.getBlue(),
                        this.width / 2 + 29 + 30, this.height / 4 + 89, 0xffffff);
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
                                                                     -0.5D, 0.0F, true,
                                                                     selectLine);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
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
