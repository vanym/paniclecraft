package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererCannon extends TileEntityItemStackRenderer {
    
    protected double height = 0.0D;
    protected boolean oneshot = false;
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TileEntityCannon tileCannon = new TileEntityCannon();
        tileCannon.setHeight(this.height);
        Core.instance.cannon.tileCannonRenderer.render(tileCannon, 0.0F, 0.0F, 0.0F,
                                                       partialTicks, -1, 0.0F);
        if (this.oneshot) {
            this.height = 0.0D;
            this.oneshot = false;
        }
    }
    
    protected void setHeight(double pitch) {
        this.height = Math.max(0.0D, Math.min(90.0D, MathHelper.wrapDegrees(pitch)));
    }
    
    @SubscribeEvent
    public void preEntityRender(RenderLivingEvent.Pre<EntityLivingBase> event) {
        this.setHeight(event.getEntity().rotationPitch);
    }
    
    @SubscribeEvent
    public void postEntityRender(RenderLivingEvent.Post<EntityLivingBase> event) {
        this.height = 0.0D;
    }
    
    @SubscribeEvent
    public void handRender(RenderSpecificHandEvent event) {
        if (event.getItemStack().getItem() == Core.instance.cannon.itemCannon) {
            this.setHeight(event.getInterpolatedPitch());
            this.oneshot = true;
        }
    }
}
