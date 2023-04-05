package com.vanym.paniclecraft.client.gui.container;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.ColorChartTexture;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPalette extends GuiContainer implements IContainerListener {
    
    protected static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/palette.png");
    protected static final ResourceLocation CHART_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/palette_color_chart.png");
    protected static ColorChartTexture chartTexture;
    
    protected final GuiOneColorField[] textColor = new GuiOneColorField[3];
    protected GuiHexColorField textHex;
    protected GuiColorChart chart;
    protected GuiColorPicker picker;
    
    protected final ContainerPalette container;
    
    public GuiPalette(ContainerPalette container) {
        super(container);
        this.container = container;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (chartTexture == null) {
            chartTexture = new ColorChartTexture(CHART_TEXTURE);
            this.mc.getTextureManager().loadTexture(CHART_TEXTURE, chartTexture);
        }
        this.chart =
                new GuiColorChart(chartTexture, this.guiLeft, this.guiTop, this.xSize, this.ySize);
        this.picker = new GuiColorPicker(this.guiLeft + 8, this.guiTop + 38, 16, 16);
        Keyboard.enableRepeatEvents(true);
        for (int i = 0; i < this.textColor.length; ++i) {
            this.textColor[i] = new GuiOneColorField(
                    i + 2,
                    this.fontRenderer,
                    this.guiLeft + 40,
                    this.guiTop + 42 - i * 12,
                    26,
                    12);
            int base = 0x555555;
            base |= 0xFF << (i * 8);
            int disabled = 0xAA << (i * 8);
            this.textColor[i].setTextColor(base);
            this.textColor[i].setDisabledTextColour(disabled);
            this.textColor[i].setMaxStringLength(3);
            this.textColor[i].setEnableBackgroundDrawing(true);
        }
        this.textHex = new GuiHexColorField(
                1,
                this.fontRenderer,
                this.guiLeft + 8,
                this.guiTop + 58);
        this.textHex.setSetter(rgb->this.setColor(new Color(rgb)));
        this.container.removeListener(this);
        this.container.addListener(this);
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.container.removeListener(this);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textHex.updateCursorCounter();
        Arrays.stream(this.textColor).forEach(t->t.updateCursorCounter());
    }
    
    @Override
    protected void keyTyped(char character, int key) throws IOException {
        if (this.textHex.textboxKeyTyped(character, key)) {
            return;
        }
        for (int i = 0; i < this.textColor.length; ++i) {
            if (this.textColorKeyTyped(i, character, key)) {
                return;
            }
        }
        if (this.switchTextTyped(character, key)) {
            return;
        }
        super.keyTyped(character, key);
    }
    
    protected boolean switchTextTyped(char character, int key) {
        boolean up;
        switch (key) {
            case 200: // up
                up = true;
            break;
            case 15: // tab
            case 28: // enter
            case 156: // enter numpad
            case 208: // down
                up = false;
            break;
            default:
                return false;
        }
        int last = this.textColor.length - 1;
        for (int i = 0; i <= last; ++i) {
            GuiTextField textOne = this.textColor[i];
            if (!textOne.isFocused()) {
                continue;
            }
            int sel = textOne.getSelectionEnd();
            textOne.setFocused(false);
            int move = i + (up ? 1 : -1);
            if (move < 0 || move > last) {
                this.textHex.setFocused(true);
            } else {
                this.textColor[move].setFocused(true);
                this.textColor[move].setCursorPosition(sel);
            }
            return true;
        }
        if (this.textHex.isFocused()) {
            this.textHex.setFocused(false);
            this.textColor[up ? 0 : last].setFocused(true);
            return true;
        }
        return false;
    }
    
    protected boolean textColorKeyTyped(int i, char character, int key) {
        GuiOneColorField textOne = this.textColor[i];
        String previousText = textOne.getText();
        if (!textOne.textboxKeyTyped(character, key)) {
            return false;
        }
        String text = textOne.getText();
        if (previousText.equals(text)) {
            return true;
        }
        int previousColor;
        int color;
        try {
            previousColor = Integer.decode(previousText);
        } catch (NumberFormatException e) {
            previousColor = 0;
        }
        try {
            color = Integer.decode(text);
        } catch (NumberFormatException e) {
            color = 0;
        }
        if (color == previousColor) {
            return true;
        }
        int rgb = ColorUtils.getAlphaless(this.getColor());
        rgb &= ~(0xff << (i * 8));
        rgb |= color << (i * 8);
        this.setColor(new Color(rgb));
        return true;
    }
    
    @Override
    protected void mouseClicked(int x, int y, int eventButton) throws IOException {
        this.textHex.mouseClicked(x, y, eventButton);
        Arrays.stream(this.textColor).forEach(t->t.mouseClicked(x, y, eventButton));
        super.mouseClicked(x, y, eventButton);
        this.chart.mouseClicked(x, y, eventButton);
        this.picker.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    protected void drawInventoriesNames() {
        String palette = InventoryUtils.getTranslatedName(this.container.inventoryPalette);
        this.fontRenderer.drawString(palette, 8, 6, 0x404040);
        String player = InventoryUtils.getTranslatedName(this.container.inventoryPlayer);
        this.fontRenderer.drawString(player, 8, this.ySize - 96 + 2, 0x404040);
    }
    
    protected void drawRGBLabels() {
        final String letters = "BGR";
        for (int i = 0; i < this.textColor.length; ++i) {
            int yoffset = this.textColor[i].getEnableBackgroundDrawing() ? 2 : 0;
            this.fontRenderer.drawString(letters.charAt(i) + ": ",
                                         -this.guiLeft + this.textColor[i].x - 11,
                                         -this.guiTop + this.textColor[i].y + yoffset,
                                         0x404040);
        }
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        this.drawInventoriesNames();
        this.drawRGBLabels();
    }
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.chart.drawChart(this.mc);
        Color color = this.getColor();
        if (color == null) {
            color = new Color(0);
        }
        drawRect(this.picker.xPosition, this.picker.yPosition,
                 this.picker.xPosition + this.picker.width,
                 this.picker.yPosition + this.picker.height,
                 color.getRGB());
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.textHex.drawTextBox();
        Arrays.stream(this.textColor).forEach(t->t.drawTextBox());
    }
    
    protected void setColor(Color color) {
        Core.instance.network.sendToServer(new MessagePaletteSetColor(color));
    }
    
    protected Color getColor() {
        return this.container.getColor();
    }
    
    protected void updateText(ItemStack stack) {
        boolean empty = (stack == null);
        Color color = this.container.getColor();
        if (color == null) {
            color = new Color(0);
        }
        int rgb = ColorUtils.getAlphaless(this.container.getColor());
        this.textHex.setEnabled(!empty);
        if (empty || !this.textHex.isFocused()) {
            this.textHex.setRGB(rgb);
            this.textHex.setFocused(false);
        }
        for (int i = 0; i < this.textColor.length; ++i) {
            this.textColor[i].setEnabled(!empty);
            if (empty || !this.textColor[i].isFocused()) {
                this.textColor[i].setText(Integer.toString((rgb >> i * 8) & 0xFF));
                this.textColor[i].setFocused(false);
            }
        }
        this.chart.enabled = !empty;
    }
    
    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> list) {
        this.sendSlotContents(container, 0, list.get(0));
    }
    
    @Override
    public void sendSlotContents(Container container, int slot, ItemStack stack) {
        if (slot != 0) {
            return;
        }
        this.updateText(stack);
    }
    
    @Override
    public void sendWindowProperty(Container container, int id, int level) {}
    
    @Override
    public void sendAllWindowProperties(Container container, IInventory inv) {
        this.sendSlotContents(container, 0, inv.getStackInSlot(0));
    }
    
    protected class GuiColorPicker extends Gui {
        
        public int width;
        public int height;
        public int xPosition;
        public int yPosition;
        
        public boolean enabled;
        
        public GuiColorPicker(int x, int y, int width, int height) {
            this.xPosition = x;
            this.yPosition = y;
            this.width = width;
            this.height = height;
            this.enabled = true;
        }
        
        public void mouseClicked(int x, int y, int eventButton) {
            if (!this.enabled || (eventButton != 0 /* left */ && eventButton != 1 /* right */)) {
                return;
            }
            x -= this.xPosition;
            y -= this.yPosition;
            if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
                return;
            }
            ItemStack stack = GuiPalette.this.container.inventoryPlayer.getItemStack();
            IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
            if (colorizeable == null) {
                return;
            }
            int rgb = colorizeable.getColor(stack);
            GuiPalette.this.setColor(new Color(rgb));
        }
    }
    
    protected class GuiColorChart extends Gui {
        
        public int width;
        public int height;
        public int xPosition;
        public int yPosition;
        
        public boolean enabled;
        public boolean visible;
        
        protected final ColorChartTexture chart;
        
        public GuiColorChart(ColorChartTexture chart, int x, int y, int width, int height) {
            this.chart = chart;
            this.xPosition = x;
            this.yPosition = y;
            this.width = width;
            this.height = height;
            this.enabled = true;
            this.visible = true;
        }
        
        public void mouseClicked(int x, int y, int eventButton) {
            if (!this.enabled || eventButton != 0 /* left */) {
                return;
            }
            x -= this.xPosition;
            y -= this.yPosition;
            if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
                return;
            }
            Color color = this.chart.getColor(x, y);
            if (color == null || color.getAlpha() == 0) {
                return;
            }
            GuiPalette.this.setColor(color);
        }
        
        public void drawChart(Minecraft mc) {
            if (!this.visible) {
                return;
            }
            mc.getTextureManager().bindTexture(this.chart.textureLocation);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width,
                                       this.height);
        }
    }
    
    protected static class GuiOneColorField extends GuiTextField {
        
        protected static final String NUM_CHARS = "0123456789";
        
        public GuiOneColorField(int id, FontRenderer font, int x, int y, int width, int height) {
            super(id, font, x, y, width, height);
        }
        
        @Override
        public void setFocused(boolean focus) {
            super.setFocused(focus);
            if (focus) {
                return;
            }
            int num;
            try {
                num = Integer.decode(this.getText());
            } catch (NumberFormatException e) {
                num = 0;
            }
            this.setText(Integer.toString(num));
        }
        
        @Override
        public void writeText(String text) {
            StringBuilder sb = new StringBuilder();
            char[] chars = text.toCharArray();
            for (char c : chars) {
                if (NUM_CHARS.indexOf(c) == -1) {
                    continue;
                }
                sb.append(c);
            }
            super.writeText(sb.toString());
        }
        
        @Override
        public boolean textboxKeyTyped(char character, int key) {
            if (!super.textboxKeyTyped(character, key)) {
                return false;
            }
            this.checkNum();
            return true;
        }
        
        protected boolean checkNum() {
            String text = this.getText();
            if (text.isEmpty()) {
                return false;
            }
            int pos = this.getCursorPosition();
            int sel = this.getSelectionEnd();
            int num;
            try {
                num = Integer.decode(text);
            } catch (NumberFormatException e) {
                return false;
            }
            if (num > 0xff) {
                this.setText(Integer.toString(0xff));
                this.setCursorPosition(pos);
                this.setSelectionPos(sel);
                return true;
            }
            if (num < 0) {
                this.setText(Integer.toString(0));
                return true;
            }
            return false;
        }
    }
}
