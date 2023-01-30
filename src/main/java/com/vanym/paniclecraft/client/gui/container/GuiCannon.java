package com.vanym.paniclecraft.client.gui.container;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.network.message.MessageCannonSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiCannon extends GuiContainer {
    
    protected static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/cannonGui.png");
    
    protected GuiTextField textDirection;
    protected GuiTextField textHeight;
    protected GuiTextField textStrength;
    
    protected final ContainerCannon container;
    
    public GuiCannon(ContainerCannon container) {
        super(container);
        this.container = container;
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.container.cannon.getStackInSlotOnClosing(0);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.textDirection =
                new GuiTextField(
                        this.fontRendererObj,
                        this.guiLeft + 132,
                        this.guiTop + 18,
                        35,
                        15);
        this.textHeight =
                new GuiTextField(
                        this.fontRendererObj,
                        this.guiLeft + 132,
                        this.guiTop + 18 + 15,
                        35,
                        15);
        this.textStrength =
                new GuiTextField(
                        this.fontRendererObj,
                        this.guiLeft + 132,
                        this.guiTop + 18 + 30,
                        35,
                        15);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRendererObj.drawString(InventoryUtils.getTranslatedName(this.container.cannon),
                                        8, 6, 0x404040);
        this.fontRendererObj.drawString(InventoryUtils.getTranslatedName(this.container.playerInv),
                                        8, this.ySize - 96 + 2, 0x404040);
        
        this.fontRendererObj.drawString(I18n.format("gui.cannon.direction",
                                                    this.container.cannon.getDirection()),
                                        35, 22, 0x404040);
        this.fontRendererObj.drawString(I18n.format("gui.cannon.height",
                                                    this.container.cannon.getHeight()),
                                        35, 22 + 15, 0x404040);
        this.fontRendererObj.drawString(I18n.format("gui.cannon.strength",
                                                    this.container.cannon.getStrength()),
                                        35, 22 + 30, 0x404040);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
        this.textDirection.drawTextBox();
        this.textHeight.drawTextBox();
        this.textStrength.drawTextBox();
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textDirection.updateCursorCounter();
        this.textHeight.updateCursorCounter();
        this.textStrength.updateCursorCounter();
    }
    
    @Override
    public void keyTyped(char character, int key) {
        super.keyTyped(character, key);
        this.textDirection.textboxKeyTyped(character, key);
        this.textHeight.textboxKeyTyped(character, key);
        this.textStrength.textboxKeyTyped(character, key);
        if (key == 28 /* enter */ || key == 156 /* enter numpad */) {
            String text = null;
            MessageCannonSet.Field bt = null;
            if (this.textDirection.isFocused()) {
                text = this.textDirection.getText();
                bt = MessageCannonSet.Field.DIRECTION;
            }
            if (this.textHeight.isFocused()) {
                text = this.textHeight.getText();
                bt = MessageCannonSet.Field.HEIGHT;
            }
            if (this.textStrength.isFocused()) {
                text = this.textStrength.getText();
                bt = MessageCannonSet.Field.STRENGTH;
            }
            if (bt != null) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(text);
                    Core.instance.network.sendToServer(new MessageCannonSet(bt, amount));
                } catch (NumberFormatException e) {
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(int x, int y, int eventButton) {
        super.mouseClicked(x, y, eventButton);
        this.textDirection.mouseClicked(x, y, eventButton);
        this.textHeight.mouseClicked(x, y, eventButton);
        this.textStrength.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
