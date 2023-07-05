package com.vanym.paniclecraft.container;

import java.util.function.Consumer;

import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class ContainerPaintingViewServer extends ContainerPaintingViewBase {
    
    public final WorldPicturePoint point;
    
    protected boolean editable = false;
    
    public ContainerPaintingViewServer(int id,
            WorldPicturePoint point,
            IPictureSize pictureSize,
            int sizeX,
            int sizeY,
            boolean editable) {
        super(id, pictureSize, sizeX, sizeY, editable);
        this.point = point;
    }
    
    @Override
    protected Picture getPicture(int x, int y) {
        return this.point.getNeighborPoint(x, y).getPicture();
    }
    
    @Override
    protected Picture getOrCreatePicture(int x, int y) {
        return this.point.getNeighborPoint(x, y).getOrCreatePicture();
    }
    
    @Override
    protected boolean clearPicture(int x, int y) {
        return false;
    }
    
    public static Provider makeFullView(WorldPicturePoint point, int maxRadius) {
        Picture center = point.getPicture();
        if (center == null) {
            return null;
        }
        FixedPictureSize size = new FixedPictureSize(center);
        int extendUp = 0, extendDown = 0, extendLeft = 0, extendRight = 0;
        for (int radius = 0;
             radius < maxRadius && radius == Math.max(Math.max(extendUp, extendDown),
                                                      Math.max(extendLeft, extendRight));
             ++radius) {
            int xmin = -extendLeft, xmax = extendRight, ymin = -extendUp, ymax = extendDown;
            extendUp:
            while (extendUp <= radius) {
                int py = -extendUp;
                int y = -(extendUp + 1);
                for (int x = xmin; x <= xmax; ++x) {
                    Picture picture = point.getNeighborPoint(x, py).getPicture();
                    Picture nextpicture = point.getNeighborPoint(x, y).getPicture();
                    if (IPictureSize.equals(size, picture)
                        && IPictureSize.equals(size, nextpicture)) {
                        ++extendUp;
                        continue extendUp;
                    }
                }
                break extendUp;
            }
            extendDown:
            while (extendDown <= radius) {
                int py = extendDown;
                int y = (extendDown + 1);
                for (int x = xmin; x <= xmax; ++x) {
                    Picture picture = point.getNeighborPoint(x, py).getPicture();
                    Picture nextpicture = point.getNeighborPoint(x, y).getPicture();
                    if (IPictureSize.equals(size, picture)
                        && IPictureSize.equals(size, nextpicture)) {
                        ++extendDown;
                        continue extendDown;
                    }
                }
                break extendDown;
            }
            extendLeft:
            while (extendLeft <= radius) {
                int px = -extendLeft;
                int x = -(extendLeft + 1);
                for (int y = ymin; y <= ymax; ++y) {
                    Picture picture = point.getNeighborPoint(px, y).getPicture();
                    Picture nextpicture = point.getNeighborPoint(x, y).getPicture();
                    if (IPictureSize.equals(size, picture)
                        && IPictureSize.equals(size, nextpicture)) {
                        ++extendLeft;
                        continue extendLeft;
                    }
                }
                break extendLeft;
            }
            extendRight:
            while (extendRight <= radius) {
                int px = extendRight;
                int x = (extendRight + 1);
                for (int y = ymin; y <= ymax; ++y) {
                    Picture picture = point.getNeighborPoint(px, y).getPicture();
                    Picture nextpicture = point.getNeighborPoint(x, y).getPicture();
                    if (IPictureSize.equals(size, picture)
                        && IPictureSize.equals(size, nextpicture)) {
                        ++extendRight;
                        continue extendRight;
                    }
                }
                break extendRight;
            }
        }
        WorldPicturePoint topLeftPoint = point.getNeighborPoint(-extendLeft, -extendUp);
        int sizeX = extendLeft + 1 + extendRight;
        int sizeY = extendUp + 1 + extendDown;
        return new Provider(topLeftPoint, size, sizeX, sizeY);
    }
    
    public static class Provider implements INamedContainerProvider, Consumer<PacketBuffer> {
        
        protected final WorldPicturePoint point;
        protected final IPictureSize pictureSize;
        protected final int sizeX, sizeY;
        
        protected boolean editable = false;
        
        protected Provider(WorldPicturePoint topLeftPoint,
                IPictureSize pictureSize,
                int sizeX,
                int sizeY) {
            this.point = topLeftPoint;
            this.pictureSize = pictureSize;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }
        
        public void setEditable(boolean editable) {
            this.editable = editable;
        }
        
        @Override
        public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
            return new ContainerPaintingViewServer(
                    id,
                    this.point,
                    this.pictureSize,
                    this.sizeX,
                    this.sizeY,
                    this.editable);
        }
        
        @Override
        public ITextComponent getDisplayName() {
            return NarratorChatListener.field_216868_a;
        }
        
        @Override
        public void accept(PacketBuffer buf) {
            buf.writeInt(this.pictureSize.getWidth());
            buf.writeInt(this.pictureSize.getHeight());
            buf.writeInt(this.sizeX);
            buf.writeInt(this.sizeY);
            buf.writeBoolean(this.point.provider.hasAlpha());
            buf.writeBoolean(this.editable);
        }
    }
}
