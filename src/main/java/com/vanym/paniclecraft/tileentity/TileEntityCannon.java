package com.vanym.paniclecraft.tileentity;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.utils.NumberUtils;
import com.vanym.paniclecraft.utils.SideUtils;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TileEntityCannon extends TileEntityBase
        implements
            IInventory,
            INamedContainerProvider,
            ITickableTileEntity {
    
    public static final String IN_MOD_ID = "cannon";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final double MAX_HEIGHT = 90.0D;
    public static final double MIN_HEIGHT = 0.0D;
    
    protected double direction = 0.0D;
    protected double height = 0.0D;
    protected double strength = 1.0D;
    
    protected int timeout;
    
    protected ItemStack stack = ItemStack.EMPTY;
    
    protected Vec3d vector;
    
    protected LazyOptional<IItemHandler> itemHandler = LazyOptional.of(()->new InvWrapper(this));
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_HEIGHT = "Height";
    protected static final String TAG_STRENGTH = "Strength";
    protected static final String TAG_TIMEOUT = "Timeout";
    protected static final String TAG_STACK = "Item";
    
    public TileEntityCannon() {
        super(Core.instance.cannon.tileEntityCannon);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT nbtTag) {
        return this.write(nbtTag, false);
    }
    
    protected CompoundNBT write(CompoundNBT nbtTag, boolean forClient) {
        return SideUtils.callSync(this.world != null && !this.world.isRemote,
                                  this, ()->this.writeAsync(nbtTag, forClient));
    }
    
    protected CompoundNBT writeAsync(CompoundNBT nbtTag, boolean forClient) {
        nbtTag = super.write(nbtTag);
        nbtTag.putDouble(TAG_DIRECTION, this.direction);
        nbtTag.putDouble(TAG_HEIGHT, this.height);
        nbtTag.putDouble(TAG_STRENGTH, this.strength);
        if (forClient) {
            return nbtTag;
        }
        if (this.timeout > 0) {
            nbtTag.putInt(TAG_TIMEOUT, this.timeout);
        } else {
            nbtTag.remove(TAG_TIMEOUT);
        }
        CompoundNBT itemTag = new CompoundNBT();
        this.stack.write(itemTag);
        nbtTag.put(TAG_STACK, itemTag);
        return nbtTag;
    }
    
    @Override
    public void read(CompoundNBT nbtTag) {
        SideUtils.runSync(this.world != null && !this.world.isRemote,
                          this, ()->this.readAsync(nbtTag));
    }
    
    public void readAsync(CompoundNBT nbtTag) {
        super.read(nbtTag);
        this.setDirection(nbtTag.getDouble(TAG_DIRECTION));
        this.height = NumberUtils.finite(nbtTag.getDouble(TAG_HEIGHT));
        this.strength = NumberUtils.finite(nbtTag.getDouble(TAG_STRENGTH));
        this.vector = null;
        this.timeout = nbtTag.getInt(TAG_TIMEOUT);
        if (nbtTag.contains(TAG_STACK, 10)) {
            CompoundNBT itemTag = nbtTag.getCompound(TAG_STACK);
            this.stack = ItemStack.read(itemTag);
        }
    }
    
    @Override
    public void tick() {
        if (this.world.isRemote) {
            return;
        }
        this.timeout = Math.min(this.timeout, Core.instance.cannon.shootTimeout.get());
        if (this.timeout <= 0 && !this.stack.isEmpty()) {
            this.shoot(this.stack);
            this.stack = ItemStack.EMPTY;
            this.timeout = Core.instance.cannon.shootTimeout.get();
            this.markDirty();
        }
        if (this.timeout > 0) {
            --this.timeout;
        }
    }
    
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT(), true);
    }
    
    public void setDirection(double direction) {
        direction = NumberUtils.finite(direction);
        direction = MathHelper.wrapDegrees(direction);
        if (direction < 0) {
            direction += 360.0D;
        }
        this.direction = direction;
        this.vector = null;
    }
    
    public double getDirection() {
        return this.direction;
    }
    
    public boolean setHeight(double height) {
        height = MathHelper.wrapDegrees(height);
        if (height >= MIN_HEIGHT && height <= MAX_HEIGHT) {
            this.height = height;
            this.vector = null;
            return true;
        } else {
            return false;
        }
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public boolean setStrength(double strength) {
        if (strength >= 0 && strength <= Core.instance.cannon.maxStrength.get()) {
            this.strength = strength;
            this.vector = null;
            return true;
        } else {
            return false;
        }
    }
    
    public double getStrength() {
        return this.strength;
    }
    
    protected synchronized Vec3d getVector() {
        // expected to be called only on server side,
        // so do synchronized unconditionally
        if (this.vector == null) {
            double heightRadians = Math.toRadians(this.height);
            double hSin = Math.sin(heightRadians);
            double hCos = Math.cos(heightRadians);
            double dirRadians = Math.toRadians(this.direction);
            double dirSin = Math.sin(dirRadians);
            double dirCos = Math.cos(dirRadians);
            this.vector = new Vec3d(-dirSin * hCos, hSin, dirCos * hCos).scale(this.strength);
        }
        return this.vector;
    }
    
    protected void shoot(ItemStack stack) {
        ItemEntity entityItem = new ItemEntity(
                this.world,
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.4D,
                this.pos.getZ() + 0.5D,
                stack);
        entityItem.setPickupDelay(Core.instance.cannon.pickupDelay.get());
        Vec3d motion = this.getVector();
        entityItem.setMotion(motion);
        this.world.addEntity(entityItem);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos).grow(0.5D);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
    
    @Override
    public int getSizeInventory() {
        return 1;
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stack;
    }
    
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return this.stack.split(amount);
    }
    
    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if (!this.stack.isEmpty()) {
            ItemStack itemstack = this.stack;
            this.stack = ItemStack.EMPTY;
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (Core.instance.cannon.shootTimeout.get() > 0) {
            this.stack = stack;
            this.markDirty();
        } else if (!this.world.isRemote && !stack.isEmpty()) {
            this.shoot(stack);
        }
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(
                Core.instance.cannon.blockCannon.getTranslationKey() + ".inventory");
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return this == player.world.getTileEntity(this.pos)
            && player.getDistanceSq(new Vec3d(this.pos).add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }
    
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    
    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
    
    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ContainerCannon(id, inventory, this);
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.removed && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemHandler.cast();
        }
        return super.getCapability(capability, facing);
    }
}
