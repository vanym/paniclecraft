package com.vanym.paniclecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody2;
import com.vanym.paniclecraft.client.renderer.model.ModelCannonBody3;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityCannonRenderer extends TileEntityRenderer<TileEntityCannon> {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/entity/cannon.png");
    
    protected final ModelCannonBody body = new ModelCannonBody();
    protected final ModelCannonBody2 body2 = new ModelCannonBody2();
    protected final ModelCannonBody3 body3 = new ModelCannonBody3();
    
    public TileEntityCannonRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    @Override
    public void render(
            TileEntityCannon tileCannon,
            float partialTicks,
            MatrixStack ms,
            IRenderTypeBuffer buffer,
            int combinedLight,
            int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        ms.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        ms.translate(0.0F, 0.5F, 0.0F);
        IVertexBuilder vertexer = buffer.getBuffer(this.body.renderType(TEXTURE));
        this.body.renderToBuffer(ms, vertexer, combinedLight, combinedOverlay,
                                 1.0F, 1.0F, 1.0F, 1.0F);
        ms.scale(1.2F, 1.2F, 1.2F);
        ms.mulPose(Vector3f.YP.rotationDegrees((float)tileCannon.getDirection()));
        this.body2.renderToBuffer(ms, vertexer, combinedLight, combinedOverlay,
                                  1.0F, 1.0F, 1.0F, 1.0F);
        ms.translate(0.0F, -0.4F, 0.0F);
        ms.mulPose(Vector3f.XP.rotationDegrees(90.0F - (float)tileCannon.getHeight()));
        this.body3.renderToBuffer(ms, vertexer, combinedLight, combinedOverlay,
                                  1.0F, 1.0F, 1.0F, 1.0F);
        ms.popPose();
    }
}
