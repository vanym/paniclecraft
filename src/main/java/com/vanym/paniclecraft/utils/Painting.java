package com.vanym.paniclecraft.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.ClientProxy;
import com.vanym.paniclecraft.item.ItemPaintBrush;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Painting {
    
    public static int defPaintRow = 16;
    
    public static boolean pngPaintingSave = true;
    
    public static boolean specialBoundingBox = true;
    
    public int brushRadiusSquare = ItemPaintBrush.brushRadiusSquare;
    public double brushRadiusRound = ItemPaintBrush.brushRadiusRound;
    
    public boolean canBeEdited = true;
    
    public int texID = -1;
    
    private int row = defPaintRow;
    private byte[] pic;
    
    public ISidePaintingProvider provider;
    
    public Painting(ISidePaintingProvider parpro) {
        this.provider = parpro;
    }
    
    public void usePaintBrush(ItemStack par1ItemStack, int x, int y, boolean updateNow) {
        if (par1ItemStack == null || !(par1ItemStack.getItem() instanceof ItemPaintBrush)) {
            return;
        }
        int var3 = Core.instance.painting.itemPaintBrush.getColorFromItemStack(par1ItemStack, 0);
        Color color = MainUtils.getColorFromInt(var3);
        this.usePaintBrush(par1ItemStack.getItemDamage(), color, x, y, updateNow);
    }
    
    public void usePaintBrush(int brushType, Color color, int x, int y, boolean updateNow) {
        if (!this.canBeEdited) {
            return;
        }
        ArrayList<Painting> updateList = new ArrayList<>();
        switch (brushType) {
            case 0:
                this.setPixelsColor(color, x, y, updateList);
            break;
            case 1:
                this.setPixelColor(color, x, y, updateList);
            break;
            case 2:
                this.fillOne(color);
                updateList.add(this);
            break;
        }
        Iterator<Painting> updateIterator = updateList.iterator();
        while (updateIterator.hasNext()) {
            Painting picture = updateIterator.next();
            if (updateNow) {
                picture.provider.markForUpdate();
            } else {
                picture.provider.needUpdate();
            }
        }
    }
    
    public void setPixelsColor(Color color, int x, int y, ArrayList<Painting> updateList) {
        for (int i = -this.brushRadiusSquare; i <= this.brushRadiusSquare; i++) {
            for (int j = -this.brushRadiusSquare; j <= this.brushRadiusSquare; j++) {
                if (i * i + j * j > this.brushRadiusRound * this.brushRadiusRound) {
                    continue;
                }
                this.setPixelColor_a(color, x + i, y + j, updateList);
            }
        }
    }
    
    public void setPixelColor_a(Color color, int x, int y, ArrayList<Painting> updateList) {
        int vx = x;
        int vy = y;
        int xO = 0;
        int yO = 0;
        while (vx < 0) {
            vx = this.getRow() + vx;
            xO--;
        }
        while (vx > this.getRow() - 1) {
            vx = vx - this.getRow();
            xO++;
        }
        while (vy < 0) {
            vy = this.getRow() + vy;
            yO--;
        }
        while (vy > this.getRow() - 1) {
            vy = vy - this.getRow();
            yO++;
        }
        Painting picture = this.provider.getPainting(this.provider.getPictureSide(this), xO, yO);
        if (picture != null && picture.getRow() == this.getRow()) {
            picture.setPixelColor(color, vx, vy, updateList);
        }
        
    }
    
    public void setPixelColor(Color color, int x, int y, ArrayList<Painting> updateList) {
        if (!color.equals(this.getPixelColor(x, y))) {
            this.setPixelColor(x, y, color);
            if (!updateList.contains(this)) {
                updateList.add(this);
            }
        }
    }
    
    public Color getPixelColor(int px, int py) {
        if (px >= 0 && px < this.getRow() && py >= 0 && py < this.getRow()) {
            return new Color(
                    (int)this.getPic()[(this.getRow() * py + px) * 3 + 0]
                             & 0xFF,
                    (int)this.getPic()[(this.getRow() * py + px) * 3 + 1] & 0xFF,
                    (int)this.getPic()[(this.getRow() * py + px) * 3 + 2] & 0xFF);
        }
        return null;
    }
    
    public boolean setPixelColor(int px, int py, Color color) {
        if (px >= 0 && px < this.getRow() && py >= 0 && py < this.getRow()) {
            this.getPic()[(this.getRow() * py + px) * 3 + 0] = (byte)color.getRed();
            this.getPic()[(this.getRow() * py + px) * 3 + 1] = (byte)color.getGreen();
            this.getPic()[(this.getRow() * py + px) * 3 + 2] = (byte)color.getBlue();
            return true;
        }
        return false;
    }
    
    public void fillOne(Color color) {
        byte[] col =
                new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
        for (int i = 0; i < this.getPic().length; i++) {
            this.getPic()[i] = col[i % 3];
        }
    }
    
    public int getRow() {
        return this.row;
    }
    
    public void setRow(int r) {
        if (r != this.row) {
            this.pic = null;
        }
        this.row = r;
    }
    
    public boolean hasPic() {
        return this.pic != null;
    }
    
    public void delPic() {
        this.pic = null;
    }
    
    public byte[] getPic() {
        if (this.pic != null && this.pic.length != this.row * this.row * 3) {
            this.pic = null;
        }
        if (this.pic == null) {
            this.pic = new byte[this.row * this.row * 3];
            for (int i = 0; i < this.pic.length; i++) {
                this.pic[i] = (byte)ItemPaintBrush.DEFAULT_COLOR_RGB;
            }
        }
        return this.pic;
    }
    
    public void rotatePicLeft() {
        byte[] newpic = new byte[this.pic.length];
        for (int x = 0; x < this.row; x++) {
            for (int y = 0; y < this.row; y++) {
                for (int i = 0; i < 3; i++) {
                    newpic[(((this.row - 1 - y) * this.row + x) * 3) + i] =
                            this.getPic()[((x * this.row + y) * 3) + i];
                }
            }
        }
        this.pic = newpic;
    }
    
    public void rotatePicRight() {
        byte[] newpic = new byte[this.pic.length];
        for (int x = 0; x < this.row; x++) {
            for (int y = 0; y < this.row; y++) {
                for (int i = 0; i < 3; i++) {
                    newpic[((y * this.row + (this.row - 1 - x)) * 3) + i] =
                            this.getPic()[((x * this.row + y) * 3) + i];
                }
            }
        }
        this.pic = newpic;
    }
    
    public void rotatePic180() {
        byte[] newpic = new byte[this.pic.length];
        for (int x = 0; x < this.row; x++) {
            for (int y = 0; y < this.row; y++) {
                for (int i = 0; i < 3; i++) {
                    newpic[(((this.row - 1 - x) * this.row + (this.row - 1 - y)) * 3) + i] =
                            this.getPic()[((x * this.row + y) * 3) + i];
                }
            }
        }
        this.pic = newpic;
    }
    
    private static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        java.awt.image.DataBufferByte buffer =
                new java.awt.image.DataBufferByte(bytes, bytes.length);
        java.awt.image.ColorModel cm = new java.awt.image.ComponentColorModel(
                java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB),
                new int[]{8, 8, 8},
                false,
                false,
                java.awt.Transparency.OPAQUE,
                java.awt.image.DataBuffer.TYPE_BYTE);
        return new BufferedImage(
                cm,
                java.awt.image.Raster.createInterleavedRaster(buffer, width, height, width * 3, 3,
                                                              new int[]{0, 1, 2}, null),
                false,
                null);
    }
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("BrushRadiusSquare", this.brushRadiusSquare);
        par1NBTTagCompound.setDouble("BrushRadiusRound", this.brushRadiusRound);
        par1NBTTagCompound.setBoolean("CanBeEdited", this.canBeEdited);
        if (pngPaintingSave) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(createRGBImage(this.getPic(), this.getRow(), this.getRow()), "png",
                              out);
                byte[] png = out.toByteArray();
                if (png.length < this.pic.length) {
                    par1NBTTagCompound.setByteArray("PicPng", out.toByteArray());
                    return;
                }
            } catch (IOException e) {
            }
        }
        par1NBTTagCompound.setInteger("Row", this.row);
        par1NBTTagCompound.setByteArray("pic[" + this.row + "]", this.getPic());
    }
    
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        if (par1NBTTagCompound.hasKey("BrushRadiusSquare")) {
            this.brushRadiusSquare = par1NBTTagCompound.getInteger("BrushRadiusSquare");
        }
        if (par1NBTTagCompound.hasKey("BrushRadiusRound")) {
            this.brushRadiusRound = par1NBTTagCompound.getDouble("BrushRadiusRound");
        }
        if (par1NBTTagCompound.hasKey("CanBeEdited")) {
            this.canBeEdited = par1NBTTagCompound.getBoolean("CanBeEdited");
        }
        if (par1NBTTagCompound.hasKey("PicPng")) {
            byte[] png = par1NBTTagCompound.getByteArray("PicPng");
            ByteArrayInputStream in = new ByteArrayInputStream(png);
            try {
                BufferedImage img = ImageIO.read(in);
                this.row = img.getHeight();
                img.getRaster().getDataElements(0, 0, this.row, this.row, this.getPic());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (par1NBTTagCompound.hasKey("Row")) {
            this.setRow(par1NBTTagCompound.getInteger("Row"));
            if (par1NBTTagCompound.hasKey("pic[" + this.row + "]")) {
                this.pic = par1NBTTagCompound.getByteArray("pic[" + this.row + "]");
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void deleteTexrure() {
        if (this.texID >= 0) {
            ClientProxy.deleteTexture(this.texID);
            this.texID = -1;
        }
    }
}
