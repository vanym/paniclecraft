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

import javax.imageio.ImageIO;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.container.ContainerPaintingViewBase;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Image;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.network.message.MessagePaintingViewAddPicture;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

@OnlyIn(Dist.CLIENT)
public class GuiPaintingEditView extends GuiPaintingView {
    
    protected final Button buttonImport;
    protected final Button buttonImportSave;
    protected final Button buttonImportCancel;
    protected TextFieldWidget textImport;
    
    protected BufferedImage importImage;
    protected DynamicTexture importTexture;
    protected int importTextureX;
    protected int importTextureY;
    protected int importTextureWidth;
    protected int importTextureHeight;
    
    protected boolean mouseMove;
    protected int mouseMoveOffsetX;
    protected int mouseMoveOffsetY;
    
    public GuiPaintingEditView(ContainerPaintingViewClient view, ITextComponent title) {
        super(view, title);
        String textImport = I18n.format("gui.paintingview.import");
        this.buttonImport = new Button(0, 0, 60, 20, textImport, b->this.paintingImport());
        String textImportSave = I18n.format("gui.paintingview.importsave");
        this.buttonImportSave =
                new Button(0, 0, 60, 20, textImportSave, b->this.paintingImportSave());
        String textImportCancel = I18n.format("gui.paintingview.importcancel");
        this.buttonImportCancel =
                new Button(0, 0, 60, 20, textImportCancel, b->this.paintingImportCancel());
        this.buttonImportSave.visible = false;
        this.buttonImportCancel.visible = false;
    }
    
    @Override
    public void init() {
        super.init();
        this.addButton(this.buttonImport);
        this.addButton(this.buttonImportSave);
        this.addButton(this.buttonImportCancel);
        this.addButton(this.textImport);
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.updateButtons();
    }
    
    @Override
    public void init(Minecraft mc, int width, int height) {
        if (this.textImport == null) {
            this.textImport = new TextFieldWidget(mc.fontRenderer, 0, 0, 60, 20, "image location");
            this.textImport.setMaxStringLength(65536);
        }
        super.init(mc, width, height);
        this.buttonImport.x = this.buttonExport.x - 5 - this.buttonImport.getWidth();
        this.buttonImport.y = this.buttonExport.y;
        this.textImport.x = Math.min(this.controlsX, this.viewX);
        this.textImport.y = this.buttonImport.y;
        this.textImport.setWidth(this.buttonImport.x - 5 - this.textImport.x);
        this.buttonImportSave.x = this.buttonImport.x;
        this.buttonImportSave.y = this.buttonImport.y;
        this.buttonImportCancel.x =
                this.buttonImportSave.x - 5 - this.buttonImportCancel.getWidth();
        this.buttonImportCancel.y = this.buttonImportSave.y;
    }
    
