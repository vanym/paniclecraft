package com.vanym.paniclecraft.client.gui.container;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Image;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.network.message.MessagePaintingViewAddPicture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class GuiPaintingEditView extends GuiPaintingView {
    
    protected final GuiButton buttonImport =
            new GuiButton(2, 0, 0, 60, 20, I18n.format("gui.paintingview.import"));
    protected final GuiButton buttonImportSave =
            new GuiButton(3, 0, 0, 60, 20, I18n.format("gui.paintingview.importsave"));
    protected final GuiButton buttonImportCancel =
            new GuiButton(4, 0, 0, 60, 20, I18n.format("gui.paintingview.importcancel"));
    protected GuiTextField textImport;
    
    protected BufferedImage importImage;
    protected DynamicTexture importTexture;
    protected int importTextureX;
    protected int importTextureY;
    protected int importTextureWidth;
    protected int importTextureHeight;
    
    public GuiPaintingEditView(ContainerPaintingViewClient view) {
        super(view);
        this.buttonImportSave.visible = false;
        this.buttonImportCancel.visible = false;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.buttonImport);
        this.buttonList.add(this.buttonImportSave);
        this.buttonList.add(this.buttonImportCancel);
        Keyboard.enableRepeatEvents(true);
        this.updateButtons();
    }
    
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        if (this.textImport == null) {
            this.textImport = new GuiTextField(mc.fontRenderer, 0, 0, 60, 20);
            this.textImport.setMaxStringLength(65536);
        }
        super.setWorldAndResolution(mc, width, height);
        this.buttonImport.xPosition = this.buttonExport.xPosition - 5 - this.buttonImport.width;
        this.buttonImport.yPosition = this.buttonExport.yPosition;
        this.textImport.xPosition = Math.min(this.controlsX, this.viewX);
        this.textImport.yPosition = this.buttonImport.yPosition;
        this.textImport.width = this.buttonImport.xPosition - 5 - this.textImport.xPosition;
        this.buttonImportSave.xPosition = this.buttonImport.xPosition;
        this.buttonImportSave.yPosition = this.buttonImport.yPosition;
        this.buttonImportCancel.xPosition =
                this.buttonImportSave.xPosition - 5 - this.buttonImportCancel.width;
        this.buttonImportCancel.yPosition = this.buttonImportSave.yPosition;
    }
    
    protected void updateButtons() {
        boolean importing = (this.importImage != null);
        this.buttonImport.visible = !importing;
        this.textImport.setVisible(!importing);
        this.buttonImportSave.visible = importing;
        this.buttonImportCancel.visible = importing;
        this.buttonImportSave.enabled = false; // to prevent double click
    }
    
    protected int getImportTextureEndX() {
        return this.importTextureX + this.importTextureWidth;
    }
    
    protected int getImportTextureEndY() {
        return this.importTextureY + this.importTextureHeight;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
        this.textImport.drawTextBox();
    }
    
    @Override
    protected void drawPainting() {
        super.drawPainting();
        this.drawImportImage();
    }
    
    protected void drawImportImage() {
        if (this.importTexture == null) {
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.importTexture.getGlTextureId());
        int pictureWidth = this.view.pictureSize.getWidth();
        int pictureHeight = this.view.pictureSize.getHeight();
        int importTextureEndX = this.getImportTextureEndX();
        int importTextureEndY = this.getImportTextureEndY();
        int w = Math.min(this.view.sizeX, importTextureEndX / pictureWidth +
                                          ((importTextureEndX % pictureWidth) == 0 ? 0 : 1));
        int h = Math.min(this.view.sizeY, importTextureEndY / pictureHeight +
                                          ((importTextureEndY % pictureHeight) == 0 ? 0 : 1));
        for (int y = Math.max(0, this.importTextureY / pictureHeight); y < h; ++y) {
            int paintingY = y * pictureHeight;
            int paintingEndY = paintingY + pictureHeight;
            double canvasY = this.viewY + y * this.viewStep;
            double canvasEndY = canvasY + this.viewStep;
            int iconHeight = pictureHeight;
            if (this.importTextureY > paintingY) {
                int cut = this.importTextureY - paintingY;
                double offset = (double)(cut) / pictureHeight;
                canvasY += offset * this.viewStep;
                iconHeight -= cut;
            }
            if (importTextureEndY < paintingEndY) {
                int cut = paintingEndY - importTextureEndY;
                double offset = (double)(cut) / pictureHeight;
                canvasEndY -= offset * this.viewStep;
                iconHeight -= cut;
            }
            for (int x = Math.max(0, this.importTextureX / pictureWidth); x < w; ++x) {
                Picture picture = this.view.getPicture(x, y);
                if (picture != null && picture.isEditable()) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    final float c = 32.0F / 255.0F;
                    GL11.glColor4f(c, c, c, 0.75F);
                }
                int paintingX = x * pictureWidth;
                int paintingEndX = paintingX + pictureWidth;
                double canvasX = this.viewX + x * this.viewStep;
                double canvasEndX = canvasX + this.viewStep;
                int iconWidth = pictureWidth;
                if (this.importTextureX > paintingX) {
                    int cut = this.importTextureX - paintingX;
                    double offset = (double)(cut) / pictureWidth;
                    canvasX += offset * this.viewStep;
                    iconWidth -= cut;
                }
                if (importTextureEndX < paintingEndX) {
                    int cut = paintingEndX - importTextureEndX;
                    double offset = (double)(cut) / pictureWidth;
                    canvasEndX -= offset * this.viewStep;
                    iconWidth -= cut;
                }
                IIcon icon = IconUtils.sub(Math.max(paintingX - this.importTextureX, 0),
                                           Math.max(paintingY - this.importTextureY, 0),
                                           iconWidth, iconHeight,
                                           this.importTextureWidth,
                                           this.importTextureHeight);
                // based on drawTexturedModelRectFromIcon
                tessellator.startDrawingQuads();
                tessellator.addVertexWithUV(canvasX, canvasEndY, (double)this.zLevel,
                                            icon.getMinU(), icon.getMaxV());
                tessellator.addVertexWithUV(canvasEndX, canvasEndY, (double)this.zLevel,
                                            icon.getMaxU(), icon.getMaxV());
                tessellator.addVertexWithUV(canvasEndX, canvasY, (double)this.zLevel,
                                            icon.getMaxU(), icon.getMinV());
                tessellator.addVertexWithUV(canvasX, canvasY, (double)this.zLevel,
                                            icon.getMinU(), icon.getMinV());
                tessellator.draw();
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textImport.updateCursorCounter();
        this.buttonImportSave.enabled = true;
    }
    
    @Override
    protected void mouseClicked(int x, int y, int eventButton) {
        super.mouseClicked(x, y, eventButton);
        this.textImport.mouseClicked(x, y, eventButton);
    }
    
    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == this.buttonImport.id) {
            this.paintingImport();
        } else if (button.id == this.buttonImportSave.id) {
            this.paintingImportSave();
        } else if (button.id == this.buttonImportCancel.id) {
            this.paintingImportCancel();
        } else {
            super.actionPerformed(button);
        }
    }
    
    protected void paintingImport() {
        String text = this.textImport.getText();
        BufferedImage img;
        try {
            try {
                img = download(text);
            } catch (MalformedURLException e) {
                img = readfile(text);
            }
        } catch (Exception e) {
            ChatComponentTranslation message =
                    new ChatComponentTranslation("painting.import.failure", e.getMessage());
            this.mc.ingameGUI.getChatGUI().printChatMessage(message);
            return;
        }
        this.switchImportImage(img);
        this.updateButtons();
    }
    
    protected void paintingImportSave() {
        if (this.importImage == null) {
            return;
        }
        Picture picture = new Picture(convertToImage(this.importImage));
        this.view.addPicture(this.importTextureX, this.importTextureY, picture);
        ItemStack stack = ItemPainting.getPictureAsItem(picture);
        Core.instance.network.sendToServer(new MessagePaintingViewAddPicture(
                this.importTextureX,
                this.importTextureY,
                stack));
        picture.unload();
        this.clearImportImage();
        this.updateButtons();
    }
    
    protected void paintingImportCancel() {
        this.clearImportImage();
        this.updateButtons();
    }
    
    protected void switchImportImage(BufferedImage img) {
        if (this.importTexture != null) {
            this.importTexture.deleteGlTexture();
            this.importTexture = null;
        }
        this.importImage = img;
        if (img == null) {
            return;
        }
        this.importTexture = new DynamicTexture(img);
        this.importTextureWidth = img.getWidth();
        this.importTextureHeight = img.getHeight();
        this.moveCenter();
    }
    
    protected void clearImportImage() {
        this.switchImportImage(null);
    }
    
    protected void moveCenter() {
        this.importTextureX = (this.view.getWidth() - this.importTextureWidth) / 2;
        this.importTextureY = (this.view.getHeight() - this.importTextureHeight) / 2;
    }
    
    protected void fillFull() {
        if (this.importImage == null) {
            return;
        }
        java.awt.Image scaled = this.importImage.getScaledInstance(this.view.getWidth(),
                                                                   this.view.getHeight(),
                                                                   java.awt.Image.SCALE_SMOOTH);
        BufferedImage img = new BufferedImage(
                this.view.getWidth(),
                this.view.getHeight(),
                this.importImage.getType());
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        this.switchImportImage(img);
    }
    
    @Override
    protected void keyTyped(char character, int key) {
        if (this.textImport.textboxKeyTyped(character, key)) {
            return;
        }
        switch (key) {
            case 205: { // right
                int moveX;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveX = this.view.getWidth() - this.importTextureWidth;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveX = this.importTextureX +
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX + 1;
                }
                this.importTextureX = Math.min(moveX, this.view.getWidth());
                return;
            }
            case 203: { // left
                int moveX;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveX = 0;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveX = this.importTextureX -
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX - 1;
                }
                this.importTextureX = Math.max(moveX, -this.importTextureWidth);
                return;
            }
            case 208: { // down
                int moveY;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveY = this.view.getHeight() - this.importTextureHeight;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveY = this.importTextureY +
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY + 1;
                }
                this.importTextureY = Math.min(moveY, this.view.getHeight());
                return;
            }
            case 200: { // up
                int moveY;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveY = 0;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveY = this.importTextureY -
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY - 1;
                }
                this.importTextureY = Math.max(moveY, -this.importTextureHeight);
                return;
            }
            case 57: // space
                if (GuiScreen.isCtrlKeyDown()) {
                    this.fillFull();
                } else {
                    this.moveCenter();
                }
                return;
        }
        super.keyTyped(character, key);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.clearImportImage();
        super.onGuiClosed();
    }
    
    protected static BufferedImage download(String urltext) throws IOException {
        URL url = new URL(urltext);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(60000);
        return ImageIO.read(connection.getInputStream());
    }
    
    protected static BufferedImage readfile(String filetext) throws IOException {
        FileInputStream input = new FileInputStream(filetext);
        return ImageIO.read(input);
    }
    
    protected static Image convertToImage(BufferedImage img) {
        Image image = new Image(img.getWidth(), img.getHeight(), true);
        for (int py = 0; py < image.getHeight(); ++py) {
            for (int px = 0; px < image.getWidth(); ++px) {
                Color color = new Color(img.getRGB(px, py), true);
                image.setPixelColor(px, py, color);
            }
        }
        return image;
    }
}
