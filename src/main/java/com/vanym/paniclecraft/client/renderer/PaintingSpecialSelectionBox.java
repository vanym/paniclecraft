package com.vanym.paniclecraft.client.renderer;

import java.awt.Color;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.PaintingSide;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent.HighlightBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
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
    
    protected Color getColor() {
        if (this.color != null) {
            return this.color;
        } else {
            return new Color(0.0F, 0.0F, 0.0F, 0.4F);
        }
    }
    
    @SubscribeEvent
    public void drawSelectionBox(HighlightBlock event) {
        if (event.getTarget().getType() != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockRayTraceResult target = event.getTarget();
        Entity entity = event.getInfo().getRenderViewEntity();
        if (!PlayerEntity.class.isInstance(entity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity)entity;
        ItemStack stack = Stream.of(Hand.MAIN_HAND, Hand.OFF_HAND)
                                .map(player::getHeldItem)
                                .filter(s->!s.isEmpty())
                                .findFirst()
                                .orElse(ItemStack.EMPTY);
        Item item = stack.getItem();
        if (!(item instanceof IPaintingTool)) {
            return;
        }
        IPaintingTool tool = (IPaintingTool)item;
        if (!tool.getPaintingToolType(stack).isPixelSelector()) {
            return;
        }
        BlockPos pos = target.getPos();
        Picture picture = new WorldPicturePoint(
                WorldPictureProvider.ANYTILE,
                player.world,
                pos,
                target.getFace().getIndex()).getPicture();
        if (picture == null) {
            return;
        }
        event.setCanceled(true);
        if (this.onlyCancel) {
            return;
        }
        PaintingSide pside = PaintingSide.getSide(target.getFace());
        double radius = tool.getPaintingToolRadius(stack, picture);
        int width = picture.getWidth();
        int height = picture.getHeight();
        Vec3d inBlockVec = GeometryUtils.getInBlockVec(target);
        Vec3d inPictureVec = pside.axes.toSideCoords(inBlockVec);
        double outline = 0.002D;
        double zOutline = inPictureVec.z + outline;
        int px = (int)(inPictureVec.x * width);
        int py = (int)(inPictureVec.y * height);
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
                        AxisAlignedBB line = new AxisAlignedBB(
                                (double)(cx + 0) / width,
                                (double)(cy + 0) / height,
                                zOutline,
                                (double)(cx + 0) / width,
                                (double)(cy + 1) / height,
                                zOutline);
                        pictureLinesBuilder.add(line);
                    }
                    if (candraw != lasty[ix + offset]) {
                        AxisAlignedBB line = new AxisAlignedBB(
                                (double)(cx + 0) / width,
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
        double dx = player.lastTickPosX +
                    (player.posX - player.lastTickPosX) * (double)event.getPartialTicks();
        double dy = player.lastTickPosY +
                    (player.posY - player.lastTickPosY) * (double)event.getPartialTicks();
        double dz = player.lastTickPosZ +
                    (player.posZ - player.lastTickPosZ) * (double)event.getPartialTicks();
        Stream<AxisAlignedBB> frameLines = pictureLines.map(b->pside.axes.fromSideCoords(b)
                                                                         .offset(pos.getX(),
                                                                                 pos.getY(),
                                                                                 pos.getZ())
                                                                         .offset(-dx, -dy, -dz));
        this.drawLines(frameLines);
    }
    
    protected void drawLines(Stream<AxisAlignedBB> lines) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                         GlStateManager.SourceFactor.ONE,
                                         GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0F);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        lines.forEach(box->this.drawLine(box));
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
    
    protected void drawLine(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        Color color = this.getColor();
        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        if (box.minX != box.maxX) {
            buf.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex();
            buf.pos(box.maxX, box.minY, box.minZ).color(r, g, b, a).endVertex();
        }
        if (box.minY != box.maxY) {
            buf.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex();
            buf.pos(box.minX, box.maxY, box.minZ).color(r, g, b, a).endVertex();
        }
        if (box.minZ != box.maxZ) {
            buf.pos(box.minX, box.minY, box.minZ).color(r, g, b, a).endVertex();
            buf.pos(box.minX, box.minY, box.maxZ).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }
}
