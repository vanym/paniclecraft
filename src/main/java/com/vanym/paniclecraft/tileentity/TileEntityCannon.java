package com.vanym.paniclecraft.tileentity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class TileEntityCannon extends TileEntity implements IInventory {
    
    public static double defMaxStrength = 10;
    
    public double maxStrength = defMaxStrength;
    
    public double direction = 0;
    
    public double height = 0;
    
    public double strength = 1;
    
    public ItemStack item;
    
    public Vec3 vector;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setDouble("direction", this.direction);
        par1NBTTagCompound.setDouble("height", this.height);
        par1NBTTagCompound.setDouble("strength", this.strength);
        par1NBTTagCompound.setDouble("maxStrength", this.maxStrength);
        NBTTagCompound nbttag = new NBTTagCompound();
        if (this.item != null) {
            this.item.writeToNBT(nbttag);
        }
        par1NBTTagCompound.setTag("item", nbttag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.direction = par1NBTTagCompound.getDouble("direction");
        this.height = par1NBTTagCompound.getDouble("height");
        this.strength = par1NBTTagCompound.getDouble("strength");
        this.maxStrength = par1NBTTagCompound.getDouble("maxStrength");
        NBTTagCompound nbttag = par1NBTTagCompound.getCompoundTag("item");
        this.item = ItemStack.loadItemStackFromNBT(nbttag);
        this.vector = null;
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.shot(this.item);
            this.item = null;
        }
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound dataTag = new NBTTagCompound();
        this.writeToNBT(dataTag);
        dataTag.removeTag("item");
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, dataTag);
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbtData = packet.func_148857_g();
        this.readFromNBT(nbtData);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            net.minecraft.client.gui.GuiScreen gui =
                    net.minecraft.client.Minecraft.getMinecraft().currentScreen;
            if (gui != null) {
                if (gui instanceof com.vanym.paniclecraft.client.gui.container.GuiCannon) {
                    com.vanym.paniclecraft.client.gui.container.GuiCannon guiCannon =
                            (com.vanym.paniclecraft.client.gui.container.GuiCannon)gui;
                    if (guiCannon.cannon.equals(this)) {
                        guiCannon.checkHeight();
                    }
                }
            }
        }
    }
    
    public void setDirection(double par1) {
        this.direction = par1;
        this.vector = null;
    }
    
    public double getDirection() {
        return this.direction;
    }
    
    public void setHeight(double par1) {
        this.height = par1;
        this.vector = null;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public void setStrength(double par1) {
        this.strength = par1;
        // vector = null;
    }
    
    public double getStrength() {
        return this.strength;
    }
    
    public Vec3 getVector() {
        if (this.vector == null) {
            double hc = Math.cos(Math.toRadians((double)this.height));
            double hs = Math.sin(Math.toRadians((double)this.height));
            int d = (int)this.direction;
            double rd = (Math.sin(Math.toRadians(d)));
            double ld = (Math.cos(Math.toRadians(d)));
            this.vector = Vec3.createVectorHelper(-hc * rd, hs, hc * ld);
        }
        return this.vector;
    }
    
    public void shot(ItemStack shotItem) {
        if (shotItem == null) {
            return;
        }
        EntityItem entityitem = new EntityItem(
                this.worldObj,
                (double)this.xCoord + 0.5D,
                (double)this.yCoord + 0.4D,
                (double)this.zCoord + 0.5D,
                shotItem);
        // entityitem.lifespan = 72000;
        entityitem.delayBeforeCanPickup = 15;
        double s = this.strength;
        Vec3 m = this.getVector();
        entityitem.motionX = m.xCoord * s;
        entityitem.motionZ = m.zCoord * s;
        entityitem.motionY = m.yCoord * s;
        this.worldObj.spawnEntityInWorld(entityitem);
        shotItem = null;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord - 0.5F, (double)this.yCoord + 0.0F,
                                            (double)this.zCoord - 0.5F, (double)this.xCoord + 1.5F,
                                            (double)this.yCoord + 1.5F, (double)this.zCoord + 1.5F);
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
    public ItemStack getStackInSlot(int i) {
        if (i == 0) {
            return this.item;
        }
        return null;
    }
    
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.item != null) {
            ItemStack itemstack;
            
            if (this.item.stackSize <= par2) {
                itemstack = this.item;
                this.item = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.item.splitStack(par2);
                
                if (this.item.stackSize == 0) {
                    this.item = null;
                }
                
                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.item != null) {
            ItemStack itemstack = this.item;
            this.item = null;
            return itemstack;
        } else {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.item = par2ItemStack;
        
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
        
        this.markDirty();
    }
    
    @Override
    public String getInventoryName() {
        return "tile.cannon.inv";
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && entityplayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D,
                                          (double)this.zCoord + 0.5D) <= 64.0D;
    }
    
    public void openChest() {
    }
    
    public void closeChest() {
    }
    
    public boolean isInvNameLocalized() {
        return false;
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }
    
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    @Override
    public void openInventory() {
    }
    
    @Override
    public void closeInventory() {
        
    }
}
