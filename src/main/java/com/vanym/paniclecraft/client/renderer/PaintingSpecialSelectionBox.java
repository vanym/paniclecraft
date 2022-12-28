package com.vanym.paniclecraft.client.renderer;

import java.awt.Color;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class PaintingSpecialSelectionBox {
    
    protected boolean onlyCancel = false;
    
    protected Color color = null;
    
    public PaintingSpecialSelectionBox() {
        this(false);
    }
    
    public PaintingSpecialSelectionBox(boolean onlyCancel) {
        this(false, null);
    }
    
    public PaintingSpecialSelectionBox(boolean onlyCancel, Color color) {
        this.onlyCancel = onlyCancel;
        this.color = color;
    }
    
    public void setOnlyCancel(boolean onlyCancel) {
        this.onlyCancel = onlyCancel;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    @SubscribeEvent
    public void drawSelectionBox(DrawBlockHighlightEvent event) {
        final MovingObjectPosition target = event.target;
        if (event.currentItem == null || target == null
            || target.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        Item item = event.currentItem.getItem();
        if (!(item instanceof IPaintingTool)) {
            return;
        }
        IPaintingTool tool = (IPaintingTool)item;
        if (!tool.getPaintingToolType(event.currentItem).isPixelSelector()) {
            return;
        }
        Picture picture = BlockPaintingContainer.getPicture(event.player.worldObj, target);
        if (picture == null) {
            return;
        }
        event.setCanceled(true);
        if (this.onlyCancel) {
            return;
        }
        PaintingSide pside = PaintingSide.getSize(target.sideHit);
        double radius = tool.getPaintingToolRadius(event.currentItem, picture);
        int width = picture.getWidth();
        int height = picture.getHeight();
        Vec3 inBlockVec = MainUtils.getInBlockVec(target);
        Vec3 inPictureVec = pside.toPaintingCoords(inBlockVec);
        double outline = 0.002D;
        double zOutline = inPictureVec.zCoord + outline;
        int px = (int)(inPictureVec.xCoord * width);
        int py = (int)(inPictureVec.yCoord * height);
        int max = (int)Math.ceil(radius);
        Builder<AxisAlignedBB> pictureLinesBuilder = Stream.builder();
        {
            boolean[] lasty = new boolean[max * 2 + 1];
            final int offset = max;
            for (int iy = -max; iy <= max; ++iy) {
                boolean lastx = false;
                for (int ix = -max; ix <= max; ++ix) {
                    int cx = px + ix;
                    int cy = py + iy;
                    int nx = cx / width + (cx % width < 0 ? -1 : 0);
                    int ny = cy / height + (cy % height < 0 ? -1 : 0);
                    boolean candraw = (ix * ix + iy * iy < radius * radius)
                        && picture.canEdit(picture.getNeighborPicture(nx, ny));
                    if (candraw != lastx) {
                        AxisAlignedBB line = AxisAlignedBB.getBoundingBox((double)(cx + 0) / width,
                                                                          (double)(cy + 0) / height,
                                                                          zOutline,
                                                                          (double)(cx + 0) / width,
                                                                          (double)(cy + 1) / height,
                                                                          zOutline);
                        pictureLinesBuilder.add(line);
                    }
                    if (candraw != lasty[ix + offset]) {
                        AxisAlignedBB line = AxisAlignedBB.getBoundingBox((double)(cx + 0) / width,
                                                                          (double)(cy + 0) / height,
                                                                          zOutline,
                                                                          (double)(cx + 1) / width,
                                                                          (double)(cy + 0) / height,
                                                                          zOutline);
                        pictureLinesBuilder.add(line);
                        
                    }
                    lastx = candraw;
                    lasty[ix + offset] = candraw;
                }
            }
        }
        Stream<AxisAlignedBB> pictureLines = pictureLinesBuilder.build();
        double dx = event.player.lastTickPosX +
                    (event.player.posX - event.player.lastTickPosX) * (double)event.partialTicks;
        double dy = event.player.lastTickPosY +
                    (event.player.posY - event.player.lastTickPosY) * (double)event.partialTicks;
        double dz = event.player.lastTickPosZ +
                    (event.player.posZ - event.player.lastTickPosZ) * (double)event.partialTicks;
        Stream<AxisAlignedBB> frameLines = pictureLines.map(b->pside.fromPaintingCoords(b)
                                                                    .offset(target.blockX,
                                                                            target.blockY,
                                                                            target.blockZ)
                                                                    .offset(-dx, -dy, -dz));
        this.drawLines(frameLines);
    }
    
    protected void drawLines(Stream<AxisAlignedBB> lines) {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                                 GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        lines.forEach(box->this.drawLine(box));
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    protected void drawLine(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);
        if (this.color != null) {
            tessellator.setColorOpaque_I(this.color.getRGB());
        }
        if (box.minX != box.maxX) {
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.maxX, box.minY, box.minZ);
        }
        if (box.minY != box.maxY) {
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.minX, box.maxY, box.minZ);
        }
        if (box.minZ != box.maxZ) {
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.minX, box.minY, box.maxZ);
        }
        tessellator.draw();
    }
}
