package com.vanym.paniclecraft.client.renderer.tileentity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.utils.BakedModelQuadsWrapper;
import com.vanym.paniclecraft.client.utils.BakedModelStatedWrapper;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.client.utils.ModelUtils;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class TileEntityPaintingRenderer extends TileEntityRenderer<TileEntityPaintingContainer> {
    
    public int renderFrameType = 1;
    public int renderPictureType = 2;
    
    protected BlockRendererDispatcher blockRenderer;
    protected ItemRenderer itemRenderer;
    protected ItemStack renderStack;
    
    public TileEntityPaintingRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    protected void initRenderers() {
        if (this.blockRenderer == null) {
            this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        }
        if (this.itemRenderer == null) {
            this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        }
        if (this.renderStack == null) {
            this.renderStack = new ItemStack(Core.instance.painting.itemPainting);
        }
    }
    
    @Override
    public void render(
            TileEntityPaintingContainer te,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        this.renderPainting(te, partialTicks, ms, buffer, combinedLight, combinedOverlay);
    }
    
    public void renderByItem(
            TileEntityPaintingContainer te,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        this.renderPainting(te, 0.0F, ms, buffer, combinedLight, combinedOverlay);
    }
    
    protected void renderPainting(
            TileEntityPaintingContainer tile,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        this.initRenderers();
        World world = tile.getLevel();
        BlockPos pos = tile.getBlockPos();
        BlockState state = this.getActualState(tile);
        long rand = tile.hasLevel() ? state.getSeed(pos) : 0;
        IBakedModel model = this.blockRenderer.getBlockModel(state);
        if (!tile.hasLevel()) {
            model = new BakedModelStatedWrapper<>(model, state);
        }
        BlockModelRenderer render = this.blockRenderer.getModelRenderer();
        if (this.renderFrameType >= 0) {
            IBakedModel frameModel = new BakedModelFrame(model);
            RenderType type = RenderType.cutoutMipped();
            IVertexBuilder vertexer = buffer.getBuffer(type);
            if (tile.hasLevel()) {
                ForgeHooksClient.setRenderLayer(type);
                if (this.renderFrameType > 0) {
                    render.renderModelSmooth(world, frameModel, state, pos, ms, vertexer,
                                             true, new Random(rand), rand, combinedOverlay,
                                             EmptyModelData.INSTANCE);
                } else {
                    render.renderModelFlat(world, frameModel, state, pos, ms, vertexer,
                                           true, new Random(rand), rand, combinedOverlay,
                                           EmptyModelData.INSTANCE);
                }
                ForgeHooksClient.setRenderLayer(null);
            } else {
                this.itemRenderer.renderModelLists(frameModel, this.renderStack,
                                                   combinedLight, combinedOverlay,
                                                   ms, vertexer);
            }
        }
        if (this.renderPictureType >= 0) {
            IRenderTypeBuffer wrappedBuffer = wrapBuffer(ms, buffer);
            int size = this.getSize(tile);
            for (int side = 0; side < size; ++side) {
                Picture picture = this.getPicture(tile, side);
                if (picture == null) {
                    continue;
                }
                IBakedModel pictureModel = new BakedModelPicture(
                        model,
                        side,
                        IconUtils.full(picture.getWidth(), picture.getHeight()));
                RenderType type = new PictureRenderType(picture);
                IVertexBuilder vertexer = wrappedBuffer.getBuffer(type);
                if (tile.hasLevel()) {
                    ForgeHooksClient.setRenderLayer(type);
                    if (this.renderPictureType > 0) {
                        render.renderModelSmooth(world, pictureModel, state, pos, ms, vertexer,
                                                 true, new Random(rand), rand, combinedOverlay,
                                                 EmptyModelData.INSTANCE);
                    } else {
                        render.renderModelFlat(world, pictureModel, state, pos, ms, vertexer,
                                               true, new Random(rand), rand, combinedOverlay,
                                               EmptyModelData.INSTANCE);
                    }
                    ForgeHooksClient.setRenderLayer(null);
                } else {
                    this.itemRenderer.renderModelLists(pictureModel, this.renderStack,
                                                       combinedLight, combinedOverlay,
                                                       ms, vertexer);
                }
            }
        }
    }
    
    public static void renderInWorldEnable() {
        RenderHelper.turnOff();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                               GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        
        if (Minecraft.useAmbientOcclusion()) {
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
        } else {
            RenderSystem.shadeModel(GL11.GL_FLAT);
        }
    }
    
    public static void renderInWorldDisable() {
        RenderHelper.turnBackOn();
    }
    
    protected BlockState getActualState(TileEntityPaintingContainer tile) {
        if (tile.hasLevel()) {
            World world = tile.getLevel();
            return world.getBlockState(tile.getBlockPos());
        }
        return Core.instance.painting.blockPainting.defaultBlockState();
    }
    
    protected Picture getPicture(TileEntityPaintingContainer tile, int index) {
        TileEntityPainting tileP = (TileEntityPainting)tile;
        return tileP.getPicture();
    }
    
    protected int getSize(TileEntityPaintingContainer tile) {
        return 1;
    }
    
    public static TextureAtlasSprite bindTexture(Picture picture) {
        boolean newtexture = false;
        if (picture.texture == null) {
            picture.texture = GlStateManager._genTexture();
            newtexture = true;
        }
        GlStateManager._bindTexture(picture.texture);
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
                GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format,
                                  width, height, 0, format,
                                  GL11.GL_UNSIGNED_BYTE,
                                  textureBuffer);
            }
            picture.imageChangeProcessed = true;
        }
        return IconUtils.full(picture.getWidth(), picture.getHeight());
    }
    
    protected static IRenderTypeBuffer wrapBuffer(
            MatrixStack ms,
            IRenderTypeBuffer buffer) {
        ms.pushPose();
        MatrixStack.Entry entry = ms.last();
        ms.popPose();
        entry.pose().multiply(Matrix4f.createScaleMatrix(-1.0F, 1.0F, -1.0F));
        entry.normal().mul(Matrix3f.createScaleMatrix(1.0F, 1.0F, -1.0F));
        return new IRenderTypeBuffer() {
            @Override
            public IVertexBuilder getBuffer(RenderType type) {
                return new MatrixApplyingVertexBuilder(buffer.getBuffer(type), entry);
            }
        };
    }
    
    protected static class BakedModelFrame extends BakedModelQuadsWrapper {
        
        public BakedModelFrame(IBakedModel originalModel) {
            super(originalModel);
        }
        
        @Override
        protected List<BakedQuad> wrapQuads(List<BakedQuad> quads) {
            return quads.stream()
                        .filter(q->!q.isTinted())
                        .collect(Collectors.toList());
        }
    }
    
    public static class BakedModelPicture extends BakedModelQuadsWrapper {
        
        protected final int index;
        protected final TextureAtlasSprite sprite;
        
        public BakedModelPicture(IBakedModel originalModel, int index, TextureAtlasSprite sprite) {
            super(originalModel);
            this.index = index;
            this.sprite = sprite;
        }
        
        @Override
        protected List<BakedQuad> wrapQuads(List<BakedQuad> quads) {
            return quads.stream()
                        .filter(q->q.getTintIndex() == this.index)
                        .map(ModelUtils::tintless)
                        .collect(Collectors.toList());
        }
        
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.sprite;
        }
    }
    
    public static class PictureRenderType extends RenderType {
        
        protected static final ImmutableList<RenderState> STATES =
                ImmutableList.of(NO_TRANSPARENCY, DIFFUSE_LIGHTING, SMOOTH_SHADE, MIDWAY_ALPHA,
                                 LEQUAL_DEPTH_TEST, CULL, LIGHTMAP, NO_OVERLAY, FOG, NO_LAYERING,
                                 MAIN_TARGET, DEFAULT_TEXTURING, COLOR_DEPTH_WRITE, DEFAULT_LINE);
        
        public PictureRenderType(Picture picture) {
            super(DEF.MOD_ID + ":picture",
                  DefaultVertexFormats.BLOCK,
                  RenderType.cutoutMipped().mode(),
                  RenderType.cutoutMipped().bufferSize(),
                  false,
                  false,
                  ()-> {
                      STATES.forEach(RenderState::setupRenderState);
                      bindTexture(picture);
                  },
                  ()-> {
                      STATES.forEach(RenderState::clearRenderState);
                  });
        }
    }
}
