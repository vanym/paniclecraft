package com.vanym.paniclecraft.client.gui.container;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Image;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.network.message.MessagePaintingViewAddPicture;
import com.vanym.paniclecraft.utils.JUtils;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
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
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class GuiPaintingEditView extends GuiPaintingView {
    
    protected final GuiButton buttonImport = JUtils.make(()-> {
        String text = I18n.format(String.format("gui.%s.paintingview.import", DEF.MOD_ID));
        return new GuiButton(2, 0, 0, 60, 20, text);
    });
    
    protected final GuiButton buttonImportSave = JUtils.make(()-> {
        String text = I18n.format(String.format("gui.%s.paintingview.importsave", DEF.MOD_ID));
        return new GuiButton(3, 0, 0, 60, 20, text);
    });
    
    protected final GuiButton buttonImportCancel = JUtils.make(()-> {
        String text = I18n.format(String.format("gui.%s.paintingview.importcancel", DEF.MOD_ID));
        return new GuiButton(4, 0, 0, 60, 20, text);
    });
    
    protected GuiTextField textImport;
    
    protected BufferedImage importImage;
    protected DynamicTexture importTexture;
    protected int importTextureX;
    protected int importTextureY;
    protected int importTextureWidth;
    protected int importTextureHeight;
    
    protected boolean mouseMove;
    protected int mouseMoveOffsetX;
    protected int mouseMoveOffsetY;
    
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
        this.textImport.xPosition = Math.min(this.controlsX, this.viewX) + 1;
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
        if (importing) {
            this.textImport.setFocused(false);
        }
        this.buttonImportSave.visible = importing;
        this.buttonImportCancel.visible = importing;
        this.buttonImportSave.enabled = false; // to prevent double click
    }
    
    protected void setImportTextureX(int x) {
        this.importTextureX =
                Math.min(Math.max(x, -this.importTextureWidth), this.view.getWidth());
    }
    
    protected void setImportTextureY(int y) {
        this.importTextureY =
                Math.min(Math.max(y, -this.importTextureHeight), this.view.getHeight());
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
    protected void drawHelp() {
        boolean importing = (this.importImage != null);
        if (importing) {
            String line = I18n.format(String.format("gui.%s.paintingview.help.show", DEF.MOD_ID));
            int lineWidth = this.fontRendererObj.getStringWidth(line);
            int x, y;
            if (this.controlsX + 1 <= this.buttonImportCancel.xPosition - lineWidth - 2) {
                x = Math.max(this.controlsX + 1, this.buttonImportCancel.xPosition - lineWidth - 4);
                y = this.height - 19;
            } else {
                x = this.width - lineWidth - 2;
                y = 2;
            }
            this.fontRendererObj.drawString(line, x, y, 0x7f7f7f);
        }
        if (this.textImport.isFocused() || !Keyboard.isKeyDown(Keyboard.KEY_H)) {
            return;
        }
        String translationKey = String.format("gui.%s.paintingview.help.%s", DEF.MOD_ID,
                                              importing ? "importing" : "import");
        this.drawHelp(Arrays.asList(I18n.format(translationKey).split(System.lineSeparator())));
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textImport.updateCursorCounter();
        this.buttonImportSave.enabled = true;
    }
    
    @Override
    protected void mouseClicked(int x, int y, int eventButton) {
        this.mouseClickedMovingImage(x, y, eventButton);
        super.mouseClicked(x, y, eventButton);
        this.textImport.mouseClicked(x, y, eventButton);
    }
    
    protected void mouseClickedMovingImage(int x, int y, int eventButton) {
        if (eventButton != 0 || this.importImage == null) {
            return;
        }
        int vmx = this.getViewMouseX();
        int vmy = this.getViewMouseY();
        if (vmx < this.importTextureX
            || vmy < this.importTextureY
            || vmx >= this.getImportTextureEndX()
            || vmy >= this.getImportTextureEndY()) {
            return;
        }
        this.mouseMoveOffsetX = this.importTextureX - vmx;
        this.mouseMoveOffsetY = this.importTextureY - vmy;
        this.mouseMove = true;
    }
    
    @Override
    protected void mouseMovedOrUp(int x, int y, int eventButton) {
        super.mouseMovedOrUp(x, y, eventButton);
        if (eventButton == 0) {
            this.mouseMove = false;
        }
    }
    
    @Override
    protected void mouseClickMove(int x, int y, int button, long timeSinceMouseClick) {
        super.mouseClickMove(x, y, button, timeSinceMouseClick);
        if (this.mouseMove) {
            this.importTextureX = this.mouseMoveOffsetX + this.getViewMouseX();
            this.importTextureY = this.mouseMoveOffsetY + this.getViewMouseY();
        }
    }
    
    protected int getViewMouseX() {
        int real = Mouse.getEventX();
        int realViewX = this.viewX * this.mc.displayWidth / this.width;
        int realViewWidth = this.getViewWidth() * this.mc.displayWidth / this.width;
        return (real - realViewX) * this.view.getWidth() / realViewWidth;
    }
    
    protected int getViewMouseY() {
        int real = this.mc.displayHeight - Mouse.getEventY() - 1;
        int realViewY = this.viewY * this.mc.displayHeight / this.height;
        int realViewHeight = this.getViewHeight() * this.mc.displayHeight / this.height;
        return (real - realViewY) * this.view.getHeight() / realViewHeight;
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
            IChatComponent message = new ChatComponentTranslation(
                    String.format("chat.%s.painting.import.failure", DEF.MOD_ID),
                    e.getMessage());
            this.mc.ingameGUI.getChatGUI().printChatMessage(message);
            return;
        }
        this.textImport.setSelectionPos(0);
        this.switchImportImage(img);
    }
    
    protected void paintingImportSave() {
        if (this.importImage == null) {
            return;
        }
        try {
            Picture picture = new Picture(convertToImage(this.importImage));
            this.view.addPicture(this.importTextureX, this.importTextureY, picture);
            ItemStack stack = ItemPainting.getPictureAsItem(picture);
            FMLEmbeddedChannel channel = Core.instance.getChannel(Side.CLIENT);
            MessagePaintingViewAddPicture message = new MessagePaintingViewAddPicture(
                    this.importTextureX,
                    this.importTextureY,
                    stack);
            FMLProxyPacket packet = (FMLProxyPacket)channel.generatePacketFrom(message);
            picture.unload();
            if (packet.payload().capacity() >= 32767) {
                // See C17PacketCustomPayload
                throw new IllegalArgumentException();
            }
            this.mc.getNetHandler().addToSendQueue(packet);
        } catch (IllegalArgumentException e) {
            final int step = 80; // split to pass 32k payload limit
            for (int y = 0; y < this.importImage.getHeight(); y += step) {
                for (int x = 0; x < this.importImage.getWidth(); x += step) {
                    int w = Math.min(step, this.importImage.getWidth() - x);
                    int h = Math.min(step, this.importImage.getHeight() - y);
                    Picture picture = new Picture(convertToImage(this.importImage, x, y, w, h));
                    ItemStack stack = ItemPainting.getPictureAsItem(picture);
                    Core.instance.network.sendToServer(new MessagePaintingViewAddPicture(
                            this.importTextureX + x,
                            this.importTextureY + y,
                            stack));
                    picture.unload();
                }
            }
        }
        this.clearImportImage();
    }
    
    protected void paintingImportCancel() {
        this.clearImportImage();
    }
    
    protected void switchImportImage(BufferedImage img) {
        if (this.importTexture != null) {
            this.importTexture.deleteGlTexture();
            this.importTexture = null;
        }
        this.importImage = img;
        if (img != null) {
            this.importTexture = new DynamicTexture(img);
            this.importTextureWidth = img.getWidth();
            this.importTextureHeight = img.getHeight();
            this.moveCenter();
        }
        this.updateButtons();
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
        if (character == 22 /* Ctrl+v */) {
            if (this.loadClipboardImage()) {
                return;
            }
            this.textImport.setFocused(true);
        }
        if (this.textImport.textboxKeyTyped(character, key)) {
            return;
        }
        switch (key) {
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if (this.textImport.isFocused() && !this.textImport.getText().isEmpty()) {
                    this.paintingImport();
                }
                return;
            case Keyboard.KEY_D:
            case Keyboard.KEY_RIGHT: {
                int moveX;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveX = this.view.getWidth() - this.importTextureWidth;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveX = this.importTextureX +
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX + 1;
                }
                this.setImportTextureX(moveX);
                return;
            }
            case Keyboard.KEY_A:
            case Keyboard.KEY_LEFT: {
                int moveX;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveX = 0;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveX = this.importTextureX -
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX - 1;
                }
                this.setImportTextureX(moveX);
                return;
            }
            case Keyboard.KEY_S:
            case Keyboard.KEY_DOWN: {
                int moveY;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveY = this.view.getHeight() - this.importTextureHeight;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveY = this.importTextureY +
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY + 1;
                }
                this.setImportTextureY(moveY);
                return;
            }
            case Keyboard.KEY_W:
            case Keyboard.KEY_UP: {
                int moveY;
                if (GuiScreen.isCtrlKeyDown()) {
                    moveY = 0;
                } else if (GuiScreen.isShiftKeyDown()) {
                    moveY = this.importTextureY -
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY - 1;
                }
                this.setImportTextureY(moveY);
                return;
            }
            case Keyboard.KEY_SPACE:
                if (GuiScreen.isCtrlKeyDown()) {
                    this.fillFull();
                } else {
                    this.moveCenter();
                }
                return;
        }
        super.keyTyped(character, key);
    }
    
    protected boolean loadClipboardImage() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable content = clipboard.getContents(null);
            BufferedImage img = (BufferedImage)content.getTransferData(DataFlavor.imageFlavor);
            this.switchImportImage(img);
            return true;
        } catch (Exception e) {
            return false;
        }
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
    
    protected static Image convertToImage(BufferedImage img, int x, int y, int w, int h) {
        Image image = new Image(w, h, true);
        for (int py = 0; py < image.getHeight(); ++py) {
            for (int px = 0; px < image.getWidth(); ++px) {
                Color color = new Color(img.getRGB(px + x, py + y), true);
                image.setPixelColor(px, py, color);
            }
        }
        return image;
    }
    
    protected static Image convertToImage(BufferedImage img) {
        return convertToImage(img, 0, 0, img.getWidth(), img.getHeight());
    }
}
