package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.util.Arrays;

public class Image {
    
    protected int width;
    protected int height;
    protected final byte[] data;
    protected final boolean hasAlpha;
    
    public Image(int width, int height, boolean hasAlpha) {
        this(width, height, null, hasAlpha);
    }
    
    public Image(int width, int height, byte[] data, boolean hasAlpha) {
        this.width = width;
        this.height = height;
        this.hasAlpha = hasAlpha;
        if (data == null) {
            this.data = new byte[this.getSize()];
        } else if (data.length != this.getSize()) {
            this.data = Arrays.copyOf(data, this.getSize());
        } else {
            this.data = data;
        }
    }
    
    protected int getPixelSize() {
        return getPixelSize(this.hasAlpha);
    }
    
    protected int getSize() {
        return this.width * this.height * this.getPixelSize();
    }
    
    public boolean isSameSize(Image image) {
        return (this.width == image.width) && (this.height == image.height);
    }
    
    public boolean hasAlpha() {
        return this.hasAlpha;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public byte[] getData() {
        int size = this.getSize();
        if (this.data != null && this.data.length != size) {
            return null;
        }
        return this.data;
    }
    
    public boolean setPixelColor(int px, int py, Color color) {
        int offset = (py * this.width + px) * this.getPixelSize();
        int offsetR = offset;
        int offsetG = offset + 1;
        int offsetB = offset + 2;
        byte r = (byte)color.getRed();
        byte g = (byte)color.getGreen();
        byte b = (byte)color.getBlue();
        boolean changed = false;
        if (this.data[offsetR] != r) {
            this.data[offsetR] = r;
            changed = true;
        }
        if (this.data[offsetG] != g) {
            this.data[offsetG] = g;
            changed = true;
        }
        if (this.data[offsetB] != b) {
            this.data[offsetB] = b;
            changed = true;
        }
        if (this.hasAlpha) {
            int offsetA = offset + 3;
            byte a = (byte)color.getAlpha();
            if (this.data[offsetA] != a) {
                this.data[offsetA] = a;
                changed = true;
            }
        }
        return changed;
    }
    
    public Color getPixelColor(int px, int py) {
        int offset = (py * this.width + px) * this.getPixelSize();
        int offsetR = offset;
        int offsetG = offset + 1;
        int offsetB = offset + 2;
        int r = (int)this.data[offsetR] & 0xFF;
        int g = (int)this.data[offsetG] & 0xFF;
        int b = (int)this.data[offsetB] & 0xFF;
        int a;
        if (this.hasAlpha) {
            int offsetA = offset + 3;
            a = (int)this.data[offsetA] & 0xFF;
        } else {
            a = 255;
        }
        return new Color(r, g, b, a);
    }
    
    public boolean fill(Color color) {
        byte r = (byte)color.getRed();
        byte g = (byte)color.getGreen();
        byte b = (byte)color.getBlue();
        byte a = (byte)color.getAlpha();
        boolean changed = false;
        for (int i = 0; i < this.data.length; i += this.getPixelSize()) {
            int iR = i;
            int iG = i + 1;
            int iB = i + 2;
            if (this.data[iR] != r) {
                this.data[iR] = r;
                changed = true;
            }
            if (this.data[iG] != g) {
                this.data[iG] = g;
                changed = true;
            }
            if (this.data[iB] != b) {
                this.data[iB] = b;
                changed = true;
            }
            if (this.hasAlpha) {
                int iA = i + 3;
                if (this.data[iA] != a) {
                    this.data[iA] = a;
                    changed = true;
                }
            }
        }
        return changed;
    }
    
    public boolean isEmpty() {
        if (!this.hasAlpha) {
            return false;
        }
        for (int i = 0; i < this.data.length; i += this.getPixelSize()) {
            int iA = i + 3;
            if (this.data[iA] != 0) {
                return false;
            }
        }
        return true;
    }
    
    protected void transpose() {
        MatrixUtils.transpose(this.data, this.width, this.getPixelSize());
        int t = this.width;
        this.width = this.height;
        this.height = t;
    }
    
    protected void flipH() {
        MatrixUtils.flipH(this.data, this.width, this.getPixelSize());
    }
    
    protected void flipV() {
        MatrixUtils.flipV(this.data, this.width, this.getPixelSize());
    }
    
    public void rotate90() {
        // Clockwise
        this.transpose();
        this.flipH();
    }
    
    public void rotate180() {
        MatrixUtils.rotate180(this.data, this.getPixelSize());
    }
    
    public void rotate270() {
        // Clockwise
        this.transpose();
        this.flipV();
    }
    
    public static int getPixelSize(boolean hasAlpha) {
        return hasAlpha ? 4 : 3;
    }
}
