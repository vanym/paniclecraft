package com.vanym.paniclecraft.core.component.painting;

public class FixedPictureSize implements IPictureSize {
    
    protected int width;
    protected int height;
    
    public FixedPictureSize(int row) {
        this(row, row);
    }
    
    public FixedPictureSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public FixedPictureSize(IPictureSize size) {
        this(size, 1);
    }
    
    public FixedPictureSize(IPictureSize size, int mul) {
        this(size.getWidth() * mul, size.getHeight() * mul);
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
}
