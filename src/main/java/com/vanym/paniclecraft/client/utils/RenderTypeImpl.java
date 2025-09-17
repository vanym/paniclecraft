package com.vanym.paniclecraft.client.utils;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class RenderTypeImpl extends RenderType.Type {
    
    public RenderTypeImpl(String name,
            VertexFormat format,
            int mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            RenderType.State state) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, state);
        this.hashCode = Objects.hash(this.hashCode, this.outline(), this.isOutline());
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof RenderTypeImpl && this.equals((RenderTypeImpl)obj);
    }
    
    public boolean equals(RenderTypeImpl obj) {
        return Objects.equals(this.state, obj.state)
            && Objects.equals(this.outline(), obj.outline())
            && this.isOutline() == obj.isOutline();
    }
    
    public static class RS {
        public static final TransparencyState NO_TRANSPARENCY = RenderState.NO_TRANSPARENCY;
        public static final TransparencyState ADDITIVE_TRANSPARENCY =
                RenderState.ADDITIVE_TRANSPARENCY;
        public static final TransparencyState LIGHTNING_TRANSPARENCY =
                RenderState.LIGHTNING_TRANSPARENCY;
        public static final TransparencyState GLINT_TRANSPARENCY = RenderState.GLINT_TRANSPARENCY;
        public static final TransparencyState CRUMBLING_TRANSPARENCY =
                RenderState.CRUMBLING_TRANSPARENCY;
        public static final TransparencyState TRANSLUCENT_TRANSPARENCY =
                RenderState.TRANSLUCENT_TRANSPARENCY;
        public static final AlphaState NO_ALPHA = RenderState.NO_ALPHA;
        public static final AlphaState DEFAULT_ALPHA = RenderState.DEFAULT_ALPHA;
        public static final AlphaState MIDWAY_ALPHA = RenderState.MIDWAY_ALPHA;
        public static final ShadeModelState FLAT_SHADE = RenderState.FLAT_SHADE;
        public static final ShadeModelState SMOOTH_SHADE = RenderState.SMOOTH_SHADE;
        public static final TextureState BLOCK_SHEET_MIPPED = RenderState.BLOCK_SHEET_MIPPED;
        public static final TextureState BLOCK_SHEET = RenderState.BLOCK_SHEET;
        public static final TextureState NO_TEXTURE = RenderState.NO_TEXTURE;
        public static final TexturingState DEFAULT_TEXTURING = RenderState.DEFAULT_TEXTURING;
        public static final TexturingState OUTLINE_TEXTURING = RenderState.OUTLINE_TEXTURING;
        public static final TexturingState GLINT_TEXTURING = RenderState.GLINT_TEXTURING;
        public static final TexturingState ENTITY_GLINT_TEXTURING =
                RenderState.ENTITY_GLINT_TEXTURING;
        public static final LightmapState LIGHTMAP = RenderState.LIGHTMAP;
        public static final LightmapState NO_LIGHTMAP = RenderState.NO_LIGHTMAP;
        public static final OverlayState OVERLAY = RenderState.OVERLAY;
        public static final OverlayState NO_OVERLAY = RenderState.NO_OVERLAY;
        public static final DiffuseLightingState DIFFUSE_LIGHTING = RenderState.DIFFUSE_LIGHTING;
        public static final DiffuseLightingState NO_DIFFUSE_LIGHTING =
                RenderState.NO_DIFFUSE_LIGHTING;
        public static final CullState CULL = RenderState.CULL;
        public static final CullState NO_CULL = RenderState.NO_CULL;
        public static final DepthTestState NO_DEPTH_TEST = RenderState.NO_DEPTH_TEST;
        public static final DepthTestState EQUAL_DEPTH_TEST = RenderState.EQUAL_DEPTH_TEST;
        public static final DepthTestState LEQUAL_DEPTH_TEST = RenderState.LEQUAL_DEPTH_TEST;
        public static final WriteMaskState COLOR_DEPTH_WRITE = RenderState.COLOR_DEPTH_WRITE;
        public static final WriteMaskState COLOR_WRITE = RenderState.COLOR_WRITE;
        public static final WriteMaskState DEPTH_WRITE = RenderState.DEPTH_WRITE;
        public static final LayerState NO_LAYERING = RenderState.NO_LAYERING;
        public static final LayerState POLYGON_OFFSET_LAYERING =
                RenderState.POLYGON_OFFSET_LAYERING;
        public static final LayerState VIEW_OFFSET_Z_LAYERING = RenderState.VIEW_OFFSET_Z_LAYERING;
        public static final FogState NO_FOG = RenderState.NO_FOG;
        public static final FogState FOG = RenderState.FOG;
        public static final FogState BLACK_FOG = RenderState.BLACK_FOG;
        public static final TargetState MAIN_TARGET = RenderState.MAIN_TARGET;
        public static final TargetState OUTLINE_TARGET = RenderState.OUTLINE_TARGET;
        public static final TargetState TRANSLUCENT_TARGET = RenderState.TRANSLUCENT_TARGET;
        public static final TargetState PARTICLES_TARGET = RenderState.PARTICLES_TARGET;
        public static final TargetState WEATHER_TARGET = RenderState.WEATHER_TARGET;
        public static final TargetState CLOUDS_TARGET = RenderState.CLOUDS_TARGET;
        public static final TargetState ITEM_ENTITY_TARGET = RenderState.ITEM_ENTITY_TARGET;
        public static final LineState DEFAULT_LINE = RenderState.DEFAULT_LINE;
    }
}
