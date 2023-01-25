package com.vanym.paniclecraft.container;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.FixedPictureSize;
import com.vanym.paniclecraft.core.component.painting.IPictureSize;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.network.message.MessageOpenPaintingView;

import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerPaintingViewServer extends ContainerPaintingViewBase {
    
    public final WorldPicturePoint point;
    
    protected boolean editable = false;
    
    public ContainerPaintingViewServer(WorldPicturePoint point,
            IPictureSize pictureSize,
            int sizeX,
            int sizeY) {
        super(pictureSize, sizeX, sizeY);
        this.point = point;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public boolean isEditable() {
        return this.editable;
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
    
    public static ContainerPaintingViewServer makeFullView(WorldPicturePoint point, int maxRadius) {
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
        return new ContainerPaintingViewServer(
                point.getNeighborPoint(-extendLeft, -extendUp),
                size,
                extendLeft + 1 + extendRight,
                extendUp + 1 + extendDown);
    }
    
    public static void openGui(EntityPlayerMP player, ContainerPaintingViewServer view) {
        player.getNextWindowId();
        player.closeContainer();
        int windowId = player.currentWindowId;
        Core.instance.network.sendTo(new MessageOpenPaintingView(
                windowId,
                view.pictureSize.getWidth(),
                view.pictureSize.getHeight(),
                view.sizeX,
                view.sizeY,
                view.point.provider.hasAlpha(),
                view.editable), player);
        player.openContainer = view;
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters(player);
    }
}
