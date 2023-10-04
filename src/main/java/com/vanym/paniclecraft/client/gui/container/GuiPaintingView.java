package com.vanym.paniclecraft.client.gui.container;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.gui.GuiUtils;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.client.utils.ImageSelection;
import com.vanym.paniclecraft.container.ContainerPaintingViewBase;
import com.vanym.paniclecraft.container.ContainerPaintingViewClient;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.utils.JUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPaintingView extends Screen implements IHasContainer<ContainerPaintingViewBase> {
    
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
    
    protected final Button buttonExport = JUtils.make(()-> {
        String text = I18n.format(String.format("gui.%s.paintingview.export", DEF.MOD_ID));
        return new Button(0, 0, 60, 20, text, b->this.paintingExport());
    });
    
    public GuiPaintingView(ContainerPaintingViewClient view, ITextComponent title) {
        super(title);
        this.view = view;
    }
    
    @Override
    public ContainerPaintingViewBase getContainer() {
        return this.view;
    }
    
    @Override
    public void init() {
        super.init();
        this.addButton(this.buttonExport);
    }
    
    @Override
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        int viewMaxWidth = this.width - (PADDING_LEFT + PADDING_RIGHT);
        int viewMaxHeight = this.height - (PADDING_TOP + PADDING_BOTTOM);
        this.viewStep = Math.min(viewMaxWidth / this.view.sizeX, viewMaxHeight / this.view.sizeY);
        this.viewX = PADDING_LEFT + (viewMaxWidth - (this.viewStep * this.view.sizeX)) / 2;
        this.viewY = PADDING_TOP + (viewMaxHeight - (this.viewStep * this.view.sizeY)) / 2;
        int center = (width / 2);
        this.controlsX = Math.min(this.viewX, center - 100);
        this.controlsEndX = Math.max(this.getViewEndX(), center + 100);
        this.buttonExport.x = this.controlsEndX - this.buttonExport.getWidth();
        this.buttonExport.y = height - this.buttonExport.getHeight() - 5;
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
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        this.renderBackground();
        this.drawPainting();
        {
            StringBuilder sb = new StringBuilder();
            sb.append(this.view.getWidth());
            sb.append("×");
            sb.append(this.view.getHeight());
            this.font.drawString(sb.toString(), 2, 2, 0x7f7f7f);
        }
        super.render(mouseX, mouseY, renderPartialTicks);
        this.drawHelp();
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
                TextureAtlasSprite icon =
                        IconUtils.shrink(TileEntityPaintingRenderer.bindTexture(picture));
                blit(this.viewX + x * this.viewStep,
                     this.viewY + y * this.viewStep,
                     this.blitOffset,
                     this.viewStep, this.viewStep,
                     icon);
            }
        }
        GlStateManager.disableBlend();
    }
    
    protected void drawHelp() {
        if (!GuiUtils.isKeyDown(GLFW.GLFW_KEY_H)) {
            return;
        }
        String translationKey = String.format("gui.%s.paintingview.help.export", DEF.MOD_ID);
        this.drawHelp(Arrays.asList(I18n.format(translationKey).split(System.lineSeparator())));
    }
    
    protected void drawHelp(List<String> lines) {
        int lineHeight = 14;
        int y = this.height / 2 - lines.size() * (lineHeight / 2);
        for (String line : lines) {
            int x = (this.width - this.font.getStringWidth(line)) / 2;
            GuiUtils.drawString8xOutline(this.font, line, x, y + (lineHeight - 10) / 2, 0xe0e0e0);
            y += lineHeight;
        }
    }
    
    protected void paintingExport() {
        File dir = new File(this.minecraft.gameDir, "paintings");
        dir.mkdir();
        File file = getTimestampedPNGFileForDirectory(dir);
        ITextComponent message;
        try {
            FileOutputStream output = new FileOutputStream(file);
            this.view.savePainting(output);
            StringTextComponent link = new StringTextComponent(file.getName());
            Style style = link.getStyle();
            style.setClickEvent(new ClickEvent(
                    ClickEvent.Action.OPEN_FILE,
                    file.getAbsolutePath()));
            style.setUnderlined(true);
            message = new TranslationTextComponent(
                    String.format("chat.%s.painting.export.success", DEF.MOD_ID),
                    link);
        } catch (IOException e) {
            message = new TranslationTextComponent(
                    String.format("chat.%s.painting.export.failure", DEF.MOD_ID),
                    e.getMessage());
        }
        this.minecraft.ingameGUI.getChatGUI().printChatMessage(message);
    }
    
    protected void paintingCopy() {
        ITextComponent message;
        try {
            BufferedImage img = this.view.getPaintingAsImage();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            ImageSelection selection = new ImageSelection(img);
            clipboard.setContents(selection, null);
            message = new TranslationTextComponent(
                    String.format("chat.%s.painting.export.copy.success", DEF.MOD_ID));
        } catch (Exception e) {
            message = new TranslationTextComponent(
                    String.format("chat.%s.painting.export.copy.failure", DEF.MOD_ID),
                    e.getMessage());
        }
        this.minecraft.ingameGUI.getChatGUI().printChatMessage(message);
    }
    
    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (super.keyPressed(key, scanCode, modifiers)) {
            return true;
        }
        InputMappings.Input inputCode = InputMappings.getInputByCode(key, scanCode);
        if (this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(inputCode)) {
            this.onClose();
            return true;
        }
        if (Screen.isCopy(key)) {
            this.paintingCopy();
            return true;
        }
        return false;
    }
    
    @Override
    public void onClose() {
        this.minecraft.player.closeScreen();
    }
    
    @Override
    public void removed() {
        if (this.minecraft.player != null) {
            this.view.onContainerClosed(this.minecraft.player);
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
