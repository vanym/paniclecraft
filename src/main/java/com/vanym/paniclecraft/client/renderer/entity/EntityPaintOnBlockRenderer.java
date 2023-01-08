package com.vanym.paniclecraft.client.renderer.entity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.RenderBlocksPainting;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class EntityPaintOnBlockRenderer extends Render {
    
    public int renderPictureType = 2;
    
    @Override
    public void doRender(
            Entity entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks) {
        TileEntityPaintingRenderer.renderInWorldEnable();
        this.doRender((EntityPaintOnBlock)entity, x, y, z, partialTicks);
        TileEntityPaintingRenderer.renderInWorldDisable();
    }
    
    protected void doRender(
            EntityPaintOnBlock entityPOB,
            double x,
            double y,
            double z,
            float partialTicks) {
        World world = this.renderManager.worldObj;
        int xCoord = entityPOB.getBlockX();
        int yCoord = entityPOB.getBlockY();
        int zCoord = entityPOB.getBlockZ();
        Profiler theProfiler;
        if (Core.instance.painting.clientConfig.renderProfiling) {
            theProfiler = world.theProfiler;
        } else {
            theProfiler = null;
        }
        if (theProfiler != null) {
            theProfiler.startSection(DEF.MOD_ID + ":" + EntityPaintOnBlock.IN_MOD_ID);
        }
        Tessellator tessellator = Tessellator.instance;
        tessellator.setTranslation(x - entityPOB.posX,
                                   y - entityPOB.posY,
                                   z - entityPOB.posZ);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        if (this.renderPictureType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("picture");
            }
            PaintOnBlock wrapBlock = new PaintOnBlock();
            wrapBlock.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.PICTURE);
            Block realBlock = world.getBlock(xCoord, yCoord, zCoord);
            realBlock.setBlockBoundsBasedOnState(world, xCoord, yCoord, zCoord);
            final double expandBase = 0.0005D;
            final double expandAdjust = 0.0001D;
            final double expandX = expandBase + Math.pow(x / 4, 2) * expandAdjust;
            final double expandY = expandBase + Math.pow(y / 4, 2) * expandAdjust;
            final double expandZ = expandBase + Math.pow(z / 4, 2) * expandAdjust;
            RenderPaintOnBlocks render =
                    new RenderPaintOnBlocks(world, expandX, expandY, expandZ, wrapBlock, realBlock);
            render.setRenderAllFaces(false);
            render.setMaxAmbientOcclusion(this.renderPictureType);
            for (int side = 0; side < ISidePictureProvider.N; ++side) {
                Picture picture = entityPOB.getPicture(side);
                if (picture == null) {
                    continue;
                }
                render.setSide(side);
                wrapBlock.setRendererSide(side);
                if (!render.willRenderSide(world, xCoord, yCoord, zCoord)) {
                    continue;
                }
                if (theProfiler != null) {
                    theProfiler.startSection(picture.getWidth() + "x" + picture.getHeight());
                    theProfiler.startSection("bind");
                }
                IIcon icon = TileEntityPaintingRenderer.bindTexture(picture, side);
                if (theProfiler != null) {
                    theProfiler.endSection(); // bind
                }
                render.setOverrideBlockTexture(icon);
                tessellator.startDrawingQuads();
                render.renderBlockByRenderType(wrapBlock, xCoord, yCoord, zCoord);
                tessellator.draw();
                if (theProfiler != null) {
                    theProfiler.endSection(); // WxH
                }
            }
            if (theProfiler != null) {
                theProfiler.endSection(); // picture
            }
            wrapBlock.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.NONE);
            wrapBlock.setRendererSide(-1);
            render.clearOverrideBlockTexture();
            render.resetMaxAmbientOcclusion();
        }
        tessellator.setTranslation(0, 0, 0);
        if (theProfiler != null) {
            theProfiler.endSection(); // root
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
    
    protected static class RenderPaintOnBlocks extends RenderBlocksPainting {
        
        protected final double expandX;
        protected final double expandY;
        protected final double expandZ;
        
        protected final Block wrapBlock;
        protected final Block realBlock;
        
        protected int side;
        
        public RenderPaintOnBlocks(IBlockAccess world,
                double expandX,
                double expandY,
                double expandZ,
                Block wrapBlock,
                Block realBlock) {
            super(world);
            this.expandX = expandX;
            this.expandY = expandY;
            this.expandZ = expandZ;
            this.wrapBlock = wrapBlock;
            this.realBlock = realBlock;
        }
        
        public void setSide(int side) {
            this.side = side;
        }
        
        public boolean willRenderSide(IBlockAccess world, int x, int y, int z) {
            switch (this.realBlock.getRenderType()) {
                case 10:
                    return true;
                default:
                    ForgeDirection pside = ForgeDirection.getOrientation(this.side);
                    return this.realBlock.shouldSideBeRendered(world, x + pside.offsetX,
                                                               y + pside.offsetY,
                                                               z + pside.offsetZ, this.side);
            }
        }
        
        @Override
        public void overrideBlockBounds(
                double minX,
                double minY,
                double minZ,
                double maxX,
                double maxY,
                double maxZ) {
            super.overrideBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
            this.expandBounds();
        }
        
        @Override
        public void setRenderBoundsFromBlock(Block block) {
            super.setRenderBoundsFromBlock(block);
            this.expandBounds();
        }
        
        protected void expandBounds() {
            ForgeDirection pside = ForgeDirection.getOrientation(this.side);
            this.renderMinX += pside.offsetX * this.expandX;
            this.renderMinY += pside.offsetY * this.expandY;
            this.renderMinZ += pside.offsetZ * this.expandZ;
            this.renderMaxX += pside.offsetX * this.expandX;
            this.renderMaxY += pside.offsetY * this.expandY;
            this.renderMaxZ += pside.offsetZ * this.expandZ;
        }
        
        @Override
        public boolean renderBlockByRenderType(
                Block block,
                int x,
                int y,
                int z) {
            switch (this.realBlock.getRenderType()) {
                case 10: // BlockStairs
                    if (this.realBlock instanceof BlockStairs) {
                        return this.renderBlockStairs((BlockStairs)this.realBlock, x, y, z);
                    }
                default:
                    this.setRenderBoundsFromBlock(this.realBlock);
                    return this.renderStandardBlock(block, x, y, z);
            }
        }
        
        @Override
        public boolean renderStandardBlock(
                Block block,
                int x,
                int y,
                int z) {
            boolean orig = this.field_152631_f;
            this.field_152631_f = true;
            boolean ret = super.renderStandardBlock(this.wrapBlock, x, y, z);
            this.field_152631_f = orig;
            return ret;
        }
    }
    
    protected static class PaintOnBlock extends BlockPaintingFrame {
        
        @Override
        @SideOnly(Side.CLIENT)
        public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
            return side == this.specialRendererSide;
        }
    }
}
