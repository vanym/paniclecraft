package com.vanym.paniclecraft.core.component.painting;

import java.awt.Color;
import java.util.Arrays;

public class Image {
    
    protected final int width;
    protected final int height;
    protected final byte[] data;
    
    public Image(int width, int height) {
        this(width, height, null);
    }
    
    public Image(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        if (data == null) {
            this.data = new byte[this.getSize()];
        } else if (data.length != this.getSize()) {
            this.data = Arrays.copyOf(data, this.getSize());
        } else {
            this.data = data;
        }
    }
    
    protected int getSize() {
        return this.width * this.height * 3;
    }
    
    public boolean isSameSize(Image image) {
        return (this.width == image.width) && (this.height == image.height);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public byte[] getData() {
        int size = this.width * this.height * 3;
        if (this.data != null && this.data.length != size) {
            return null;
        }
        return this.data;
    }
    
    public boolean setPixelColor(int px, int py, Color color) {
        int offset = (py * this.width + px) * 3;
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
        return changed;
    }
    
    public Color getPixelColor(int px, int py) {
        int offset = (py * this.width + px) * 3;
        int offsetR = offset;
        int offsetG = offset + 1;
        int offsetB = offset + 2;
        int r = (int)this.data[offsetR] & 0xFF;
        int g = (int)this.data[offsetG] & 0xFF;
        int b = (int)this.data[offsetB] & 0xFF;
        return new Color(r, g, b);
    }
    
    public boolean fill(Color color) {
        byte r = (byte)color.getRed();
        byte g = (byte)color.getGreen();
        byte b = (byte)color.getBlue();
        boolean changed = false;
        for (int i = 0; i < this.data.length; i += 3) {
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
        }
        return changed;
    }
    
    public void rotate90() {
        // TODO
    }
    
    public void rotate180() {
        // TODO
    }
    
    public void rotate270() {
        // TODO
    }
}
