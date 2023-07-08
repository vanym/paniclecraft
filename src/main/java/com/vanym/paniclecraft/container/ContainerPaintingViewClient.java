package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerPaintingViewClient extends ContainerPaintingViewBase {
    
    protected final Picture[][] pictures;
    protected final boolean hasAlpha;
    
    public ContainerPaintingViewClient(int id,
            IPictureSize pictureSize,
            int sizeX,
            int sizeY,
            boolean hasAlpha,
            boolean editable) {
        super(id, pictureSize, sizeX, sizeY, editable);
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
    
    public static ContainerPaintingViewClient create(
            int id,
            PlayerInventory playerInv,
            PacketBuffer buf) {
        int pictureWidth = buf.readInt();
        int pictureHeight = buf.readInt();
        int sizeX = buf.readInt();
        int sizeY = buf.readInt();
        boolean hasAlpha = buf.readBoolean();
        boolean editable = buf.readBoolean();
        return new ContainerPaintingViewClient(
                id,
                new FixedPictureSize(pictureWidth, pictureHeight),
                sizeX,
                sizeY,
                hasAlpha,
                editable);
    }
}
