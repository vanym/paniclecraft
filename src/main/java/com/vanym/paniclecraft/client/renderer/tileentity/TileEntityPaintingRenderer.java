package com.vanym.paniclecraft.client.renderer.tileentity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.block.BlockPainting;
import com.vanym.paniclecraft.client.utils.IconFlippedBugFixed;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.utils.Painting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingRenderer extends TileEntitySpecialRenderer {
    
    protected RenderBlocksWorldless renderBlocksWorldless = new RenderBlocksWorldless();
    
    protected RenderBlocks renderBlocks;
    
    @Override
    public void func_147496_a(World world) {
        this.renderBlocks = new RenderBlocks(world);
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        this.renderTileEntityAt((TileEntityPainting)tile, x, y, z, f);
    }
    
    public void renderTileEntityAt(TileEntityPainting tile, double x, double y, double z, float f) {
        this.renderTileEntityAtWorld(tile, x, y, z, f);
    }
    
    public void renderTileEntityAtItem(TileEntityPainting tile) {
        this.renderTileEntity(tile, 0, 0, 0, 0);
    }
    
    public void renderTileEntityAtWorld(
            TileEntityPainting tile,
            double x,
            double y,
            double z,
            float f) {
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
    
    protected void renderTileEntity(
            TileEntityPainting tile,
            double x,
            double y,
            double z,
            float f) {
        boolean worldless = (tile.getWorldObj() == null);
        int meta = tile.getBlockMetadata();
        RenderBlocks render;
        if (worldless) {
            this.renderBlocksWorldless.setMeta(meta);
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
        BlockPainting block = (BlockPainting)tile.getBlockType();
        block.setRendererPhase(BlockPainting.SpecialRendererPhase.FRAME);
        block.setBlockBoundsBasedOnState(meta);
        render.setRenderBoundsFromBlock(block);
        render.renderStandardBlock(tile.getBlockType(), tile.xCoord, tile.yCoord, tile.zCoord);
        tessellator.draw();
        tessellator.startDrawingQuads();
        {
            Painting picture = tile.getPainting(meta);
            IIcon icon = bindTexture(picture, meta);
            render.setOverrideBlockTexture(icon);
            block.setRendererPhase(BlockPainting.SpecialRendererPhase.PAINTING);
            render.renderStandardBlock(tile.getBlockType(), tile.xCoord, tile.yCoord, tile.zCoord);
            block.setRendererPhase(BlockPainting.SpecialRendererPhase.NONE);
            render.clearOverrideBlockTexture();
        }
        tessellator.draw();
        tessellator.setTranslation(0, 0, 0);
    }
    
    public static IIcon bindTexture(Painting picture, int side) {
        if (picture.texID <= 0) {
            picture.texID = GL11.glGenTextures();
            picture.getPic();
        }
        if (picture.hasPic()) {
            ByteBuffer textureBuffer =
                    ByteBuffer.allocateDirect(picture.getPic().length);
            textureBuffer.order(ByteOrder.nativeOrder());
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                                 GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                                 GL11.GL_NEAREST);
            textureBuffer.clear();
            textureBuffer.put(picture.getPic());
            textureBuffer.flip();
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB,
                              picture.getRow(),
                              picture.getRow(), 0, GL11.GL_RGB,
                              GL11.GL_UNSIGNED_BYTE,
                              textureBuffer);
            picture.delPic();
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, picture.texID);
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
        
        public RenderBlocksWorldless() {
            super(null);
        }
        
        public void setMeta(int meta) {
            this.meta = meta;
        }
        
        @Override
        public boolean renderStandardBlock(Block block, int x, int y, int z) {
            BlockPainting blockPainting = (BlockPainting)block;
            this.enableAO = false;
            Tessellator tessellator = Tessellator.instance;
            int side = 0;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.renderFaceYNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 1;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                this.renderFaceYPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 2;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                this.renderFaceZNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 3;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                this.renderFaceZPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 4;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                this.renderFaceXNeg(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            side = 5;
            if (blockPainting.shouldSideBeRendered(side, this.meta)) {
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                this.renderFaceXPos(block, (double)x, (double)y, (double)z,
                                    block.getIcon(side, this.meta));
            }
            return true;
        }
    }
}
