package com.vanym.paniclecraft.core.component.painting;

import java.util.Objects;
import java.util.Optional;

import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class PaintingFrameSideItemHandler implements IItemHandler {
    
    public final TileEntityPaintingFrame frame;
    public final EnumFacing side;
    protected final int index;
    
    public PaintingFrameSideItemHandler(TileEntityPaintingFrame frame, EnumFacing side) {
        this.frame = Objects.requireNonNull(frame);
        this.side = Objects.requireNonNull(side);
        this.index = side.getIndex();
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        return Optional.ofNullable(this.frame.getPicture(this.index))
                       .map(ItemPainting::getPictureAsItem)
                       .orElse(ItemStack.EMPTY);
    }
    
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.frame.getPicture(this.index) != null
            || !this.isItemValid(slot, stack)) {
            return stack;
        }
        if (!simulate) {
            this.createPicture(stack);
        }
        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
    }
    
    protected void createPicture(ItemStack stack) {
        SideUtils.runSync(this.frame.getWorld() != null
            && !this.frame.getWorld().isRemote, this.frame,
                          ()->this.frame.createPicture(this.index, stack));
        this.frame.markForUpdate();
    }
    
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.getStackInSlot(slot);
        if (!simulate) {
            this.clearPicture();
        }
        return stack;
    }
    
    protected void clearPicture() {
        SideUtils.runSync(this.frame.getWorld() != null
            && !this.frame.getWorld().isRemote, this.frame,
                          ()->this.frame.clearPicture(this.index));
        this.frame.markForUpdate();
    }
    
    @Override
    public int getSlots() {
        return 1;
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
    
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof ItemPainting;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.frame, this.side);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof PaintingFrameSideItemHandler) {
            PaintingFrameSideItemHandler otherHandler =
                    (PaintingFrameSideItemHandler)other;
            return this.frame.equals(otherHandler.frame)
                && this.side.equals(otherHandler.side);
        }
        return false;
    }
}
