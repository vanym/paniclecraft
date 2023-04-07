package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelPortableWorkbench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererPortableWorkbench extends TileEntityItemStackRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/portable_workbench.png");
    
    ModelPortableWorkbench model = new ModelPortableWorkbench();
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.5F, -0.0625F, -0.5F);
        this.model.render(0.0625F);
    }
}
