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

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.client.utils.IconUtils;
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public void doRender(
            EntityPaintOnBlock entity,
            double x,
            double y,
            double z,
            float entityYaw,
            float partialTicks) {
        if (this.blockRenderer == null) {
            this.blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        }
        TileEntityPaintingRenderer.renderInWorldEnable();
        this.doRenderPaint(entity, x, y, z, entityYaw, partialTicks);
        TileEntityPaintingRenderer.renderInWorldDisable();
    }
    
    protected void doRenderPaint(
            EntityPaintOnBlock entityPOB,
            double x,
            double y,
            double z,
            float entityYaw,
            float partialTicks) {
        World world = this.renderManager.world;
        BlockPos pos = entityPOB.getBlockPos();
        IProfiler theProfiler = null;
        if (world != null && Core.instance.painting.clientConfig.renderProfiling) {
            theProfiler = world.getProfiler();
        }
        if (theProfiler != null) {
            theProfiler.startSection(DEF.MOD_ID + ":" + EntityPaintOnBlock.IN_MOD_ID);
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        if (this.renderPictureTypeSup.get() >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("picture");
            }
            final double expandBase = 0.0005D;
            final double expandAdjust = 0.0001D;
            final double expandX = expandBase + Math.pow(x / 4, 2) * expandAdjust;
            final double expandY = expandBase + Math.pow(y / 4, 2) * expandAdjust;
            final double expandZ = expandBase + Math.pow(z / 4, 2) * expandAdjust;
            BlockModelRenderer render = this.blockRenderer.getBlockModelRenderer();
            BlockState state = world.getBlockState(pos);
            long rand = MathHelper.getPositionRandom(pos);
            IBakedModel model = this.getModel(state, world, pos);
            for (int side = 0; side < ISidePictureProvider.N; ++side) {
                Picture picture = entityPOB.getPicture(side);
                if (picture == null) {
                    continue;
                }
                if (theProfiler != null) {
                    theProfiler.startSection(picture.getWidth() + "x" + picture.getHeight());
                    theProfiler.startSection("bind");
                }
                TextureAtlasSprite sprite = TileEntityPaintingRenderer.bindTexture(picture);
                if (theProfiler != null) {
                    theProfiler.endSection(); // bind
                }
                Direction pside = Direction.byIndex(side);
                IBakedModel pictureModel =
                        new TileEntityPaintingRenderer.BakedModelPicture(model, side, sprite);
                buf.setTranslation(x - entityPOB.posX + pside.getXOffset() * expandX,
                                   y - entityPOB.posY + pside.getYOffset() * expandY,
                                   z - entityPOB.posZ + pside.getZOffset() * expandZ);
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                if (this.renderPictureTypeSup.get() > 0) {
                    render.renderModelSmooth(world, pictureModel, state, pos,
                                             buf, true, new Random(rand), rand,
                                             EmptyModelData.INSTANCE);
                } else {
                    render.renderModelFlat(world, pictureModel, state, pos,
                                           buf, true, new Random(rand), rand,
                                           EmptyModelData.INSTANCE);
                }
                tessellator.draw();
                if (theProfiler != null) {
                    theProfiler.endSection(); // WxH
                }
            }
            if (theProfiler != null) {
                theProfiler.endSection(); // picture
            }
        }
        buf.setTranslation(0.0D, 0.0D, 0.0D);
        if (theProfiler != null) {
            theProfiler.endSection(); // root
        }
        
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityPaintOnBlock entity) {
        return null;
    }
    
    protected IBakedModel getModel(BlockState state, World world, BlockPos pos) {
        if (this.isUsingCollisionBox(state, world, pos)) {
            return this.getModelByCollisionBox(state, world, pos);
        }
        return this.getModel(state.getShape(world, pos).getBoundingBox());
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
        List<AxisAlignedBB> list = shape.toBoundingBoxList()
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
    
    @SuppressWarnings("deprecation")
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
            for (Entry<Direction, BlockPartFace> e : part.mapFaces.entrySet()) {
                Direction side = e.getKey();
                BlockPartFace face = e.getValue();
                // use small sprite here to decrease ratio of the shrink done in makeBakedQuad
                BakedQuad quad = this.faceBakery.makeBakedQuad(part.positionFrom, part.positionTo,
                                                               face, SMALL_SPRITE, side,
                                                               ModelRotation.X0_Y0,
                                                               part.partRotation,
                                                               part.shade);
                quadsMap.getOrDefault(face.cullFace, quads).add(quad);
            }
        }
        return new SimpleBakedModel(
                quads,
                quadsMap,
                true,
                false,
                FULL_SPRITE,
                net.minecraft.client.renderer.model.ItemCameraTransforms.DEFAULT,
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
                                                    int i = side.getIndex();
                                                    BlockFaceUV uv = new BlockFaceUV(null, 0);
                                                    return new BlockPartFace(cullFace, i, "", uv);
                                                }));
        BlockPart part = new BlockPart(min, max, blockPartFaceMap, null, true);
        // fixing vertical sides rotation
        Arrays.stream(Direction.values())
              .filter(side->side.getAxis().isVertical())
              .map(part.mapFaces::get)
              .map(bpf->bpf.blockFaceUV.uvs)
              .forEach(uvs-> {
                  for (int i = 0; i < uvs.length; ++i) {
                      uvs[i] = 16.0F - uvs[i];
                  }
              });
        return part;
    }
}
