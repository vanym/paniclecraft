package com.vanym.paniclecraft.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.utils.GeometryUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class TileEntityCannon extends TileEntityBase implements IInventory {
    
    public static final String IN_MOD_ID = "cannon";
    public static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, IN_MOD_ID);
    
    public static final double MAX_HEIGHT = 90.0D;
    public static final double MIN_HEIGHT = 0.0D;
    
    protected double direction = 0.0D;
    protected double height = 0.0D;
    protected double strength = 1.0D;
    
    protected int timeout;
    
    protected ItemStack stack;
    
    protected Vec3 vector;
    
    protected static final String TAG_DIRECTION = "Direction";
    protected static final String TAG_HEIGHT = "Height";
    protected static final String TAG_STRENGTH = "Strength";
    protected static final String TAG_TIMEOUT = "Timeout";
    protected static final String TAG_STACK = "Item";
    
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
        this.writeToNBT(nbtTag, false);
    }
    
    protected void writeToNBT(NBTTagCompound nbtTag, boolean forClient) {
        super.writeToNBT(nbtTag);
        nbtTag.setDouble(TAG_DIRECTION, this.direction);
        nbtTag.setDouble(TAG_HEIGHT, this.height);
        nbtTag.setDouble(TAG_STRENGTH, this.strength);
        if (forClient) {
            return;
        }
        if (this.timeout > 0) {
            nbtTag.setInteger(TAG_TIMEOUT, this.timeout);
        } else {
            nbtTag.removeTag(TAG_TIMEOUT);
        }
        if (this.stack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.stack.writeToNBT(itemTag);
            nbtTag.setTag(TAG_STACK, itemTag);
        } else {
            nbtTag.removeTag(TAG_STACK);
        }
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
            this.stack = ItemStack.loadItemStackFromNBT(nbtTag.getCompoundTag(TAG_STACK));
        } else {
            this.stack = null;
        }
    }
    
    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            return;
        }
        this.timeout = Math.min(this.timeout, Core.instance.cannon.config.shootTimeout);
        if (this.timeout <= 0 && this.stack != null) {
            this.shoot(this.stack);
            this.stack = null;
            this.timeout = Core.instance.cannon.config.shootTimeout;
            this.markDirty();
        }
        if (this.timeout > 0) {
            --this.timeout;
        }
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound dataTag = new NBTTagCompound();
        this.writeToNBT(dataTag, true);
        dataTag.removeTag(TAG_STACK);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, dataTag);
    }
    
    public void setDirection(double direction) {
        direction = MathHelper.wrapAngleTo180_double(direction);
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
        height = MathHelper.wrapAngleTo180_double(height);
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
    
    protected Vec3 getVector() {
        if (this.vector == null) {
            double heightRadians = Math.toRadians(this.height);
            double hSin = Math.sin(heightRadians);
            double hCos = Math.cos(heightRadians);
            double dirRadians = Math.toRadians(this.direction);
            double dirSin = Math.sin(dirRadians);
            double dirCos = Math.cos(dirRadians);
            this.vector = Vec3.createVectorHelper(-dirSin * hCos, hSin, dirCos * hCos);
            this.vector.xCoord *= this.strength;
            this.vector.yCoord *= this.strength;
            this.vector.zCoord *= this.strength;
        }
        return this.vector;
    }
    
    protected void shoot(ItemStack stack) {
        EntityItem entityItem = new EntityItem(
                this.worldObj,
                this.xCoord + 0.5D,
                this.yCoord + 0.4D,
                this.zCoord + 0.5D,
                stack);
        entityItem.delayBeforeCanPickup = Core.instance.cannon.config.pickupDelay;
        Vec3 motion = this.getVector();
        entityItem.motionX = motion.xCoord;
        entityItem.motionZ = motion.zCoord;
        entityItem.motionY = motion.yCoord;
        this.worldObj.spawnEntityInWorld(entityItem);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return GeometryUtils.getFullBlockBox()
                            .expand(0.5D, 0.5D, 0.5D)
                            .getOffsetBoundingBox(this.xCoord, this.yCoord, this.zCoord);
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
    public ItemStack decrStackSize(int slot, int size) {
        if (this.stack == null) {
            return null;
        }
        if (this.stack.stackSize <= size) {
            ItemStack stack = this.stack;
            this.stack = null;
            this.markDirty();
            return stack;
        } else {
            ItemStack stack = this.stack.splitStack(size);
            if (this.stack.stackSize <= 0) {
                this.stack = null;
            }
            this.markDirty();
            return stack;
        }
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.stack != null) {
            ItemStack itemstack = this.stack;
            this.stack = null;
            return itemstack;
        } else {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (Core.instance.cannon.config.shootTimeout > 0) {
            this.stack = stack;
            this.markDirty();
        } else if (!this.worldObj.isRemote && stack != null) {
            this.shoot(stack);
        }
    }
    
    @Override
    public String getInventoryName() {
        return Core.instance.cannon.blockCannon.getUnlocalizedName() + ".inv";
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this == player.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord)
            && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D,
                                    this.zCoord + 0.5D) <= 64.0D;
    }
    
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    @Override
    public void openInventory() {}
    
    @Override
    public void closeInventory() {}
}
