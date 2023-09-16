package com.vanym.paniclecraft.client.gui.container;

import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.network.message.MessageCannonSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiCannon extends GuiContainer {
    
    protected static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/cannon.png");
    
    protected GuiCircularSlider sliderDir;
    protected GuiCircularSlider sliderHeight;
    protected GuiCircularSlider sliderStrength;
    
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
    
    protected void sendDirection(double value) {
        Core.instance.network.sendToServer(MessageCannonSet.Field.DIRECTION.message(value));
    }
    
    protected void sendStrength(double value) {
        Core.instance.network.sendToServer(MessageCannonSet.Field.STRENGTH.message(value));
    }
    
    protected void sendHeight(double value) {
        Core.instance.network.sendToServer(MessageCannonSet.Field.HEIGHT.message(value));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.sliderDir =
                new GuiCircularSlider(1, this.guiLeft + this.xSize - 72, this.guiTop + 12, 60, 60);
        this.sliderDir.setGetter(()->this.container.cannon.getDirection() / 360.0D);
        this.sliderDir.setSetter(v-> {
            v *= 32.0D;
            if (GuiScreen.isShiftKeyDown()) {
                v = (double)Math.round(v);
            }
            v *= 11.25D;
            this.sendDirection(v);
        });
        this.sliderDir.setOffset(0.25D);
        this.buttonList.add(this.sliderDir);
        this.sliderHeight =
                new GuiCircularSlider(2, this.guiLeft + 8 - 30, this.guiTop + 38, 60, 60);
        this.sliderHeight.setGetter(()->0.25D - this.container.cannon.getHeight() / 90.0D * 0.25D);
        this.sliderHeight.setSetter(v-> {
            v = (0.25D - v) / 0.25D;
            v *= 18.0D;
            if (GuiScreen.isShiftKeyDown()) {
                v = (double)Math.round(v);
            }
            v *= 5.0D;
            this.sendHeight(v);
        });
        this.sliderHeight.setOffset(-0.25D);
        this.sliderHeight.setMax(0.25D);
        this.buttonList.add(this.sliderHeight);
        this.sliderStrength = new GuiCircularSlider(3, this.guiLeft + 75, this.guiTop + 20, 50, 50);
        final double maxStrength = Core.instance.cannon.config.maxStrength;
        this.sliderStrength.setGetter(()->this.container.cannon.getStrength() /
                                          maxStrength * 0.125D);
        this.sliderStrength.setSetter(v-> {
            v /= 0.125D;
            v *= 10.0D;
            if (GuiScreen.isShiftKeyDown()) {
                v = (double)Math.round(v);
            }
            v /= 10.0D;
            v *= maxStrength;
            this.sendStrength(v);
        });
        this.sliderStrength.setOffset(0.5D);
        this.sliderStrength.setMax(0.125D);
        this.buttonList.add(this.sliderStrength);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRendererObj.drawString(InventoryUtils.getTranslatedName(this.container.cannon),
                                        8, 6, 0x404040);
        this.fontRendererObj.drawString(InventoryUtils.getTranslatedName(this.container.playerInv),
                                        8, this.ySize - 96 + 2, 0x404040);
        String directionString = I18n.format(String.format("gui.%s.cannon.direction", DEF.MOD_ID));
        this.fontRendererObj.drawString(directionString, 62, 8, 0x404040);
        double direction = this.container.cannon.getDirection();
        this.fontRendererObj.drawString(String.format("%.4f", direction), 62, 18, 0x404040);
        String heightString = I18n.format(String.format("gui.%s.cannon.height", DEF.MOD_ID));
        this.fontRendererObj.drawString(heightString, 40, 48, 0x404040);
        double height = this.container.cannon.getHeight();
        this.fontRendererObj.drawString(String.format("%.4f", height), 40, 58, 0x404040);
        String strengthString = I18n.format(String.format("gui.%s.cannon.strength", DEF.MOD_ID));
        this.fontRendererObj.drawString(strengthString, 30, 28, 0x404040);
        double strength = this.container.cannon.getStrength();
        this.fontRendererObj.drawString(String.format("%.4f", strength), 30, 38, 0x404040);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    
    @Override
    public void keyTyped(char character, int key) {
        super.keyTyped(character, key);
    }
    
    @Override
    public void mouseClicked(int x, int y, int eventButton) {
        super.mouseClicked(x, y, eventButton);
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
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
