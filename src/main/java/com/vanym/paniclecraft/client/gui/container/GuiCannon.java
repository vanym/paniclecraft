package com.vanym.paniclecraft.client.gui.container;

import java.util.stream.Stream;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.element.GuiCircularSlider;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.network.message.MessageCannonSet;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCannon extends ContainerScreen<ContainerCannon> {
    
    protected static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/gui/cannon.png");
    
    protected GuiCircularSlider sliderDir;
    protected GuiCircularSlider sliderHeight;
    protected GuiCircularSlider sliderStrength;
    
    protected final ContainerCannon container;
    
    public GuiCannon(ContainerCannon container, PlayerInventory playerInv, ITextComponent title) {
        super(container, playerInv, title);
        this.container = container;
    }
    
    @Override
    public void onClose() {
        super.onClose();
        this.container.cannon.removeItemNoUpdate(0);
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
    public void init() {
        super.init();
        this.sliderDir = new GuiCircularSlider(
                this.leftPos + this.imageWidth - 72,
                this.topPos + 12,
                60,
                60);
        this.sliderDir.setGetter(()->this.container.cannon.getDirection() / 360.0D);
        this.sliderDir.setSetter(v-> {
            v *= 32.0D;
            if (Screen.hasShiftDown()) {
                v = (double)Math.round(v);
            }
            v *= 11.25D;
            this.sendDirection(v);
        });
        this.sliderDir.setOffset(0.25D);
        this.addButton(this.sliderDir);
        this.sliderHeight = new GuiCircularSlider(this.leftPos + 8 - 30, this.topPos + 38, 60, 60);
        this.sliderHeight.setGetter(()->0.25D - this.container.cannon.getHeight() / 90.0D * 0.25D);
        this.sliderHeight.setSetter(v-> {
            v = (0.25D - v) / 0.25D;
            v *= 18.0D;
            if (Screen.hasShiftDown()) {
                v = (double)Math.round(v);
            }
            v *= 5.0D;
            this.sendHeight(v);
        });
        this.sliderHeight.setOffset(-0.25D);
        this.sliderHeight.setMax(0.25D);
        this.addButton(this.sliderHeight);
        this.sliderStrength = new GuiCircularSlider(this.leftPos + 75, this.topPos + 20, 50, 50);
        final double maxStrength = Core.instance.cannon.maxStrength.get();
        this.sliderStrength.setGetter(()->this.container.cannon.getStrength() /
                                          maxStrength * 0.125D);
        this.sliderStrength.setSetter(v-> {
            v /= 0.125D;
            v *= 10.0D;
            if (Screen.hasShiftDown()) {
                v = (double)Math.round(v);
            }
            v /= 10.0D;
            v *= maxStrength;
            this.sendStrength(v);
        });
        this.sliderStrength.setOffset(0.5D);
        this.sliderStrength.setMax(0.125D);
        this.addButton(this.sliderStrength);
    }
    
    @Override
    public void renderLabels(int x, int y) {
        this.font.draw(this.title.getColoredString(), 8, 6, 0x404040);
        this.font.draw(this.inventory.getDisplayName().getColoredString(),
                       8, this.imageHeight - 96 + 2, 0x404040);
        String directionString = I18n.get(String.format("gui.%s.cannon.direction", DEF.MOD_ID));
        this.font.draw(directionString, 62, 8, 0x404040);
        double dir = this.container.cannon.getDirection();
        this.font.draw(String.format("%.4f", dir), 62, 18, 0x404040);
        String heightString = I18n.get(String.format("gui.%s.cannon.height", DEF.MOD_ID));
        this.font.draw(heightString, 40, 48, 0x404040);
        double height = this.container.cannon.getHeight();
        this.font.draw(String.format("%.4f", height), 40, 58, 0x404040);
        String strengthString = I18n.get(String.format("gui.%s.cannon.strength", DEF.MOD_ID));
        this.font.draw(strengthString, 30, 28, 0x404040);
        double strength = this.container.cannon.getStrength();
        this.font.draw(String.format("%.4f", strength), 30, 38, 0x404040);
        if (this.isRotating()) {
            String tooltipKey = Screen.hasShiftDown() ? "gui.%s.cannon.slider_unshift_tooltip"
                                                      : "gui.%s.cannon.slider_shift_tooltip";
            this.drawCenteredString(this.font, I18n.get(String.format(tooltipKey, DEF.MOD_ID)),
                                    this.imageWidth / 2, this.imageHeight + 6, 0xd0d0d0);
        }
    }
    
    protected boolean isRotating() {
        return Stream.of(this.sliderDir, this.sliderHeight, this.sliderStrength)
                     .filter(slider->slider == this.getFocused())
                     .findAny()
                     .filter(GuiCircularSlider::isPressed)
                     .isPresent()
            && this.isDragging();
    }
    
    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, renderPartialTicks);
        this.renderTooltip(mouseX, mouseY);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean charTyped(char character, int key) {
        return super.charTyped(character, key);
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
    public void renderBg(float f, int i, int j) {
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        this.blit(this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
