package com.vanym.paniclecraft.client.renderer.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.client.utils.PictureRender;
import com.vanym.paniclecraft.client.utils.PictureRender.PictureTextureState;
import com.vanym.paniclecraft.client.utils.RenderTypeImpl;
import com.vanym.paniclecraft.client.utils.RenderTypeImpl.RS;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class EntityPaintOnBlockRenderer extends EntityRenderer<EntityPaintOnBlock> {
    
    protected static final TextureAtlasSprite FULL_SPRITE = IconUtils.full(16, 16);
    protected static final TextureAtlasSprite SMALL_SPRITE =
            IconUtils.sub(0, 0, 16, 16, 1024, 1024);
    
    protected final Supplier<Integer> renderPictureTypeSup =
            ()->Core.instance.painting.clientConfig.renderPaintOnBlockPartPictureType;
    
    protected final FaceBakery faceBakery = new FaceBakery();
    protected BlockRendererDispatcher blockRenderer;
    
    protected final Map<Collection<AxisAlignedBB>, IBakedModel> preparedModels;
    
    public EntityPaintOnBlockRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.preparedModels =
                Stream.of(GeometryUtils.getFullBlockBox())
                      .map(Arrays::asList)
                      .collect(Collectors.toMap(Function.identity(), this::makeModel));
    }
    
    @Override
    public void render(
            EntityPaintOnBlock entity,
            float entityYaw,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int packedLight) {
        if (this.blockRenderer == null) {
            this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        }
        this.doRenderPaint(entity, entityYaw, partialTicks, ms, buffer, packedLight);
    }
    
    protected void doRenderPaint(
            EntityPaintOnBlock entityPOB,
            float entityYaw,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int packedLight) {
        World world = this.entityRenderDispatcher.level;
        BlockPos pos = entityPOB.getBlockPos();
        IProfiler theProfiler = null;
        if (world != null && Core.instance.painting.clientConfig.renderProfiling) {
            theProfiler = world.getProfiler();
        }
        if (theProfiler != null) {
            theProfiler.push(DEF.MOD_ID + ":" + EntityPaintOnBlock.IN_MOD_ID);
        }
        if (this.renderPictureTypeSup.get() >= 0) {
            if (theProfiler != null) {
                theProfiler.push("picture");
            }
            Vec3d cam = this.entityRenderDispatcher.camera.getPosition();
            Vec3d expand =
                    calcExpand(new Vec3d(pos).add(GeometryUtils.getCenterVec3d()).subtract(cam));
            BlockModelRenderer render = this.blockRenderer.getModelRenderer();
            BlockState state = world.getBlockState(pos);
            long rand = MathHelper.getSeed(pos);
            IBakedModel model = this.getModel(state, world, pos);
            ms.pushPose();
            GeometryUtils.acceptVec3d(new Vec3d(pos).subtract(GeometryUtils.createVec3d(entityPOB)),
                                      ms::translate);
            for (int side = 0; side < ISidePictureProvider.N; ++side) {
                Picture picture = entityPOB.getPicture(side);
                if (picture == null) {
                    continue;
                }
                Direction pside = Direction.from3DDataValue(side);
                IBakedModel pictureModel =
                        new TileEntityPaintingRenderer.BakedModelPicture(
                                model,
                                side,
                                PictureRender.getIcon(picture));
                RenderType type = createRenderType(picture);
                IVertexBuilder vertexer = buffer.getBuffer(type);
                ms.pushPose();
                GeometryUtils.acceptVec3d(expand.multiply(new Vec3d(pside.getNormal())),
                                          ms::translate);
                ForgeHooksClient.setRenderLayer(type);
                if (this.renderPictureTypeSup.get() > 0) {
                    render.renderModelSmooth(world, pictureModel, state, pos, ms, vertexer,
                                             true, new Random(rand), rand,
                                             OverlayTexture.NO_OVERLAY,
                                             EmptyModelData.INSTANCE);
                } else {
                    render.renderModelFlat(world, pictureModel, state, pos, ms, vertexer,
                                           true, new Random(rand), rand,
                                           OverlayTexture.NO_OVERLAY,
                                           EmptyModelData.INSTANCE);
                }
                ForgeHooksClient.setRenderLayer(null);
                ms.popPose();
                if (theProfiler != null) {
                    theProfiler.pop(); // WxH
                }
            }
            ms.popPose();
            if (theProfiler != null) {
                theProfiler.pop(); // picture
            }
        }
        if (theProfiler != null) {
            theProfiler.pop(); // root
        }
        
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntityPaintOnBlock entity) {
        return null;
    }
    
    protected static double calcExpand(double coord) {
        final double expandBase = 0.0005D;
        final double expandAdjust = 0.0001D;
        return expandBase + Math.pow(coord / 4, 2) * expandAdjust;
    }
    
    protected static Vec3d calcExpand(Vec3d coords) {
        return new Vec3d(calcExpand(coords.x), calcExpand(coords.y), calcExpand(coords.z));
    }
    
    protected IBakedModel getModel(BlockState state, World world, BlockPos pos) {
        if (this.isUsingCollisionBox(state, world, pos)) {
            return this.getModelByCollisionBox(state, world, pos);
        }
        VoxelShape shape = state.getShape(world, pos);
        return this.getModel(shape.isEmpty() ? null : shape.bounds());
    }
    
    protected boolean isUsingCollisionBox(BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        return block instanceof StairsBlock
            || block instanceof FenceBlock
            || block instanceof WallBlock
            || block instanceof PaneBlock;
    }
    
    protected IBakedModel getModelByCollisionBox(BlockState state, World world, BlockPos pos) {
        VoxelShape shape = state.getCollisionShape(world, pos);
        List<AxisAlignedBB> list = shape.toAabbs()
                                        .stream()
                                        .map(box->box.intersect(GeometryUtils.getFullBlockBox()))
                                        .collect(Collectors.toList());
        return this.getModel(list);
    }
    
    protected IBakedModel getModel(AxisAlignedBB box) {
        if (box == null) {
            box = GeometryUtils.getFullBlockBox();
        } else {
            box = box.intersect(GeometryUtils.getFullBlockBox());
        }
        return this.getModel(Arrays.asList(box));
    }
    
    protected IBakedModel getModel(Collection<AxisAlignedBB> boxes) {
        IBakedModel model = this.preparedModels.get(boxes);
        if (model != null) {
            return model;
        }
        return this.makeModel(boxes);
    }
    
    protected IBakedModel makeModel(Collection<AxisAlignedBB> boxes) {
        List<BlockPart> parts = boxes.stream()
                                     .map(EntityPaintOnBlockRenderer::makeBlockPart)
                                     .collect(Collectors.toList());
        List<BakedQuad> quads = new ArrayList<>();
        EnumMap<Direction, List<BakedQuad>> quadsMap = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values()) {
            quadsMap.put(side, new ArrayList<>());
        }
        for (BlockPart part : parts) {
            for (Entry<Direction, BlockPartFace> e : part.faces.entrySet()) {
                Direction side = e.getKey();
                BlockPartFace face = e.getValue();
                // use small sprite here to decrease ratio of the shrink done in makeBakedQuad
                BakedQuad quad = this.faceBakery.bakeQuad(part.from, part.to,
                                                          face, SMALL_SPRITE, side,
                                                          ModelRotation.X0_Y0,
                                                          part.rotation,
                                                          part.shade,
                                                          SMALL_SPRITE.getName());
                quadsMap.getOrDefault(face.cullForDirection, quads).add(quad);
            }
        }
        return new SimpleBakedModel(
                quads,
                quadsMap,
                true,
                false,
                false,
                FULL_SPRITE,
                net.minecraft.client.renderer.model.ItemCameraTransforms.NO_TRANSFORMS,
                ItemOverrideList.EMPTY);
    }
    
    protected static BlockPart makeBlockPart(AxisAlignedBB box) {
        Vector3f min = new Vector3f((float)box.minX, (float)box.minY, (float)box.minZ);
        Vector3f max = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.maxZ);
        min.mul(16.0F);
        max.mul(16.0F);
        Map<Direction, BlockPartFace> blockPartFaceMap =
                Arrays.stream(Direction.values())
                      .collect(Collectors.toMap(Function.identity(),
                                                side-> {
                                                    Direction cullFace = null;
                                                    if (GeometryUtils.isTouchingSide(side, box)) {
                                                        cullFace = side;
                                                    }
                                                    int i = side.get3DDataValue();
                                                    BlockFaceUV uv = new BlockFaceUV(null, 0);
                                                    return new BlockPartFace(cullFace, i, "", uv);
                                                }));
        BlockPart part = new BlockPart(min, max, blockPartFaceMap, null, true);
        // fixing vertical sides rotation
        Arrays.stream(Direction.values())
              .filter(side->side.getAxis().isVertical())
              .map(part.faces::get)
              .map(bpf->bpf.uv.uvs)
              .forEach(uvs-> {
                  for (int i = 0; i < uvs.length; ++i) {
                      uvs[i] = 16.0F - uvs[i];
                  }
              });
        return part;
    }
    
    protected static RenderType createRenderType(Picture picture) {
        // based on RenderType.solid() with DEFAULT_ALPHA, NO_CULL added
        RenderState.TextureState texture = new PictureTextureState(picture);
        return new RenderTypeImpl(
                DEF.MOD_ID + ":picture_entity",
                DefaultVertexFormats.BLOCK,
                GL11.GL_QUADS,
                2097152,
                true,
                false,
                RenderType.State.builder()
                                .setTextureState(texture)
                                .setShadeModelState(RS.SMOOTH_SHADE)
                                .setLightmapState(RS.LIGHTMAP)
                                .setAlphaState(RS.DEFAULT_ALPHA)
                                .setCullState(RS.NO_CULL)
                                .createCompositeState(true));
    }
}
