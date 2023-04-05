package com.vanym.paniclecraft.client.renderer.tileentity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingRenderer
        extends
            TileEntitySpecialRenderer<TileEntityPaintingContainer> {
    
    public int renderFrameType = 1;
    public int renderPictureType = 2;
    
    protected BlockRendererDispatcher blockRenderer;
    
    @Override
    public void render(
            TileEntityPaintingContainer te,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        if (this.blockRenderer == null) {
            this.blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        }
        this.renderInWorld(te, x, y, z, partialTicks, destroyStage, alpha);
    }
    
    public static void renderInWorldEnable() {
        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }
    }
    
    public static void renderInWorldDisable() {
        RenderHelper.enableStandardItemLighting();
    }
    
    public void renderInWorld(
            TileEntityPaintingContainer te,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        // based on TileEntityRendererPiston
        renderInWorldEnable();
        this.renderPainting(te, x, y, z, partialTicks, destroyStage, alpha);
        renderInWorldDisable();
    }
    
    protected void renderPainting(
            TileEntityPaintingContainer tile,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha) {
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();
        IBlockState state = world.getBlockState(pos);
        state = this.getActualState(state, tile);
        long rand = MathHelper.getPositionRandom(pos);
        IBakedModel model = this.blockRenderer.getModelForState(state);
        BlockModelRenderer render = this.blockRenderer.getBlockModelRenderer();
        Profiler theProfiler = null;
        if (world != null && Core.instance.painting.clientConfig.renderProfiling) {
            theProfiler = world.profiler;
        }
        if (theProfiler != null) {
            theProfiler.startSection(String.valueOf(TileEntity.getKey(tile.getClass())));
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.setTranslation(x - (double)pos.getX(), y - (double)pos.getY(), z - (double)pos.getZ());
        if (this.renderFrameType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("frame");
            }
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            IBakedModel frameModel = new BakedModelFrame(model);
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            if (this.renderFrameType > 0) {
                render.renderModelSmooth(world, frameModel, state, pos, buf, true, rand);
            } else {
                render.renderModelFlat(world, frameModel, state, pos, buf, true, rand);
            }
            tessellator.draw();
            if (theProfiler != null) {
                theProfiler.endSection(); // frame
            }
        }
        if (this.renderPictureType >= 0) {
            for (int side = 0; side < this.getN(); ++side) {
                Picture picture = this.getPicture(tile, side);
                if (picture == null) {
                    continue;
                }
                if (theProfiler != null) {
                    theProfiler.startSection("picture");
                    theProfiler.startSection(picture.getWidth() + "x" + picture.getHeight());
                    theProfiler.startSection("bind");
                }
                TextureAtlasSprite sprite = bindTexture(picture);
                if (theProfiler != null) {
                    theProfiler.endSection(); // bind
                }
                IBakedModel pictureModel = new BakedModelPicture(model, side, sprite);
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                if (this.renderPictureType > 0) {
                    render.renderModelSmooth(world, pictureModel, state, pos, buf, true, rand);
                } else {
                    render.renderModelFlat(world, pictureModel, state, pos, buf, true, rand);
                }
                tessellator.draw();
                if (theProfiler != null) {
                    theProfiler.endSection(); // WxH
                    theProfiler.endSection(); // picture
                }
            }
        }
        buf.setTranslation(0.0D, 0.0D, 0.0D);
        if (theProfiler != null) {
            theProfiler.endSection(); // root
        }
    }
    
    protected int getN() {
        return 1;
    }
    
    protected IBlockState getActualState(IBlockState state, TileEntityPaintingContainer tile) {
        return state;
    }
    
    protected Picture getPicture(TileEntityPaintingContainer tile, int side) {
        TileEntityPainting tileP = (TileEntityPainting)tile;
        return tileP.getPicture();
    }
    
    public static TextureAtlasSprite bindTexture(Picture picture) {
        boolean newtexture = false;
        if (picture.texture == null) {
            picture.texture = GlStateManager.generateTexture();
            newtexture = true;
        }
        GlStateManager.bindTexture(picture.texture);
        if (newtexture || !picture.imageChangeProcessed) {
            ByteBuffer textureBuffer = picture.getImageAsDirectByteBuffer();
            if (textureBuffer != null) {
                final int width = picture.getWidth();
                final int height = picture.getHeight();
                final int format = picture.hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB;
                textureBuffer.order(ByteOrder.nativeOrder());
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                                     GL11.GL_NEAREST);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format,
                                  width, height, 0, format,
                                  GL11.GL_UNSIGNED_BYTE,
                                  textureBuffer);
            }
            picture.imageChangeProcessed = true;
        }
        return IconUtils.full(picture.getWidth(), picture.getHeight());
    }
    
    protected static class BakedModelFrame extends BakedModelWrapper<IBakedModel> {
        
        public BakedModelFrame(IBakedModel originalModel) {
            super(originalModel);
        }
        
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            return super.getQuads(state, side, rand).stream()
                                                    .filter(q->!q.hasTintIndex())
                                                    .collect(Collectors.toList());
        }
    }
    
    public static class BakedModelPicture extends BakedModelWrapper<IBakedModel> {
        
        protected final int index;
        protected final TextureAtlasSprite sprite;
        
        public BakedModelPicture(IBakedModel originalModel, int index, TextureAtlasSprite sprite) {
            super(originalModel);
            this.index = index;
            this.sprite = sprite;
        }
        
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            List<BakedQuad> quads = super.getQuads(state, side, rand);
            return quads.stream()
                        .filter(q->q.getTintIndex() == this.index)
                        .map(q->new BakedQuadRetexturedTintless(q, this.sprite))
                        .collect(Collectors.toList());
        }
        
        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.sprite;
        }
        
        protected static class BakedQuadRetexturedTintless extends BakedQuadRetextured {
            
            public BakedQuadRetexturedTintless(BakedQuad quad, TextureAtlasSprite textureIn) {
                super(quad, textureIn);
            }
            
            @Override
            public boolean hasTintIndex() {
                return false;
            }
        }
    }
}
