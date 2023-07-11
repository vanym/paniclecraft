package com.vanym.paniclecraft.client.gui.container;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.ColorChartTexture;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.client.gui.element.GuiOneColorField;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
            new ResourceLocation(DEF.MOD_ID, "textures/gui/palette.png");
    protected static final ResourceLocation CHART_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/palette_color_chart.png");
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
            GuiOneColorField textOne = this.textColor[i] = new GuiOneColorField(
                    i + 2,
                    this.fontRenderer,
                    this.guiLeft + 40,
                    this.guiTop + 42 - i * 12,
                    26,
                    12);
            int offset = i * 8;
            textOne.setSetter(color-> {
                int rgb = ColorUtils.getAlphaless(this.getColor());
                rgb &= ~(0xff << offset);
                rgb |= color << offset;
                this.sendColor(new Color(rgb));
            });
            int base = 0x555555;
            base |= 0xFF << (i * 8);
            int disabled = 0xAA << (i * 8);
            textOne.setTextColor(base);
            textOne.setDisabledTextColour(disabled);
        }
        this.textHex = new GuiHexColorField(
                1,
                this.fontRenderer,
                this.guiLeft + 8,
                this.guiTop + 58);
        this.textHex.setSetter(rgb->this.sendColor(new Color(rgb)));
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
            if (this.textColor[i].textboxKeyTyped(character, key)) {
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
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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
        RenderHelper.disableStandardItemLighting();
        this.drawInventoriesNames();
        this.drawRGBLabels();
        RenderHelper.enableGUIStandardItemLighting();
    }
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
        RenderHelper.disableStandardItemLighting();
        this.textHex.drawTextBox();
        Arrays.stream(this.textColor).forEach(t->t.drawTextBox());
    }
    
    protected void sendColor(Color color) {
        Core.instance.network.sendToServer(new MessagePaletteSetColor(color));
    }
    
    protected Color getColor() {
        return this.container.getColor();
    }
    
    protected void updateText(ItemStack stack) {
        boolean empty = stack.isEmpty();
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
            GuiPalette.this.sendColor(new Color(rgb));
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
            GuiPalette.this.sendColor(color);
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
}
