package com.vanym.paniclecraft.client.renderer.tileentity;

import java.util.List;

import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingFrameRenderer extends TileEntityPaintingRenderer {
    
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
        boolean worldless = (tile.getWorldObj() == null);
        RenderBlocks render;
        if (worldless) {
            this.renderBlocksWorldless.setTile(tile);
            render = this.renderBlocksWorldless;
        } else {
            render = this.renderBlocks;
        }
        this.bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        render.setRenderAllFaces(false);
        tessellator.setTranslation(x - tile.xCoord, y - tile.yCoord, z - tile.zCoord);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        BlockPaintingFrame block = (BlockPaintingFrame)tile.getBlockType();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.FRAME);
        block.setRendererSide(-1);
        List<AxisAlignedBB> frameBoxes = block.getFrameBoxes();
        for (AxisAlignedBB box : frameBoxes) {
            render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                       box.maxX, box.maxY, box.maxZ);
            block.setRendererBox(box);
            render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
        }
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.FRAMEINSIDE);
        final double paintingWidth = block.getPaintingOutlineSize();
        for (int side = 0; side < 6; ++side) {
            Picture picture = tile.getPainting(side);
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
        tessellator.draw();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.PAINTING);
        for (int side = 0; side < 6; ++side) {
            Picture picture = tile.getPainting(side);
            if (picture == null) {
                continue;
            }
            AxisAlignedBB box = MainUtils.getBoundsBySide(side, paintingWidth);
            render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                       box.maxX, box.maxY, box.maxZ);
            block.setRendererBox(box);
            block.setRendererSide(side);
            IIcon icon = bindTexture(picture, side);
            render.setOverrideBlockTexture(icon);
            tessellator.startDrawingQuads();
            render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
            tessellator.draw();
        }
        block.setRendererBox(null);
        render.clearOverrideBlockTexture();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.NONE);
        block.setRendererSide(-1);
        tessellator.setTranslation(0, 0, 0);
    }
}
