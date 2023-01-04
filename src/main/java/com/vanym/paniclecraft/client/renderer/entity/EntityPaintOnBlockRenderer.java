package com.vanym.paniclecraft.client.renderer.entity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.client.renderer.RenderBlocksPainting;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class EntityPaintOnBlockRenderer extends Render {
    
    public int renderPictureType = 2;
    
    protected final PaintOnBlock block = new PaintOnBlock();
    
    @Override
    public void doRender(
            Entity entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks) {
        if (!Core.instance.painting.clientConfig.renderPaintOnBlock) {
            return;
        }
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
        RenderBlocksPainting render = new RenderBlocksPainting(world);
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
        render.setRenderAllFaces(false);
        tessellator.setTranslation(x - entityPOB.posX,
                                   y - entityPOB.posY,
                                   z - entityPOB.posZ);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        if (this.renderPictureType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("picture");
            }
            render.setMaxAmbientOcclusion(this.renderPictureType);
            this.block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.PICTURE);
            Block realBlock = world.getBlock(entityPOB.getBlockX(),
                                             entityPOB.getBlockY(),
                                             entityPOB.getBlockZ());
            realBlock.setBlockBoundsBasedOnState(world, entityPOB.getBlockX(),
                                                 entityPOB.getBlockY(),
                                                 entityPOB.getBlockZ());
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(realBlock.getBlockBoundsMinX(),
                                                             realBlock.getBlockBoundsMinY(),
                                                             realBlock.getBlockBoundsMinZ(),
                                                             realBlock.getBlockBoundsMaxX(),
                                                             realBlock.getBlockBoundsMaxY(),
                                                             realBlock.getBlockBoundsMaxZ());
            this.block.setRendererBox(box);
            final double expandBase = 0.0005D;
            final double expandAdjust = 0.0001D;
            final double expandX = expandBase + Math.pow(x / 4, 2) * expandAdjust;
            final double expandY = expandBase + Math.pow(y / 4, 2) * expandAdjust;
            final double expandZ = expandBase + Math.pow(z / 4, 2) * expandAdjust;
            for (int side = 0; side < EntityPaintOnBlock.N; ++side) {
                Picture picture = entityPOB.getExistingPicture(side);
                if (picture == null) {
                    continue;
                }
                ForgeDirection pside = ForgeDirection.getOrientation(side);
                if (!realBlock.shouldSideBeRendered(world,
                                                    entityPOB.getBlockX() + pside.offsetX,
                                                    entityPOB.getBlockY() + pside.offsetY,
                                                    entityPOB.getBlockZ() + pside.offsetZ,
                                                    side)) {
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
                render.overrideBlockBounds(box.minX + pside.offsetX * expandX,
                                           box.minY + pside.offsetY * expandY,
                                           box.minZ + pside.offsetZ * expandZ,
                                           box.maxX + pside.offsetX * expandX,
                                           box.maxY + pside.offsetY * expandY,
                                           box.maxZ + pside.offsetZ * expandZ);
                this.block.setRendererSide(side);
                render.setOverrideBlockTexture(icon);
                tessellator.startDrawingQuads();
                render.renderStandardBlock(this.block,
                                           entityPOB.getBlockX(),
                                           entityPOB.getBlockY(),
                                           entityPOB.getBlockZ());
                tessellator.draw();
                if (theProfiler != null) {
                    theProfiler.endSection(); // WxH
                }
            }
            if (theProfiler != null) {
                theProfiler.endSection(); // picture
            }
        }
        this.block.setRendererBox(null);
        render.clearOverrideBlockTexture();
        render.resetMaxAmbientOcclusion();
        this.block.setRendererPhase(BlockPaintingContainer.SpecialRendererPhase.NONE);
        this.block.setRendererSide(-1);
        tessellator.setTranslation(0, 0, 0);
        if (theProfiler != null) {
            theProfiler.endSection(); // root
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
    
    protected class PaintOnBlock extends BlockPaintingFrame {
        
        @Override
        @SideOnly(Side.CLIENT)
        public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
            return side == this.specialRendererSide;
        }
    }
}
