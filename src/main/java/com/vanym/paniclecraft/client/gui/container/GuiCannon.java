package com.vanym.paniclecraft.client.gui.container;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.network.message.MessageCannonChange;
import com.vanym.paniclecraft.network.message.MessageCannonSet;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiCannon extends GuiContainer {
    
    public static final ResourceLocation GuiTexture =
            new ResourceLocation(DEF.MOD_ID, "textures/guis/cannonGui.png");
    
    public IInventory upperChestInventory;
    public TileEntityCannon cannon;
    
    private GuiButton buttonMinusDirection;
    private GuiButton buttonPlusDirection;
    
    private GuiButton buttonMinusHeight;
    private GuiButton buttonPlusHeight;
    
    private GuiButton buttonMinusStrength;
    private GuiButton buttonPlusStrength;
    
    private GuiTextField textDirection;
    private GuiTextField textHeight;
    private GuiTextField textStrength;
    
    public GuiCannon(IInventory par1IInventory, TileEntityCannon par2Cannon) {
        super(new ContainerCannon(par1IInventory, par2Cannon));
        this.upperChestInventory = par1IInventory;
        this.cannon = par2Cannon;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        int var1 = this.width / 2;
        int var2 = this.height / 2;
        int var3 = var1 - 60;
        int var4 = var2 - 66;
        this.buttonMinusDirection = new GuiButton(0, var3 + 10, var4, 10, 15, "-");
        this.buttonPlusDirection = new GuiButton(1, var3, var4, 10, 15, "+");
        this.buttonMinusHeight = new GuiButton(2, var3 + 10, var4 + 15, 10, 15, "-");
        this.buttonPlusHeight = new GuiButton(3, var3, var4 + 15, 10, 15, "+");
        this.buttonMinusStrength = new GuiButton(4, var3 + 10, var4 + 30, 10, 15, "-");
        this.buttonPlusStrength = new GuiButton(5, var3, var4 + 30, 10, 15, "+");
        this.textDirection = new GuiTextField(this.fontRendererObj, var3 + 105, var4, 35, 15);
        this.textHeight = new GuiTextField(this.fontRendererObj, var3 + 105, var4 + 15, 35, 15);
        this.textStrength = new GuiTextField(this.fontRendererObj, var3 + 105, var4 + 30, 35, 15);
        this.buttonList.clear();
        this.buttonList.add(this.buttonMinusDirection);
        this.buttonList.add(this.buttonPlusDirection);
        this.buttonList.add(this.buttonMinusHeight);
        this.buttonList.add(this.buttonPlusHeight);
        this.buttonList.add(this.buttonMinusStrength);
        this.buttonList.add(this.buttonPlusStrength);
        this.checkHeight();
    }
    
    @Override
    public void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            byte bt = (byte)par1GuiButton.id;
            if (GuiScreen.isCtrlKeyDown()) {
                bt += 12;
            } else if (GuiScreen.isShiftKeyDown()) {
                bt += 6;
            }
            Core.instance.network.sendToServer(new MessageCannonChange(bt));
            // this.checkHeight();
        }
    }
    
    public void checkHeight() {
        // if(true)
        // return;
        if (this.cannon.getHeight() >= 90) {
            this.buttonPlusHeight.enabled = false;
        } else {
            this.buttonPlusHeight.enabled = true;
        }
        if (this.cannon.getHeight() <= -90) {
            this.buttonMinusHeight.enabled = false;
        } else {
            this.buttonMinusHeight.enabled = true;
        }
        
        if (this.cannon.getStrength() >= this.cannon.maxStrength) {
            this.buttonPlusStrength.enabled = false;
        } else {
            this.buttonPlusStrength.enabled = true;
        }
        if (this.cannon.getStrength() <= 0) {
            this.buttonMinusStrength.enabled = false;
        } else {
            this.buttonMinusStrength.enabled = true;
        }
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.cannon.hasCustomInventoryName() ? this.cannon.getInventoryName()
                                                                             : StatCollector.translateToLocal(this.cannon.getInventoryName() +
                                                                                 ".name").trim(),
                                        8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName()
                                                                                          : I18n.format(this.upperChestInventory.getInventoryName(),
                                                                                                        new Object[0]),
                                        8, this.ySize - 96 + 2, 4210752);
        
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.direction")
                                                     .trim() +
            " " + this.cannon.getDirection(), 50, 22, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.height").trim() +
            " " + this.cannon.getHeight(), 50, 37, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.cannon.strength")
                                                     .trim() +
            " " + this.cannon.getStrength(), 50, 52, 4210752);
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
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
    public void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        this.textDirection.textboxKeyTyped(par1, par2);
        this.textHeight.textboxKeyTyped(par1, par2);
        this.textStrength.textboxKeyTyped(par1, par2);
        if (par2 == 28) {
            String text = null;
            byte bt = -1;
            if (this.textDirection.isFocused()) {
                text = this.textDirection.getText();
                bt = 0;
            }
            if (this.textHeight.isFocused()) {
                text = this.textHeight.getText();
                bt = 1;
            }
            if (this.textStrength.isFocused()) {
                text = this.textStrength.getText();
                bt = 2;
            }
            if (bt >= 0) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(text);
                } catch (NumberFormatException e) {
                } finally {
                    Core.instance.network.sendToServer(new MessageCannonSet(bt, amount));
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.textDirection.mouseClicked(par1, par2, par3);
        this.textHeight.mouseClicked(par1, par2, par3);
        this.textStrength.mouseClicked(par1, par2, par3);
    }
    
    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(GuiTexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 3 * 18 + 17 + 96);
    }
    
}
