package com.vanym.paniclecraft.client.gui.container;

import java.awt.Color;
import java.util.Arrays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.ColorChartTexture;
import com.vanym.paniclecraft.client.gui.element.GuiHexColorField;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.network.message.MessagePaletteSetColor;
import com.vanym.paniclecraft.utils.ColorUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPalette extends ContainerScreen<ContainerPalette> implements IContainerListener {
    
    protected static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/palette.png");
    protected static final ResourceLocation CHART_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/palette_color_chart.png");
    protected static ColorChartTexture chartTexture;
    
    protected final GuiOneColorField[] textColor = new GuiOneColorField[3];
    protected GuiHexColorField textHex;
    protected GuiColorChart chart;
    protected GuiColorPicker picker;
    
    public GuiPalette(ContainerPalette container, PlayerInventory playerInv, ITextComponent title) {
        super(container, playerInv, title);
    }
    
    @Override
    public void init() {
        super.init();
        if (chartTexture == null) {
            chartTexture = new ColorChartTexture(CHART_TEXTURE);
            this.minecraft.getTextureManager().loadTexture(CHART_TEXTURE, chartTexture);
        }
        this.chart =
                new GuiColorChart(chartTexture, this.guiLeft, this.guiTop, this.xSize, this.ySize);
        this.children.add(this.chart);
        this.picker = new GuiColorPicker(this.guiLeft + 8, this.guiTop + 38, 16, 16);
        this.children.add(this.picker);
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        for (int i = 0; i < this.textColor.length; ++i) {
            this.textColor[i] = new GuiOneColorField(
                    this.font,
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
            this.addButton(this.textColor[i]);
        }
        this.textHex = new GuiHexColorField(
                this.font,
                this.guiLeft + 8,
                this.guiTop + 58);
        this.textHex.setSetter(rgb->this.sendColor(new Color(rgb)));
        this.addButton(this.textHex);
        this.container.removeListener(this);
        this.container.addListener(this);
    }
    
    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.container.removeListener(this);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.children()
            .stream()
            .filter(TextFieldWidget.class::isInstance)
            .map(TextFieldWidget.class::cast)
            .forEach(TextFieldWidget::tick);
    }
    
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return super.keyPressed(key, scanCode, modifiers)
            || this.switchTextTyped(key, scanCode, modifiers);
    }
    
    protected boolean switchTextTyped(int key, int scanCode, int modifiers) {
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
            GuiOneColorField textOne = this.textColor[i];
            if (!textOne.isFocused()) {
                continue;
            }
            int sel = textOne.getSelectionEnd();
            textOne.changeFocus(false);
            int move = i + (up ? 1 : -1);
            if (move < 0 || move > last) {
                this.textHex.changeFocus(true);
            } else {
                this.textColor[move].changeFocus(true);
                this.textColor[move].setCursorPosition(sel);
            }
            return true;
        }
        if (this.textHex.isFocused()) {
            this.textHex.changeFocus(false);
            this.textColor[up ? 0 : last].changeFocus(true);
            this.setFocused(this.textColor[up ? 0 : last]);
            return true;
        }
        return false;
    }
    
    protected boolean textColorKeyTyped(int i, int key, int scanCode, int modifiers) {
        GuiOneColorField textOne = this.textColor[i];
        String previousText = textOne.getText();
        if (!textOne.keyPressedParent(key, scanCode, modifiers)) {
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
        this.sendColor(new Color(rgb));
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int eventButton) {
        return super.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, renderPartialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawInventoriesNames() {
        this.font.drawString(this.title.getFormattedText(), 8, 6, 0x404040);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),
                             8, this.ySize - 96 + 2, 0x404040);
    }
    
    protected void drawRGBLabels() {
        final String letters = "BGR";
        for (int i = 0; i < this.textColor.length; ++i) {
            GuiOneColorField field = this.textColor[i];
            int yoffset = (field.getAdjustedWidth() - field.getWidth()) / -4;
            this.font.drawString(letters.charAt(i) + ": ",
                                 -this.guiLeft + field.x - 11,
                                 -this.guiTop + field.y + yoffset,
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
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.chart.render(mouseX, mouseY, partialTicks);
        Color color = this.getColor();
        if (color == null) {
            color = new Color(0);
        }
        fill(this.picker.xPosition, this.picker.yPosition,
             this.picker.xPosition + this.picker.width,
             this.picker.yPosition + this.picker.height,
             color.getRGB());
        RenderHelper.disableStandardItemLighting();
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
    
    protected class GuiColorPicker extends AbstractGui implements IGuiEventListener {
        
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
        
        @Override
        public boolean mouseClicked(double x, double y, int eventButton) {
            if (!this.enabled || (eventButton != 0 /* left */ && eventButton != 1 /* right */)) {
                return false;
            }
            x -= this.xPosition;
            y -= this.yPosition;
            if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
                return false;
            }
            ItemStack stack = GuiPalette.this.container.inventoryPlayer.getItemStack();
            IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
            if (colorizeable == null) {
                return false;
            }
            int rgb = colorizeable.getColor(stack);
            GuiPalette.this.sendColor(new Color(rgb));
            return true;
        }
    }
    
    protected class GuiColorChart extends AbstractGui implements IRenderable, IGuiEventListener {
        
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
        
        @Override
        public boolean mouseClicked(double x, double y, int eventButton) {
            if (!this.enabled || eventButton != 0 /* left */) {
                return false;
            }
            x -= this.xPosition;
            y -= this.yPosition;
            if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
                return false;
            }
            Color color = this.chart.getColor((int)x, (int)y);
            if (color == null || color.getAlpha() == 0) {
                return false;
            }
            GuiPalette.this.sendColor(color);
            return true;
        }
        
        @Override
        public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
            if (!this.visible) {
                return;
            }
            this.chart.bindTexture();
            this.blit(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
        }
    }
    
    protected class GuiOneColorField extends TextFieldWidget {
        
        protected static final String NUM_CHARS = "0123456789";
        
        public GuiOneColorField(FontRenderer font, int x, int y, int width, int height) {
            super(font, x, y, width, height, "");
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
        
        protected int getSelectionEnd() {
            return this.selectionEnd;
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
        public boolean keyPressed(int key, int scanCode, int modifiers) {
            return GuiPalette.this.textColorKeyTyped(Arrays.asList(GuiPalette.this.textColor)
                                                           .indexOf(this),
                                                     key, scanCode,
                                                     modifiers);
        }
        
        public boolean keyPressedParent(int key, int scanCode, int modifiers) {
            return super.keyPressed(key, scanCode, modifiers);
        }
        
        @Override
        public boolean charTyped(char character, int key) {
            if (!super.charTyped(character, key)) {
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
