package com.vanym.paniclecraft.client.renderer.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.tileentity.TileEntityPaintingRenderer;
import com.vanym.paniclecraft.client.utils.IconUtils;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.entity.EntityPaintOnBlock;
import com.vanym.paniclecraft.utils.GeometryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityPaintOnBlockRenderer extends Render<EntityPaintOnBlock> {
    
    protected static final TextureAtlasSprite FULL_SPRITE = IconUtils.full(16, 16);
    
    public int renderPictureType = 2;
    
    protected final FaceBakery faceBakery = new FaceBakery();
    protected BlockRendererDispatcher blockRenderer;
    
    protected final Map<Collection<AxisAlignedBB>, IBakedModel> preparedModels;
    
    public EntityPaintOnBlockRenderer() {
        super(Minecraft.getMinecraft().getRenderManager());
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
            this.blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
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
        Profiler theProfiler = null;
        if (world != null && Core.instance.painting.clientConfig.renderProfiling) {
            theProfiler = world.profiler;
        }
        if (theProfiler != null) {
            theProfiler.startSection(DEF.MOD_ID + ":" + EntityPaintOnBlock.IN_MOD_ID);
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        if (this.renderPictureType >= 0) {
            if (theProfiler != null) {
                theProfiler.startSection("picture");
            }
            final double expandBase = 0.0005D;
            final double expandAdjust = 0.0001D;
            final double expandX = expandBase + Math.pow(x / 4, 2) * expandAdjust;
            final double expandY = expandBase + Math.pow(y / 4, 2) * expandAdjust;
            final double expandZ = expandBase + Math.pow(z / 4, 2) * expandAdjust;
            BlockModelRenderer render = this.blockRenderer.getBlockModelRenderer();
            IBlockState state = world.getBlockState(pos);
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
                EnumFacing pside = EnumFacing.getFront(side);
                IBakedModel pictureModel =
                        new TileEntityPaintingRenderer.BakedModelPicture(model, side, sprite);
                buf.setTranslation(x - entityPOB.posX + pside.getFrontOffsetX() * expandX,
                                   y - entityPOB.posY + pside.getFrontOffsetY() * expandY,
                                   z - entityPOB.posZ + pside.getFrontOffsetZ() * expandZ);
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                if (this.renderPictureType > 0) {
                    render.renderModelSmooth(world, pictureModel, state, pos, buf, true, rand);
                } else {
                    render.renderModelFlat(world, pictureModel, state, pos, buf, true, rand);
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
    
    protected IBakedModel getModel(IBlockState state, World world, BlockPos pos) {
        if (this.isUsingCollisionBox(state, world, pos)) {
            return this.getModelByCollisionBox(state, world, pos);
        }
        return this.getModel(state.getBoundingBox(world, pos));
    }
    
    protected boolean isUsingCollisionBox(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        return block instanceof BlockStairs
            || block instanceof BlockFence
            || block instanceof BlockWall
            || block instanceof BlockPane;
    }
    
    protected IBakedModel getModelByCollisionBox(IBlockState state, World world, BlockPos pos) {
        List<AxisAlignedBB> absoluteList = new ArrayList<>();
        AxisAlignedBB absoluteMask = GeometryUtils.getFullBlockBox().offset(pos);
        state.addCollisionBoxToList(world, pos, absoluteMask, absoluteList, null, false);
        List<AxisAlignedBB> list =
                absoluteList.stream()
                            .map(box->box.offset(-pos.getX(), -pos.getY(), -pos.getZ()))
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
        EnumMap<EnumFacing, List<BakedQuad>> quadsMap = new EnumMap<>(EnumFacing.class);
        for (EnumFacing side : EnumFacing.values()) {
            quadsMap.put(side, new ArrayList<>());
        }
        for (BlockPart part : parts) {
            for (Entry<EnumFacing, BlockPartFace> e : part.mapFaces.entrySet()) {
                EnumFacing side = e.getKey();
                BlockPartFace face = e.getValue();
                BakedQuad quad = this.faceBakery.makeBakedQuad(part.positionFrom, part.positionTo,
                                                               face, FULL_SPRITE, side,
                                                               ModelRotation.X0_Y0,
                                                               part.partRotation,
                                                               false, part.shade);
                quadsMap.getOrDefault(face.cullFace, quads).add(quad);
            }
        }
        return new SimpleBakedModel(
                quads,
                quadsMap,
                true,
                false,
                FULL_SPRITE,
                ItemCameraTransforms.DEFAULT,
                ItemOverrideList.NONE);
    }
    
    protected static BlockPart makeBlockPart(AxisAlignedBB box) {
        Vector3f min = new Vector3f((float)box.minX, (float)box.minY, (float)box.minZ);
        Vector3f max = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.maxZ);
        min.scale(16.0F);
        max.scale(16.0F);
        Map<EnumFacing, BlockPartFace> blockPartFaceMap =
                Stream.of(EnumFacing.values())
                      .collect(Collectors.toMap(Function.identity(),
                                                side-> {
                                                    EnumFacing cullFace = null;
                                                    if (GeometryUtils.isTouchingSide(side, box)) {
                                                        cullFace = side;
                                                    }
                                                    int i = side.getIndex();
                                                    BlockFaceUV uv = new BlockFaceUV(null, 0);
                                                    return new BlockPartFace(cullFace, i, "", uv);
                                                }));
        BlockPart part = new BlockPart(min, max, blockPartFaceMap, null, true);
        // fixing vertical sides rotation
        Arrays.stream(EnumFacing.values())
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
