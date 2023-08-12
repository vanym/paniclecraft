package com.vanym.paniclecraft.plugins.computercraft;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.component.painting.PaintingFrameSideItemHandler;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public class TurtleSuckPaintingFrame {
    
    protected static final ResourceLocation CAPABILITY_ID =
            new ResourceLocation(DEF.MOD_ID, "turtle_suck_paintingframe");
    
    @SubscribeEvent
    protected void attachCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame frame = (TileEntityPaintingFrame)event.getObject();
            event.addCapability(CAPABILITY_ID, new PaintingFrameSideItemHandlerProvider(frame));
        }
    }
    
    protected static class PaintingFrameSideItemHandlerProvider implements ICapabilityProvider {
        public final TileEntityPaintingFrame frame;
        
        public PaintingFrameSideItemHandlerProvider(TileEntityPaintingFrame frame) {
            this.frame = frame;
        }
        
        @Override
        public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing) {
            return facing != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && Arrays.stream(Thread.currentThread().getStackTrace())
                         .limit(16)
                         .map(StackTraceElement::getClassName)
                         .anyMatch(name->name.endsWith("TurtleSuckCommand"));
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing facing) {
            if (this.hasCapability(cap, facing)) {
                return (T)new PaintingFrameSideItemHandler(this.frame, facing);
            }
            return null;
        }
    }
}
