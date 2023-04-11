package com.vanym.paniclecraft.tileentity;

import javax.annotation.Nullable;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TileEntityCannon extends TileEntityBase implements IInventory, ITickable {
    
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
    
    protected IItemHandler itemHandler;
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_HEIGHT = "Height";
    protected static final String TAG_STRENGTH = "Strength";
    protected static final String TAG_TIMEOUT = "Timeout";
    protected static final String TAG_STACK = "Item";
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTag) {
        return this.writeToNBT(nbtTag, false);
    }
    
    protected NBTTagCompound writeToNBT(NBTTagCompound nbtTag, boolean forClient) {
        nbtTag = super.writeToNBT(nbtTag);
        nbtTag.setDouble(TAG_DIRECTION, this.direction);
        nbtTag.setDouble(TAG_HEIGHT, this.height);
        nbtTag.setDouble(TAG_STRENGTH, this.strength);
        if (forClient) {
            return nbtTag;
        }
        if (this.timeout > 0) {
            nbtTag.setInteger(TAG_TIMEOUT, this.timeout);
        } else {
            nbtTag.removeTag(TAG_TIMEOUT);
        }
        NBTTagCompound itemTag = new NBTTagCompound();
        this.stack.writeToNBT(itemTag);
        nbtTag.setTag(TAG_STACK, itemTag);
        return nbtTag;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTag) {
        super.readFromNBT(nbtTag);
        this.setDirection(nbtTag.getDouble(TAG_DIRECTION));
        this.height = nbtTag.getDouble(TAG_HEIGHT);
        this.strength = nbtTag.getDouble(TAG_STRENGTH);
        this.vector = null;
        this.timeout = nbtTag.getInteger(TAG_TIMEOUT);
        if (nbtTag.hasKey(TAG_STACK, 10)) {
            NBTTagCompound itemTag = nbtTag.getCompoundTag(TAG_STACK);
            this.stack = new ItemStack(itemTag);
        }
    }
    
    @Override
    public void update() {
        if (this.world.isRemote) {
            return;
        }
        this.timeout = Math.min(this.timeout, Core.instance.cannon.config.shootTimeout);
        if (this.timeout <= 0 && !this.stack.isEmpty()) {
            this.shoot(this.stack);
            this.stack = ItemStack.EMPTY;
            this.timeout = Core.instance.cannon.config.shootTimeout;
            this.markDirty();
        }
        if (this.timeout > 0) {
            --this.timeout;
        }
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound(), true);
    }
    
    public void setDirection(double direction) {
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
        if (strength >= 0 && strength <= Core.instance.cannon.config.maxStrength) {
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
    
    protected Vec3d getVector() {
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
        EntityItem entityItem = new EntityItem(
                this.world,
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.4D,
                this.pos.getZ() + 0.5D,
                stack);
        entityItem.setPickupDelay(Core.instance.cannon.config.pickupDelay);
        Vec3d motion = this.getVector();
        entityItem.motionX = motion.x;
        entityItem.motionZ = motion.z;
        entityItem.motionY = motion.y;
        this.world.spawnEntity(entityItem);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos).grow(0.5D);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
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
        return this.stack.splitStack(amount);
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
        if (Core.instance.cannon.config.shootTimeout > 0) {
            this.stack = stack;
            this.markDirty();
        } else if (!this.world.isRemote && !stack.isEmpty()) {
            this.shoot(stack);
        }
    }
    
    @Override
    public String getName() {
        return Core.instance.cannon.blockCannon.getUnlocalizedName() + ".inv";
    }
    
    @Override
    public boolean hasCustomName() {
        return false;
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this == player.world.getTileEntity(this.pos)
            && player.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
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
    public void openInventory(EntityPlayer player) {}
    
    @Override
    public void closeInventory(EntityPlayer player) {}
    
    @Override
    public int getField(int id) {
        return 0;
    }
    
    @Override
    public void setField(int id, int value) {}
    
    @Override
    public int getFieldCount() {
        return 0;
    }
    
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandler == null) {
                this.itemHandler = new InvWrapper(this);
            }
            return (T)this.itemHandler;
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            || super.hasCapability(capability, facing);
    }
}
