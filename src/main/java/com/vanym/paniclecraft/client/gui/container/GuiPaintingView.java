package com.vanym.paniclecraft.client.gui.container;

import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Picture;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiPaintingView extends GuiScreen {
    
    protected static final int PADDING_TOP = 20;
    protected static final int PADDING_BOTTOM = 20;
    protected static final int PADDING_LEFT = 20;
    protected static final int PADDING_RIGHT = 20;
    
    protected final ContainerPaintingViewClient view;
    
    public GuiPaintingView(ContainerPaintingViewClient view) {
        this.view = view;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.mc.thePlayer.openContainer = this.view;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        this.drawDefaultBackground();
        int width = this.width - (PADDING_LEFT + PADDING_RIGHT);
        int height = this.height - (PADDING_TOP + PADDING_BOTTOM);
        int step = Math.min(width / this.view.sizeX, height / this.view.sizeY);
        int offsetX = PADDING_LEFT + (width - (step * this.view.sizeX)) / 2;
        int offsetY = PADDING_TOP + (height - (step * this.view.sizeY)) / 2;
        for (int y = 0; y < this.view.sizeY; ++y) {
            for (int x = 0; x < this.view.sizeX; ++x) {
                Picture picture = this.view.getPicture(x, y);
                if (picture == null) {
                    continue;
                }
                int side = ForgeDirection.SOUTH.ordinal();
                IIcon icon = TileEntityPaintingRenderer.bindTexture(picture, side);
                this.drawTexturedModelRectFromIcon(offsetX + x * step, offsetY + y * step,
                                                   icon, step, step);
            }
        }
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public void onGuiClosed() {
        if (this.mc.thePlayer != null) {
            this.view.onContainerClosed(this.mc.thePlayer);
        }
    }
}
