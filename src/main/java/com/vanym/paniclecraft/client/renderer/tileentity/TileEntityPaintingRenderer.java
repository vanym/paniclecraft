package com.vanym.paniclecraft.client.renderer.tileentity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.client.utils.IconFlippedBugFixed;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingRenderer extends TileEntitySpecialRenderer {
    
    protected final RenderBlocksWorldless renderBlocksWorldless = new RenderBlocksWorldless();
    
    protected RenderBlocks renderBlocks;
    
    @Override
    public void func_147496_a(World world) {
        this.renderBlocks = new RenderBlocks(world);
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntityAtWorld(tile, x, y, z, f);
    }
    
    public void renderTileEntityAtItem(TileEntity tile) {
        this.renderTileEntity(tile, 0, 0, 0, 0);
    }
    
    public void renderTileEntityAtWorld(TileEntity tile, double x, double y, double z, float f) {
        // based on TileEntityRendererPiston
        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        this.renderTileEntity(tile, x, y, z, f);
        RenderHelper.enableStandardItemLighting();
    }
    
    protected void renderTileEntity(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntity((TileEntityPainting)tile, x, y, z, f);
    }
    
    protected void renderTileEntity(
            TileEntityPainting tile,
            double x,
            double y,
            double z,
            float f) {
        World world = tile.getWorldObj();
        int meta = tile.getBlockMetadata();
        Profiler theProfiler = null;
        RenderBlocks render;
        if (world == null) {
            this.renderBlocksWorldless.setMeta(meta);
            render = this.renderBlocksWorldless;
        } else {
            render = this.renderBlocks;
            if (Core.instance.painting.clientConfig.renderProfiling) {
                theProfiler = world.theProfiler;
            }
        }
        if (theProfiler != null) {
            theProfiler.startSection(DEF.MOD_ID + ":" + TileEntityPainting.IN_MOD_ID);
            theProfiler.startSection("frame");
        }
        this.bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        render.setRenderAllFaces(false);
        tessellator.setTranslation(x - tile.xCoord, y - tile.yCoord, z - tile.zCoord);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        BlockPainting block = (BlockPainting)tile.getBlockType();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.FRAME);
        AxisAlignedBB box = block.getBlockBoundsBasedOnState(meta);
        render.overrideBlockBounds(box.minX, box.minY, box.minZ,
                                   box.maxX, box.maxY, box.maxZ);
        block.setRendererBox(box);
        render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
        tessellator.draw();
        Picture picture = tile.getPainting(meta);
        if (theProfiler != null) {
            theProfiler.endStartSection("painting"); // frame
            theProfiler.startSection(picture.getWidth() + "x" + picture.getHeight());
            theProfiler.startSection("bind");
        }
        IIcon icon = bindTexture(picture, meta);
        if (theProfiler != null) {
            theProfiler.endSection(); // bind
        }
        render.setOverrideBlockTexture(icon);
        tessellator.startDrawingQuads();
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.PAINTING);
        render.renderStandardBlock(block, tile.xCoord, tile.yCoord, tile.zCoord);
        block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.NONE);
        render.clearOverrideBlockTexture();
        tessellator.draw();
        tessellator.setTranslation(0, 0, 0);
        if (theProfiler != null) {
            theProfiler.endSection(); // WxH
            theProfiler.endSection(); // painting
            theProfiler.endSection(); // root
        }
    }
    
    protected static IIcon bindTexture(Picture picture, int side) {
        boolean newtexture = false;
        if (picture.texture < 0) {
            picture.texture = GL11.glGenTextures();
            newtexture = true;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texture);
        if (newtexture || !picture.imageChangeProcessed) {
            ByteBuffer textureBuffer = picture.getImageAsDirectByteBuffer();
            if (textureBuffer != null) {
                final int width = picture.getWidth();
                final int height = picture.getHeight();
                textureBuffer.order(ByteOrder.nativeOrder());
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB,
                                  width, height, 0, GL11.GL_RGB,
                                  GL11.GL_UNSIGNED_BYTE,
                                  textureBuffer);
            }
            picture.imageChangeProcessed = true;
        }
        IIcon icon = new FullTextureIcon(1, 1);
        switch (side) {
            case 0:
                icon = new IconFlippedBugFixed(icon, true, false);
            break;
            case 1:
                icon = new IconFlippedBugFixed(icon, true, true);
            break;
        }
        return icon;
    }
    
    protected static class FullTextureIcon implements IIcon {
        // @formatter:off
        protected int width, height;
        public FullTextureIcon(int width, int heigh) { this.width = width; this.height = heigh; }
        public FullTextureIcon() { this(1, 1); }
        @Override public int getIconWidth() { return this.width; }
        @Override public int getIconHeight() { return this.height; }
        @Override public float getMinU() { return 0; }
        @Override public float getMaxU() { return 1; }
        @Override public float getInterpolatedU(double u) { return (float)u / 16; }
        @Override public float getMinV() { return 0; }
        @Override public float getMaxV() { return 1; }
        @Override public float getInterpolatedV(double v) { return (float)v / 16; }
        @Override public String getIconName() { return "fulltextureicon"; }
        // @formatter:on
    }
    
    protected static class RenderBlocksWorldless extends RenderBlocks {
        
        protected int meta;
        protected TileEntity tile;
        
        public RenderBlocksWorldless() {
            super(null);
        }
        
        public void setMeta(int meta) {
            this.meta = meta;
        }
        
        public void setTile(TileEntity tile) {
            this.tile = tile;
        }
        
        @Override
        public boolean renderStandardBlock(Block block, int x, int y, int z) {
            BlockPaintingContainer blockPC = (BlockPaintingContainer)block;
            this.enableAO = false;
            Tessellator tessellator = Tessellator.instance;
            int side = 0;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.renderFaceYNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 1;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                this.renderFaceYPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 2;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                this.renderFaceZNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 3;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                this.renderFaceZPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 4;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                this.renderFaceXNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 5;
            if (blockPC.shouldSideBeRendered(side, this.meta, this.tile)) {
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                this.renderFaceXPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            return true;
        }
    }
}
