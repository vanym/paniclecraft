package com.vanym.paniclecraft.plugins.computercraft;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.block.BlockPaintingContainer;
import com.vanym.paniclecraft.core.component.painting.PaintingFrameSideItemHandler;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.SideUtils;

import dan200.computercraft.api.turtle.event.TurtleInventoryEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public class TurtleSuckPaintingFrame {
    
    protected static final ResourceLocation CAPABILITY_ID =
            new ResourceLocation(DEF.MOD_ID, "turtle_suck_paintingframe");
    
    protected final ThreadLocal<Reference<PlayerEntity>> turtlePlayer = new ThreadLocal<>();
    
    protected boolean active = false;
    
    public void enable() {
        this.active = true;
    }
    
    public void disable() {
        this.active = false;
    }
    
    @SubscribeEvent
    protected void attachCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityPaintingFrame) {
            TileEntityPaintingFrame frame = (TileEntityPaintingFrame)event.getObject();
            event.addCapability(CAPABILITY_ID, new PaintingFrameSideItemHandlerProvider(frame));
        }
    }
    
    @SubscribeEvent
    protected void turtleSuck(TurtleInventoryEvent.Suck event) {
        this.turtlePlayer.set(new WeakReference<>(event.getPlayer()));
    }
    
    @Nullable
    protected PlayerEntity getPlayer() {
        return Optional.of(this.turtlePlayer)
                       .map(ThreadLocal::get)
                       .map(Reference::get)
                       .orElse(null);
    }
    
    protected class PaintingFrameSideItemHandlerProvider implements ICapabilityProvider {
        public final TileEntityPaintingFrame frame;
        
        public PaintingFrameSideItemHandlerProvider(TileEntityPaintingFrame frame) {
            this.frame = frame;
        }
        
        protected boolean hasCapability(Capability<?> cap, @Nullable Direction side) {
            return side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                && TurtleSuckPaintingFrame.this.active
                && Arrays.stream(Thread.currentThread().getStackTrace())
                         .limit(16)
                         .map(StackTraceElement::getClassName)
                         .anyMatch(name->name.endsWith("TurtleSuckCommand"));
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (this.hasCapability(cap, side)) {
                return LazyOptional.of(()->(T)new TurtleSuckPaintingFrameSideItemHandler(
                        this.frame,
                        side));
            }
            return LazyOptional.empty();
        }
    }
    
    protected class TurtleSuckPaintingFrameSideItemHandler extends PaintingFrameSideItemHandler {
        
        public TurtleSuckPaintingFrameSideItemHandler(
                TileEntityPaintingFrame frame,
                Direction side) {
            super(frame, side);
        }
        
        @Override
        protected void createPicture(ItemStack stack) {
            PlayerEntity player = TurtleSuckPaintingFrame.this.getPlayer();
            SideUtils.runSync(this.frame.getWorld() != null
                && !this.frame.getWorld().isRemote, this.frame, ()-> {
                    Picture picture = this.frame.createPicture(this.index, stack);
                    if (player != null) {
                        BlockPaintingContainer.rotatePicture(player, picture, this.side, true);
                    }
                });
            this.frame.markForUpdate();
        }
        
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) {
                return ItemStack.EMPTY;
            }
            if (simulate) {
                return this.getStackInSlot(slot);
            }
            PlayerEntity player = TurtleSuckPaintingFrame.this.getPlayer();
            return SideUtils.callSync(this.frame.getWorld() != null
                && !this.frame.getWorld().isRemote, this.frame, ()-> {
                    Picture picture = this.frame.getPicture(this.index);
                    if (player != null) {
                        BlockPaintingContainer.rotatePicture(player, picture, this.side, false);
                    }
                    this.clearPicture();
                    return ItemPainting.getPictureAsItem(picture);
                });
        }
    }
}
