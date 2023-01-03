package com.vanym.paniclecraft.client.renderer.tileentity;

import java.util.List;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.RenderBlocksPainting;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingFrameRenderer extends TileEntityPaintingRenderer {
    
    public TileEntityPaintingFrameRenderer() {
        super();
        this.renderFrameType = 0;
    }
    
    @Override
    protected void renderTileEntity(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntity((TileEntityPaintingFrame)tile, x, y, z, f);
    }
    
    protected void renderTileEntity(
            TileEntityPaintingFrame tile,
            double x,
            double y,
            double z,
            float f) {
        World world = tile.getWorldObj();
        Profiler theProfiler = null;
        RenderBlocksPainting render;
        if (world == null) {
            this.renderBlocksWorldless.setTile(tile);
            render = this.renderBlocksWorldless;
            theProfiler = null;
        } else {
            render = this.renderBlocks;
            if (Core.instance.painting.clientConfig.renderProfiling) {
                theProfiler = world.theProfiler;
            }
        }
        if (theProfiler != null) {
            theProfiler.startSection(DEF.MOD_ID + ":" + TileEntityPaintingFrame.IN_MOD_ID);
        }
        Tessellator tessellator = Tessellator.instance;
        render.setRenderAllFaces(false);
        tessellator.setTranslation(x - tile.xCoord, y - tile.yCoord, z - tile.zCoord);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        BlockPaintingFrame block = (BlockPaintingFrame)tile.getBlockType();
        final double paintingWidth = block.getPaintingOutlineSize();
        boolean skipFrame = (this.renderPictureType >= 0);
        for (int side = 0; side < TileEntityPaintingFrame.N; ++side) {
            Picture picture = tile.getPicture(side);
            if (picture == null) {
                skipFrame = false;
                break;
            }
        }
        if (!skipFrame && this.renderFrameType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("frame");
            }
            this.bindTexture(TextureMap.locationBlocksTexture);
            render.setMaxAmbientOcclusion(this.renderFrameType);
            if (theProfiler != null) {
                theProfiler.startSection("outside");
            }
            tessellator.startDrawingQuads();
            block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.FRAME);
            block.setRendererSide(-1);
            List<AxisAlignedBB> frameBoxes = block.getFrameBoxes();
            for (AxisAlignedBB box : frameBoxes) {
                render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                           box.maxX, box.maxY, box.maxZ);
                block.setRendererBox(box);
                render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
            }
            if (theProfiler != null) {
                theProfiler.endStartSection("inside"); // outside
            }
            block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.FRAMEINSIDE);
            for (int side = 0; side < TileEntityPaintingFrame.N; ++side) {
                Picture picture = tile.getPicture(side);
                if (picture == null) {
                    continue;
                }
                AxisAlignedBB box = MainUtils.getBoundsBySide(side, paintingWidth);
                render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                           box.maxX, box.maxY, box.maxZ);
                block.setRendererBox(box);
                block.setRendererSide(side);
                render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
            }
            if (theProfiler != null) {
                theProfiler.endStartSection("draw"); // inside
            }
            tessellator.draw();
            if (theProfiler != null) {
                theProfiler.endSection(); // draw
                theProfiler.endSection(); // frame
            }
        }
        if (this.renderPictureType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("picture");
            }
            render.setMaxAmbientOcclusion(this.renderPictureType);
            block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.PICTURE);
            for (int side = 0; side < TileEntityPaintingFrame.N; ++side) {
                Picture picture = tile.getPicture(side);
                if (picture == null) {
                    continue;
                }
                if (theProfiler != null) {
                    theProfiler.startSection(picture.getWidth() + "x" + picture.getHeight());
                    theProfiler.startSection("bind");
                }
                IIcon icon = bindTexture(picture, side);
                if (theProfiler != null) {
                    theProfiler.endSection();
                }
                AxisAlignedBB box = MainUtils.getBoundsBySide(side, paintingWidth);
                render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                           box.maxX, box.maxY, box.maxZ);
                block.setRendererBox(box);
                block.setRendererSide(side);
                render.setOverrideBlockTexture(icon);
                tessellator.startDrawingQuads();
                render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
                tessellator.draw();
                if (theProfiler != null) {
                    theProfiler.endSection(); // WxH
                }
            }
            if (theProfiler != null) {
                theProfiler.endSection(); // picture
            }
        }
        block.setRendererBox(null);
        render.clearOverrideBlockTexture();
        render.resetMaxAmbientOcclusion();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.NONE);
        block.setRendererSide(-1);
        tessellator.setTranslation(0, 0, 0);
        if (theProfiler != null) {
            theProfiler.endSection(); // root
        }
    }
}
