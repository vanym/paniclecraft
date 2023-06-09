package com.vanym.paniclecraft.client.gui.container;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.client.utils.ImageSelection;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Picture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPaintingView extends GuiScreen {
    
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    
    protected static final int PADDING_TOP = 20;
    protected static final int PADDING_BOTTOM = 30;
    protected static final int PADDING_LEFT = 20;
    protected static final int PADDING_RIGHT = 20;
    
    protected final ContainerPaintingViewClient view;
    
    protected int viewX;
    protected int viewY;
    protected int viewStep;
    
    protected int controlsX;
    protected int controlsEndX;
    
    protected final GuiButton buttonExport =
            new GuiButton(1, 0, 0, 60, 20, I18n.format("gui.paintingview.export"));
    
    public GuiPaintingView(ContainerPaintingViewClient view) {
        this.view = view;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.mc.player.openContainer = this.view;
        this.buttonList.add(this.buttonExport);
    }
    
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        int viewMaxWidth = this.width - (PADDING_LEFT + PADDING_RIGHT);
        int viewMaxHeight = this.height - (PADDING_TOP + PADDING_BOTTOM);
        this.viewStep = Math.min(viewMaxWidth / this.view.sizeX, viewMaxHeight / this.view.sizeY);
        this.viewX = PADDING_LEFT + (viewMaxWidth - (this.viewStep * this.view.sizeX)) / 2;
        this.viewY = PADDING_TOP + (viewMaxHeight - (this.viewStep * this.view.sizeY)) / 2;
        int center = (width / 2);
        this.controlsX = Math.min(this.viewX, center - 100);
        this.controlsEndX = Math.max(this.getViewEndX(), center + 100);
        this.buttonExport.x = this.controlsEndX - this.buttonExport.width;
        this.buttonExport.y = height - this.buttonExport.height - 5;
    }
    
    protected int getViewWidth() {
        return this.viewStep * this.view.sizeX;
    }
    
    protected int getViewHeight() {
        return this.viewStep * this.view.sizeY;
    }
    
    protected int getViewEndX() {
        return this.viewX + this.getViewWidth();
    }
    
    protected int getViewEndY() {
        return this.viewY + this.getViewHeight();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        this.drawDefaultBackground();
        this.drawPainting();
        {
            StringBuilder sb = new StringBuilder();
            sb.append(this.view.getWidth());
            sb.append("×");
            sb.append(this.view.getHeight());
            this.fontRenderer.drawString(sb.toString(), 2, 2, 0x7f7f7f);
        }
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }
    
    protected void drawPainting() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                                 GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        for (int y = 0; y < this.view.sizeY; ++y) {
            for (int x = 0; x < this.view.sizeX; ++x) {
                Picture picture = this.view.getPicture(x, y);
                if (picture == null) {
                    continue;
                }
                TextureAtlasSprite icon = TileEntityPaintingRenderer.bindTexture(picture);
                this.drawTexturedModalRect(this.viewX + x * this.viewStep,
                                           this.viewY + y * this.viewStep,
                                           icon, this.viewStep, this.viewStep);
            }
        }
        GlStateManager.disableBlend();
    }
    
    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == this.buttonExport.id) {
            this.paintingExport();
        }
    }
    
    protected void paintingExport() {
        File dir = new File(this.mc.mcDataDir, "paintings");
        dir.mkdir();
        File file = getTimestampedPNGFileForDirectory(dir);
        ITextComponent message;
        try {
            FileOutputStream output = new FileOutputStream(file);
            this.view.savePainting(output);
            TextComponentString link = new TextComponentString(file.getName());
            Style style = link.getStyle();
            style.setClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_FILE,
                    file.getAbsolutePath()));
            style.setUnderlined(true);
            message = new TextComponentTranslation("painting.export.success", link);
        } catch (IOException e) {
            message = new TextComponentTranslation("painting.export.failure", e.getMessage());
        }
        this.mc.ingameGUI.getChatGUI().printChatMessage(message);
    }
    
    protected void paintingCopy() {
        ITextComponent message;
        try {
            BufferedImage img = this.view.getPaintingAsImage();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            ImageSelection selection = new ImageSelection(img);
            clipboard.setContents(selection, null);
            message = new TextComponentTranslation("painting.export.copy.success");
        } catch (Exception e) {
            message = new TextComponentTranslation("painting.export.copy.failure", e.getMessage());
        }
        this.mc.ingameGUI.getChatGUI().printChatMessage(message);
    }
    
    @Override
    protected void keyTyped(char character, int key) {
        if (character == 3 /* Ctrl+c */) {
            this.paintingCopy();
            return;
        }
        if (key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.player.closeScreen();
        }
    }
    
    @Override
    public void onGuiClosed() {
        if (this.mc.player != null) {
            this.view.onContainerClosed(this.mc.player);
        }
    }
    
    // from ScreenShotHelper
    protected static File getTimestampedPNGFileForDirectory(File dir) {
        String s = DATE_FORMAT.format(new Date()).toString();
        for (int i = 1;; ++i) {
            File file = new File(dir, s + (i == 1 ? "" : "_" + i) + ".png");
            if (!file.exists()) {
                return file;
            }
        }
    }
}