    protected void updateButtons() {
        boolean importing = (this.importImage != null);
        this.buttonImport.visible = !importing;
        this.textImport.setVisible(!importing);
        this.buttonImportSave.visible = importing;
        this.buttonImportCancel.visible = importing;
        this.buttonImportSave.active = false; // to prevent double click
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
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        super.render(mouseX, mouseY, renderPartialTicks);
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
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                                 GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.bindTexture(this.importTexture.getGlTextureId());
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
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    final float c = 32.0F / 255.0F;
                    GlStateManager.color4f(c, c, c, 0.75F);
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
                TextureAtlasSprite icon =
                        IconUtils.sub(Math.max(paintingX - this.importTextureX, 0),
                                      Math.max(paintingY - this.importTextureY, 0),
                                      iconWidth, iconHeight,
                                      this.importTextureWidth,
                                      this.importTextureHeight);
                // based on drawTexturedModelRect
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buf = tessellator.getBuffer();
                buf.begin(7, DefaultVertexFormats.POSITION_TEX);
                buf.pos(canvasX, canvasEndY, (double)this.blitOffset)
                   .tex(icon.getMinU(), icon.getMaxV())
                   .endVertex();
                buf.pos(canvasEndX, canvasEndY, (double)this.blitOffset)
                   .tex(icon.getMaxU(), icon.getMaxV())
                   .endVertex();
                buf.pos(canvasEndX, canvasY, (double)this.blitOffset)
                   .tex(icon.getMaxU(), icon.getMinV())
                   .endVertex();
                buf.pos(canvasX, canvasY, (double)this.blitOffset)
                   .tex(icon.getMinU(), icon.getMinV())
                   .endVertex();
                tessellator.draw();
            }
        }
        GlStateManager.disableBlend();
    }
    
    @Override
    public void tick() {
        super.tick();
        this.textImport.tick();
        this.buttonImportSave.active = true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int eventButton) {
        return this.mouseClickedMovingImage(x, y, eventButton)
            || super.mouseClicked(x, y, eventButton);
    }
    
    protected boolean mouseClickedMovingImage(double x, double y, int eventButton) {
        if (eventButton != 0 || this.importImage == null) {
            return false;
        }
        int vmx = this.getViewMouseX();
        int vmy = this.getViewMouseY();
        if (vmx < this.importTextureX
            || vmy < this.importTextureY
            || vmx >= this.getImportTextureEndX()
            || vmy >= this.getImportTextureEndY()) {
            return false;
        }
        this.mouseMoveOffsetX = this.importTextureX - vmx;
        this.mouseMoveOffsetY = this.importTextureY - vmy;
        this.mouseMove = true;
        return true;
    }
    
    @Override
    public boolean mouseReleased(double x, double y, int eventButton) {
        boolean changed = false;
        if (this.mouseMove && eventButton == 0) {
            this.mouseMove = false;
            changed = true;
        }
        return super.mouseReleased(x, y, eventButton) || changed;
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (this.mouseMove) {
            this.importTextureX = this.mouseMoveOffsetX + this.getViewMouseX();
            this.importTextureY = this.mouseMoveOffsetY + this.getViewMouseY();
        }
        return super.mouseDragged(x, y, button, dragX, dragY) || this.mouseMove;
    }
    
    protected int getViewMouseX() {
        int real = (int)this.minecraft.mouseHelper.getMouseX();
        int displayWidth = this.minecraft.mainWindow.getWidth();
        int realViewX = this.viewX * displayWidth / this.width;
        int realViewWidth = this.getViewWidth() * displayWidth / this.width;
        return (real - realViewX) * this.view.getWidth() / realViewWidth;
    }
    
    protected int getViewMouseY() {
        int real = (int)this.minecraft.mouseHelper.getMouseY();
        int displayHeight = this.minecraft.mainWindow.getHeight();
        int realViewY = this.viewY * displayHeight / this.height;
        int realViewHeight = this.getViewHeight() * displayHeight / this.height;
        return (real - realViewY) * this.view.getHeight() / realViewHeight;
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
            TranslationTextComponent message =
                    new TranslationTextComponent("painting.import.failure", e.getMessage());
            this.minecraft.ingameGUI.getChatGUI().printChatMessage(message);
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
            MessagePaintingViewAddPicture message = new MessagePaintingViewAddPicture(
                    this.importTextureX,
                    this.importTextureY,
                    stack);
            IPacket<?> packet =
                    Core.instance.network.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
            picture.unload();
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            packet.writePacketData(buf);
            if (buf.capacity() >= 32767) {
                // See CCustomPayloadPacket
                throw new IllegalArgumentException();
            }
            this.minecraft.getConnection().sendPacket(packet);
        } catch (IllegalArgumentException | IOException e) {
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
            this.importTexture.close();
            this.importTexture = null;
        }
        this.importImage = img;
        if (img != null) {
            this.importTexture = new DynamicTexture(convertToNativeImage(img));
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
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (Screen.isPaste(key)) {
            if (this.loadClipboardImage()) {
                return true;
            }
            this.textImport.setFocused2(true);
            this.setFocused(this.textImport);
        }
        if (super.keyPressed(key, scanCode, modifiers)) {
            return true;
        }
        switch (key) {
            case 257: // enter
            case 335: // enter numpad
                if (this.textImport.isFocused() && !this.textImport.getText().isEmpty()) {
                    this.paintingImport();
                    return true;
                }
            case 262: { // right
                int moveX;
                if (Screen.hasControlDown()) {
                    moveX = this.view.getWidth() - this.importTextureWidth;
                } else if (Screen.hasShiftDown()) {
                    moveX = this.importTextureX +
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX + 1;
                }
                this.setImportTextureX(moveX);
                return true;
            }
            case 263: { // left
                int moveX;
                if (Screen.hasControlDown()) {
                    moveX = 0;
                } else if (Screen.hasShiftDown()) {
                    moveX = this.importTextureX -
                            Math.min(this.importTextureWidth, this.view.pictureSize.getWidth());
                } else {
                    moveX = this.importTextureX - 1;
                }
                this.setImportTextureX(moveX);
                return true;
            }
            case 264: { // down
                int moveY;
                if (Screen.hasControlDown()) {
                    moveY = this.view.getHeight() - this.importTextureHeight;
                } else if (Screen.hasShiftDown()) {
                    moveY = this.importTextureY +
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY + 1;
                }
                this.setImportTextureY(moveY);
                return true;
            }
            case 265: { // up
                int moveY;
                if (Screen.hasControlDown()) {
                    moveY = 0;
                } else if (Screen.hasShiftDown()) {
                    moveY = this.importTextureY -
                            Math.min(this.importTextureHeight, this.view.pictureSize.getHeight());
                } else {
                    moveY = this.importTextureY - 1;
                }
                this.setImportTextureY(moveY);
                return true;
            }
            case 32: // space
                if (Screen.hasControlDown()) {
                    this.fillFull();
                } else {
                    this.moveCenter();
                }
                return true;
        }
        return false;
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
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.clearImportImage();
        super.onClose();
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
    
    protected static NativeImage convertToNativeImage(
            BufferedImage img,
            int x,
            int y,
            int w,
            int h) {
        NativeImage image = new NativeImage(w, h, false);
        for (int py = 0; py < image.getHeight(); ++py) {
            for (int px = 0; px < image.getWidth(); ++px) {
                image.setPixelRGBA(px, py, img.getRGB(px + x, py + y));
            }
        }
        return image;
    }
    
    protected static NativeImage convertToNativeImage(BufferedImage img) {
        return convertToNativeImage(img, 0, 0, img.getWidth(), img.getHeight());
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
    
    public static GuiPaintingView create(
            ContainerPaintingViewBase view,
            PlayerInventory playerInv,
            ITextComponent title) {
        ContainerPaintingViewClient viewClient = (ContainerPaintingViewClient)view;
        return view.editable ? new GuiPaintingEditView(viewClient, title)
                             : new GuiPaintingView(viewClient, title);
    }
}
