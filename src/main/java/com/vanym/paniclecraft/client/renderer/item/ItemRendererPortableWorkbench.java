package com.vanym.paniclecraft.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelPortableWorkbench;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererPortableWorkbench extends ItemStackTileEntityRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/portable_workbench.png");
    
    ModelPortableWorkbench model = new ModelPortableWorkbench();
    
    @Override
    public void renderByItem(
            ItemStack stack,
            MatrixStack ms,
            IRenderTypeBuffer buffers,
            int light,
            int overlay) {
        IVertexBuilder vertexer =
                ItemRenderer.getFoilBuffer(buffers, this.model.renderType(TEXTURE), false,
                                           stack.hasFoil());
        ms.pushPose();
        ms.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        ms.translate(0.5F, -0.0625F, -0.5F);
        this.model.renderToBuffer(ms, vertexer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        ms.popPose();
    }
}
