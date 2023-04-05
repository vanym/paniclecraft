package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;

public class ContainerPaintingViewClient extends ContainerPaintingViewBase {
    
    protected final Picture[][] pictures;
    protected final boolean hasAlpha;
    
    public ContainerPaintingViewClient(IPictureSize pictureSize,
            int sizeX,
            int sizeY,
            boolean hasAlpha) {
        super(pictureSize, sizeX, sizeY);
        this.pictures = new Picture[sizeY][sizeX];
        this.hasAlpha = hasAlpha;
    }
    
    @Override
    public Picture getPicture(int x, int y) {
        return this.pictures[y][x];
    }
    
    @Override
    protected Picture getOrCreatePicture(int x, int y) {
        return this.pictures[y][x] = new Picture(this.pictureSize, this.hasAlpha);
    }
    
    @Override
    protected boolean clearPicture(int x, int y) {
        Picture picture = this.pictures[y][x];
        if (picture != null) {
            picture.unload();
        }
        this.pictures[y][x] = null;
        return true;
    }
}
