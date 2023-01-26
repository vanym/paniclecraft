package com.vanym.paniclecraft.core.component.painting;

import java.util.Objects;

public class FixedPictureSize implements IPictureSize, Comparable<IPictureSize> {
    
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FixedPictureSize) {
            return IPictureSize.equals(this, (IPictureSize)obj);
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }
    
    @Override
    public int compareTo(IPictureSize o) {
        int tw = this.getWidth();
        int ow = o.getWidth();
        int tsize = tw * this.getHeight();
        int osize = ow * o.getHeight();
        int v = Integer.compare(tsize, osize);
        if (v == 0) {
            v = Integer.compare(tw, ow);
        }
        return v;
    }
}
